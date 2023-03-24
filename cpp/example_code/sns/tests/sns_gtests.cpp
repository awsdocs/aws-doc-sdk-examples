/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "sns_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/DeleteTopicRequest.h>
#include <aws/core/utils/UUID.h>


Aws::SDKOptions AwsDocTest::SNS_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::SNS_GTests::s_clientConfig;
Aws::String AwsDocTest::SNS_GTests::s_stashedTopicARN;

void AwsDocTest::SNS_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::SNS_GTests::TearDownTestSuite() {
    if (!s_stashedTopicARN.empty()) {
        deleteTopic(s_stashedTopicARN);
        s_stashedTopicARN.clear();
    }
    ShutdownAPI(s_options);

}

void AwsDocTest::SNS_GTests::SetUp() {
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

void AwsDocTest::SNS_GTests::TearDown() {
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

Aws::String AwsDocTest::SNS_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::SNS_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::SNS_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

bool AwsDocTest::SNS_GTests::deleteTopic(const Aws::String &topicARN) {
    Aws::SNS::SNSClient snsClient(*s_clientConfig);

    Aws::SNS::Model::DeleteTopicRequest request;
    request.SetTopicArn(topicARN);

    const Aws::SNS::Model::DeleteTopicOutcome outcome = snsClient.DeleteTopic(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting topic " << topicARN << ":" <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::SNS_GTests::createTopic(Aws::String &topicARN) {
    Aws::SNS::SNSClient snsClient(*s_clientConfig);

    Aws::SNS::Model::CreateTopicRequest request;
    Aws::String topicName = uuidName("gtests_topic");
    request.SetName(topicName);

    const Aws::SNS::Model::CreateTopicOutcome outcome = snsClient.CreateTopic(request);

    if (outcome.IsSuccess()) {
        topicARN = outcome.GetResult().GetTopicArn();
    }
    else {
        std::cerr << "Error creating topic " << topicName << ":" <<
                  outcome.GetError().GetMessage() << std::endl;
        topicARN.clear();
    }

    return outcome.IsSuccess();
}

Aws::String AwsDocTest::SNS_GTests::getStashedTopicARN() {
    if (s_stashedTopicARN.empty()) {
        createTopic(s_stashedTopicARN);
    }
    return s_stashedTopicARN;
}

Aws::String AwsDocTest::SNS_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}


int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}
