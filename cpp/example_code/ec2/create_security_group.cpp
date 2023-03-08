/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

//snippet-start:[ec2.cpp.create_security_group.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateSecurityGroupRequest.h>
#include <aws/ec2/model/CreateSecurityGroupResponse.h>
//snippet-end:[ec2.cpp.create_security_group.inc]
//snippet-start:[ec2.cpp.configure_security_group.inc]
#include <aws/ec2/model/AuthorizeSecurityGroupIngressRequest.h>
//snippet-end:[ec2.cpp.configure_security_group.inc]
#include <iostream>
#include "ec2_samples.h"

namespace AwsDoc {
    namespace EC2 {
        //! Build a sample ingress rule.
        /*!
          \sa BuildSampleIngressRule()
          \param authorize_request: An 'AuthorizeSecurityGroupIngressRequest' instance.
          \return void:
         */
        void BuildSampleIngressRule(
                Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest &authorize_request);
    } // EC2 {
} // AwsDoc

//! Create a security group.
/*!
  \sa CreateSecurityGroup()
  \param groupName: A security group name.
  \param description: A description.
  \param vpcID: A virtual private cloud (VPC) ID.
  \param groupIDResult: A string to receive the group ID.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::CreateSecurityGroup(const Aws::String &groupName,
                                      const Aws::String &description,
                                      const Aws::String &vpcID,
                                      Aws::String &groupIDResult,
                                      const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.create_security_group.code]
    // snippet-start:[cpp.example_code.ec2.create_security_group.client]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    // snippet-end:[cpp.example_code.ec2.create_security_group.client]
    // snippet-start:[cpp.example_code.ec2.CreateSecurityGroup]
    Aws::EC2::Model::CreateSecurityGroupRequest request;

    request.SetGroupName(groupName);
    request.SetDescription(description);
    request.SetVpcId(vpcID);

    const Aws::EC2::Model::CreateSecurityGroupOutcome outcome =
            ec2Client.CreateSecurityGroup(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to create security group:" <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }

    std::cout << "Successfully created security group named " << groupName <<
              std::endl;
    // snippet-end:[ec2.cpp.create_security_group.code]
    // snippet-end:[cpp.example_code.ec2.CreateSecurityGroup]

    groupIDResult = outcome.GetResult().GetGroupId();

    // snippet-start:[ec2.cpp.configure_security_group01.code]
    Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest authorizeRequest;

    authorizeRequest.SetGroupName(groupName);
    // snippet-end:[ec2.cpp.configure_security_group01.code]

    BuildSampleIngressRule(authorizeRequest);

    // snippet-start:[ec2.cpp.configure_security_group03.code]
    const Aws::EC2::Model::AuthorizeSecurityGroupIngressOutcome authorizeOutcome =
            ec2Client.AuthorizeSecurityGroupIngress(authorizeRequest);

    if (!authorizeOutcome.IsSuccess()) {
        std::cerr << "Failed to set ingress policy for security group " <<
                  groupName << ":" << authorizeOutcome.GetError().GetMessage() <<
                  std::endl;
        return false;
    }

    std::cout << "Successfully added ingress policy to security group " <<
              groupName << std::endl;
    // snippet-end:[ec2.cpp.configure_security_group03.code]

    return true;
}

//! Build a sample ingress rule.
/*!
  \sa BuildSampleIngressRule()
  \param authorize_request: An 'AuthorizeSecurityGroupIngressRequest' instance.
  \return void:
 */
void AwsDoc::EC2::BuildSampleIngressRule(
        Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest &authorize_request) {
    // snippet-start:[ec2.cpp.configure_security_group02.code]
    Aws::EC2::Model::IpRange ip_range;
    ip_range.SetCidrIp("0.0.0.0/0");

    Aws::EC2::Model::IpPermission permission1;
    permission1.SetIpProtocol("tcp");
    permission1.SetToPort(80);
    permission1.SetFromPort(80);
    permission1.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission1);

    Aws::EC2::Model::IpPermission permission2;
    permission2.SetIpProtocol("tcp");
    permission2.SetToPort(22);
    permission2.SetFromPort(22);
    permission2.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission2);
    // snippet-end:[ec2.cpp.configure_security_group02.code]
}


/*
 *
 *  main function
 *
 *  Usage: run_create_security_group <group_name> <group_description> <vpc_id>
 *
 *  Prerequisites: A VPC ID.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_create_security_group <group_name> " <<
                  "<group_description> <vpc_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String group_name = argv[1];
        Aws::String group_desc = argv[2];
        Aws::String vpc_id = argv[3];
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::String groupIDResult;
        AwsDoc::EC2::CreateSecurityGroup(group_name, group_desc, vpc_id,
                                         groupIDResult, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

