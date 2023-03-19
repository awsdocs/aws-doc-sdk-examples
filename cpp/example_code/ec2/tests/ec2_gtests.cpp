/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ec2_gtests.h"
#include <thread>
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/utils/UUID.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/AllocateAddressRequest.h>
#include <aws/ec2/model/CreateKeyPairRequest.h>
#include <aws/ec2/model/CreateSecurityGroupRequest.h>
#include <aws/ec2/model/DeleteKeyPairRequest.h>
#include <aws/ec2/model/DeleteSecurityGroupRequest.h>
#include <aws/ec2/model/DescribeInstancesRequest.h>
#include <aws/ec2/model/DescribeSecurityGroupsRequest.h>
#include <aws/ec2/model/ReleaseAddressRequest.h>
#include <aws/ec2/model/RunInstancesRequest.h>
#include <aws/ec2/model/TerminateInstancesRequest.h>
#include "ec2_samples.h"

Aws::SDKOptions AwsDocTest::EC2_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::EC2_GTests::s_clientConfig;
Aws::String AwsDocTest::EC2_GTests::s_instanceID;
Aws::String AwsDocTest::EC2_GTests::s_vpcID;


void AwsDocTest::EC2_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::EC2_GTests::TearDownTestSuite() {
    if (!s_instanceID.empty()) {
        terminateInstance(s_instanceID);
        s_instanceID.clear();
    }

    ShutdownAPI(s_options);
}

void AwsDocTest::EC2_GTests::SetUp() {
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, an infinite loop occurs when looping for a result on an empty buffer.
    std::cin.exceptions(std::ios_base::badbit);
}

void AwsDocTest::EC2_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        std::cin.exceptions(std::ios_base::goodbit);
        m_savedInBuffer = nullptr;
    }
}

Aws::String AwsDocTest::EC2_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::EC2_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

bool AwsDocTest::EC2_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

Aws::String AwsDocTest::EC2_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

