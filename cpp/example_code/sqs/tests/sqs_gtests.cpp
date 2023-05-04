/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "sqs_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/utils/UUID.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/DeleteQueueRequest.h>
#include <aws/sqs/model/GetQueueUrlRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/SendMessageRequest.h>
#include <aws/sqs/model/GetQueueAttributesRequest.h>

Aws::SDKOptions AwsDocTest::SQS_GTests::s_options;
Aws::String AwsDocTest::SQS_GTests::s_cachedQueueUrl;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::SQS_GTests::s_clientConfig;

void AwsDocTest::SQS_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::SQS_GTests::TearDownTestSuite() {
    if (!s_cachedQueueUrl.empty()) {
        deleteQueueWithUrl(s_cachedQueueUrl);
        s_cachedQueueUrl.clear();
    }

    ShutdownAPI(s_options);
}

void AwsDocTest::SQS_GTests::SetUp() {
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

void AwsDocTest::SQS_GTests::TearDown() {
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

Aws::String AwsDocTest::SQS_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::SQS_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

bool AwsDocTest::SQS_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}

Aws::String AwsDocTest::SQS_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

bool AwsDocTest::SQS_GTests::deleteQueueWithName(const Aws::String &name) {
    Aws::String queueURL = getQueueUrl(name);
    bool result = false;
    if (!queueURL.empty()) {
        result = deleteQueueWithUrl(queueURL);
    }

    return result;
}

Aws::String AwsDocTest::SQS_GTests::createQueue(const Aws::String &name) {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);

    Aws::SQS::Model::CreateQueueRequest request;
    request.SetQueueName(name);

    const Aws::SQS::Model::CreateQueueOutcome outcome = sqsClient.CreateQueue(request);
    Aws::String queueUrl;
    if (outcome.IsSuccess()) {
        queueUrl = outcome.GetResult().GetQueueUrl();
    }
    else {
        std::cerr << "Error creating queue " << name << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return queueUrl;
}

Aws::String AwsDocTest::SQS_GTests::getCachedQueueUrl() {
    if (s_cachedQueueUrl.empty()) {
        s_cachedQueueUrl = createQueue(uuidName("test_cached"));
        if (!s_cachedQueueUrl.empty()) // Have one message in queue.
        {
            sendMessage(s_cachedQueueUrl, "initial message");
        }
    }

    return s_cachedQueueUrl;
}

Aws::String AwsDocTest::SQS_GTests::getMessageReceiptHandle() {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);
    Aws::String queueUrl = getCachedQueueUrl();
    Aws::String messageReceiptHandle;
    if (!queueUrl.empty()) {
        sendMessage(queueUrl, "test message");
        Aws::SQS::Model::ReceiveMessageRequest request;
        request.SetQueueUrl(queueUrl);
        request.SetMaxNumberOfMessages(1);
        request.SetWaitTimeSeconds(1);

        const Aws::SQS::Model::ReceiveMessageOutcome outcome = sqsClient.ReceiveMessage(
                request);
        if (outcome.IsSuccess()) {

            const Aws::Vector<Aws::SQS::Model::Message> &messages =
                    outcome.GetResult().GetMessages();
            if (!messages.empty()) {
                messageReceiptHandle = messages[0].GetReceiptHandle();
            }
            else {
                std::cerr
                        << "getMessageReceiptHandle No messages received from queue "
                        << queueUrl <<
                        std::endl;

            }
        }
        else {
            std::cerr << "Error receiving message from queue " << queueUrl << ": "
                      << outcome.GetError().GetMessage() << std::endl;
        }


    }

    return messageReceiptHandle;
}

Aws::String AwsDocTest::SQS_GTests::getQueueUrl(const Aws::String &name) {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);
    Aws::String queueURL;
    Aws::SQS::Model::GetQueueUrlRequest request;
    request.SetQueueName(name);

    const Aws::SQS::Model::GetQueueUrlOutcome outcome = sqsClient.GetQueueUrl(
            request);
    if (outcome.IsSuccess()) {
        queueURL = outcome.GetResult().GetQueueUrl();
    }
    else {
        std::cerr << "Error getting url for queue " << name << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return queueURL;

}

bool AwsDocTest::SQS_GTests::deleteQueueWithUrl(const Aws::String &queueUrl) {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);
    Aws::SQS::Model::DeleteQueueRequest request;
    request.SetQueueUrl(queueUrl);

    const Aws::SQS::Model::DeleteQueueOutcome outcome = sqsClient.DeleteQueue(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting queue " << queueUrl << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

Aws::String AwsDocTest::SQS_GTests::getQueueArn(const Aws::String &queueUrl) {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);
    Aws::SQS::Model::GetQueueAttributesRequest request;
    request.SetQueueUrl(queueUrl);
    request.AddAttributeNames(Aws::SQS::Model::QueueAttributeName::QueueArn);

    Aws::SQS::Model::GetQueueAttributesOutcome outcome = sqsClient.GetQueueAttributes(
            request);

    Aws::String queueArn;
    if (outcome.IsSuccess()) {
        const Aws::Map<Aws::SQS::Model::QueueAttributeName, Aws::String> &attributes =
                outcome.GetResult().GetAttributes();
        const auto &iter = attributes.find(
                Aws::SQS::Model::QueueAttributeName::QueueArn);
        if (iter != attributes.end()) {
            queueArn = iter->second;
        }
        else {
            std::cerr
                    << "Error ARN attribute not returned by GetQueueAttribute."
                    << std::endl;
        }
    }
    else {
        std::cerr << "Error getQueueARN " << outcome.GetError().GetMessage()
                  << std::endl;
    }
    return queueArn;
}

bool AwsDocTest::SQS_GTests::sendMessage(const Aws::String &queueUrl,
                                         const Aws::String &messageText) {
    Aws::SQS::SQSClient sqsClient(*s_clientConfig);
    Aws::SQS::Model::SendMessageRequest request;
    request.SetQueueUrl(queueUrl);
    request.SetMessageBody(messageText);

    const Aws::SQS::Model::SendMessageOutcome outcome = sqsClient.SendMessage(
            request);
    if (!outcome.IsSuccess()) {
        std::cerr << "getMessageReceiptHandle error sending message to "
                  << queueUrl << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