bool AwsDocTest::EC2_GTests::releaseIPAddress(const Aws::String &allocationID) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);

    Aws::EC2::Model::ReleaseAddressRequest request;
    request.SetAllocationId(allocationID);

    auto outcome = ec2Client.ReleaseAddress(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to release Elastic IP address " <<
                  allocationID << ":" << outcome.GetError().GetMessage() <<
                  std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::EC2_GTests::terminateInstance(const Aws::String &instanceID) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);

    Aws::EC2::Model::TerminateInstancesRequest request;
    request.SetInstanceIds({instanceID});

    Aws::EC2::Model::TerminateInstancesOutcome outcome =
            ec2Client.TerminateInstances(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to terminate ec2 instance " << instanceID <<
                  ", " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

Aws::String AwsDocTest::EC2_GTests::getAmiID() {
    Aws::String result;

    if (s_clientConfig->region == "us-east-1") {
        result = "ami-0dfcb1ef8550277af";
    }
    else {
        std::cerr << "EC2_GTests::getAmiID no amiID specified for the region "
                  << s_clientConfig->region << std::endl;
    }

    return result;
}

Aws::String AwsDocTest::EC2_GTests::createInstance() {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::RunInstancesRequest runRequest;
    const Aws::String amiID = getAmiID();
    runRequest.SetImageId(amiID);
    runRequest.SetInstanceType(Aws::EC2::Model::InstanceType::t1_micro);
    runRequest.SetMinCount(1);
    runRequest.SetMaxCount(1);

    Aws::EC2::Model::RunInstancesOutcome runOutcome = ec2Client.RunInstances(
            runRequest);
    Aws::String instanceID;
    if (!runOutcome.IsSuccess()) {
        std::cerr << "Failed to launch ec2 instance  based on ami " << amiID
                  << ":" << runOutcome.GetError().GetMessage() << std::endl;
    }
    else {
        const Aws::Vector<Aws::EC2::Model::Instance> &instances = runOutcome.GetResult().GetInstances();
        if (instances.empty()) {
            std::cerr << "Failed to launch ec2 instance  based on ami " <<
                      amiID << ":" <<
                      runOutcome.GetError().GetMessage() << std::endl;
        }
        else {
            instanceID = instances[0].GetInstanceId();
        }
    }

    return instanceID;
}

Aws::String AwsDocTest::EC2_GTests::getCachedInstanceID() {
    if (s_instanceID.empty()) {
        Aws::String instanceID = createInstance();

        Aws::EC2::Model::InstanceStateName instanceStateName =
                waitWhileInstanceInState(instanceID,
                                         Aws::EC2::Model::InstanceStateName::pending);

        if (instanceStateName == Aws::EC2::Model::InstanceStateName::running) {
            s_instanceID = instanceID;
        }
        else {
            std::cerr << "Error starting instance, instanceStateName '"
                      << Aws::EC2::Model::InstanceStateNameMapper::GetNameForInstanceStateName(
                              instanceStateName)
                      << "'" << std::endl;
            terminateInstance(instanceID);
        }
    }

    return s_instanceID;
}


Aws::EC2::Model::InstanceStateName
AwsDocTest::EC2_GTests::waitWhileInstanceInState(const Aws::String &instanceID,
                                                 Aws::EC2::Model::InstanceStateName waitState) {
    Aws::EC2::Model::InstanceStateName instanceStateName;
    int count = 0;
    do {
        ++count;
        std::this_thread::sleep_for(std::chrono::seconds(1));
        instanceStateName = getInstanceState(instanceID);
    } while ((count < 600) &&
             (instanceStateName == waitState));

    return instanceStateName;
}


Aws::EC2::Model::InstanceStateName
AwsDocTest::EC2_GTests::getInstanceState(const Aws::String &instanceID) {
    Aws::EC2::Model::InstanceStateName instanceState = Aws::EC2::Model::InstanceStateName::NOT_SET;
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::DescribeInstancesRequest request;
    request.SetInstanceIds({instanceID});
    auto outcome = ec2Client.DescribeInstances(request);
    if (outcome.IsSuccess()) {
        if (!outcome.GetResult().GetReservations().empty() &&
            !outcome.GetResult().GetReservations()[0].GetInstances().empty()) {
            instanceState = outcome.GetResult().GetReservations()[0].GetInstances()[0].GetState().GetName();
        }
        else {
            std::cerr << "EC2_GTests::getInstanceState no instance returned."
                      << std::endl;
        }
    }
    else {
        std::cerr << "EC2_GTests::getInstanceState error "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return instanceState;
}

bool AwsDocTest::EC2_GTests::createKeyPair(const Aws::String &keyPairName) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::CreateKeyPairRequest request;
    request.SetKeyName(keyPairName);

    Aws::EC2::Model::CreateKeyPairOutcome outcome = ec2Client.CreateKeyPair(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "EC2_GTests::createKeyPair: Failed to create key pair:" <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::EC2_GTests::deleteKeyPair(const Aws::String &keyPairName) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::DeleteKeyPairRequest request;

    request.SetKeyName(keyPairName);
    const Aws::EC2::Model::DeleteKeyPairOutcome outcome = ec2Client.DeleteKeyPair(
            request);

    if (!outcome.IsSuccess()) {
        std::cerr << "EC2_GTests::deleteKeyPair - Failed to delete key pair "
                  << keyPairName <<
                  ":" << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

Aws::String AwsDocTest::EC2_GTests::createSecurityGroup(const Aws::String &groupName) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::CreateSecurityGroupRequest request;

    request.SetGroupName(groupName);
    request.SetDescription("test group");
    request.SetVpcId(getVpcID());

    const Aws::EC2::Model::CreateSecurityGroupOutcome outcome =
            ec2Client.CreateSecurityGroup(request);

    Aws::String groupID;
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to create security group:" <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        groupID = outcome.GetResult().GetGroupId();
    }

    return groupID;
}

bool AwsDocTest::EC2_GTests::deleteSecurityGroup(const Aws::String &groupID) {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::DeleteSecurityGroupRequest request;

    request.SetGroupId(groupID);
    auto outcome = ec2Client.DeleteSecurityGroup(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to delete security group " << groupID <<
                  ":" << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

Aws::String AwsDocTest::EC2_GTests::getVpcID() {
    if (s_vpcID.empty()) {
        Aws::EC2::EC2Client ec2Client(*s_clientConfig);
        Aws::EC2::Model::DescribeSecurityGroupsRequest request;

        Aws::String nextToken;
        bool done = false;
        Aws::String vpcID;
        while (!done) {
            if (!nextToken.empty()) {
                request.SetNextToken(nextToken);
            }

            auto outcome = ec2Client.DescribeSecurityGroups(request);

            if (outcome.IsSuccess()) {

                const std::vector<Aws::EC2::Model::SecurityGroup> &securityGroups =
                        outcome.GetResult().GetSecurityGroups();

                for (const auto &securityGroup: securityGroups) {
                    vpcID = securityGroup.GetVpcId();
                    if (securityGroup.GetGroupName() == "default") {
                        done = true;
                        break;
                    }
                }
            }
            else {
                std::cerr << "Failed to describe security groups:" <<
                          outcome.GetError().GetMessage() << std::endl;
                done = true;
            }

            nextToken = outcome.GetResult().GetNextToken();
            done = done || !nextToken.empty();
        }

        s_vpcID = vpcID;
    }

    return s_vpcID;
}

Aws::String AwsDocTest::EC2_GTests::allocateIPAddress() {
    Aws::EC2::EC2Client ec2Client(*s_clientConfig);
    Aws::EC2::Model::AllocateAddressRequest request;
    request.SetDomain(Aws::EC2::Model::DomainType::vpc);

    const Aws::EC2::Model::AllocateAddressOutcome outcome =
            ec2Client.AllocateAddress(request);
    Aws::String allocationID;
    if (!outcome.IsSuccess()) {
        std::cerr
                << "EC2_GTests::allocateIPAddress: failed to allocate Elastic IP address:"
                << outcome.GetError().GetMessage() << std::endl;
    }
    else {
        allocationID = outcome.GetResult().GetAllocationId();
    }

    return allocationID;
}

int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}
