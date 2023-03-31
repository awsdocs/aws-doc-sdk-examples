/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/GetTopicAttributesRequest.h>
#include <aws/sns/model/ListTopicsRequest.h>
#include <aws/sns/model/PublishRequest.h>
#include <aws/sns/model/SubscribeRequest.h>
#include <aws/sns/SNSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/DeleteMessageBatchRequest.h>
#include <aws/sqs/model/GetQueueAttributesRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/SQSClient.h>
#include "sns_samples.h"

namespace AwsDoc {
    namespace SNS {
static const char TOPIC_NAME[] = "getting_started_topic.fifo";
        static const char QUEUE_NAME[] = "getting_started_queue.fifo";
    } // namespace SNS
} // namespace AwsDoc

bool AwsDoc::SNS::gettingStartedWithSNSTopics(const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SNS::SNSClient client(clientConfiguration);
    // Create a new topic. This call will succeed even if a topic with the same name was
    // already created.
    Aws::String topicARN;
    {
        Aws::SNS::Model::CreateTopicRequest request;

        request.SetName(TOPIC_NAME);
        request.AddAttributes("FifoTopic", "true");
        request.AddAttributes("ContentBasedDeduplication", "true");

        Aws::SNS::Model::CreateTopicOutcome outcome = client.CreateTopic(request);

        if (outcome.IsSuccess()) {
            std::cout << "SNS::CreateTopic was successful." << std::endl;
            topicARN = outcome.GetResult().GetTopicArn();
        }
        else {
            std::cerr << "Error with SNS::CreateTopic. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    // List topics.
    Aws::Vector<Aws::SNS::Model::Topic> topics;
    {
        Aws::String nextToken; // Next token is used to handle a paginated response.
        do {
            Aws::SNS::Model::ListTopicsRequest request;

            if (!nextToken.empty()) {
                request.SetNextToken(nextToken);
            }

            const Aws::SNS::Model::ListTopicsOutcome outcome =
                    client.ListTopics(request);

            if (outcome.IsSuccess()) {
                const Aws::Vector<Aws::SNS::Model::Topic> newTopics = outcome.GetResult().GetTopics();
                topics.insert(topics.end(), newTopics.begin(), newTopics.end());
            }
            else {
                std::cerr << "Error listing topics " << outcome.GetError().GetMessage() <<
                          std::endl;
                return false;
            }

            nextToken = outcome.GetResult().GetNextToken();
        } while (!nextToken.empty());

    }
    std::cout << "Topics list:" << std::endl;
    for (auto const &topic: topics) {
        std::cout << "  * " << topic.GetTopicArn() << std::endl;
    }

    // Get topic attributes.
    {
        Aws::SNS::Model::GetTopicAttributesRequest request;
        request.SetTopicArn(topicARN);

        Aws::SNS::Model::GetTopicAttributesOutcome outcome = client.GetTopicAttributes(request);

        if (outcome.IsSuccess()) {
            std::cout << "Here are the attributes for the topic with the ARN '" << topicARN
            << "'." << std::endl;
            const Aws::Map<Aws::String, Aws::String>& attributes = outcome.GetResult().GetAttributes();
            for (auto &entry : attributes)
            {
                std::cout <<  "  " << entry.first << " : " << entry.second << std::endl;
            }
        }
        else {
            std::cerr << "Error with SNS::GetTopicAttributes. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    // Create an SQS queue.
    Aws::String queueURL;
    {
        Aws::SQS::Model::CreateQueueRequest request;
        request.SetQueueName(QUEUE_NAME);
        request.AddAttributes(Aws::SQS::Model::QueueAttributeName::FifoQueue, "true");

        Aws::SQS::Model::CreateQueueOutcome outcome = sqsClient.CreateQueue(request);

        if (outcome.IsSuccess()) {
            std::cout << "SQS::CreateQueue was successful." << std::endl;
            queueURL = outcome.GetResult().GetQueueUrl();
        }
        else {
            std::cerr << "Error with SQS::CreateQueue. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    // Get the queue ARN attribute.
    Aws::String queueARN;
    {
        Aws::SQS::Model::GetQueueAttributesRequest request;
        request.SetQueueUrl(queueURL);
        request.AddAttributeNames(Aws::SQS::Model::QueueAttributeName::QueueArn);

        Aws::SQS::Model::GetQueueAttributesOutcome outcome = sqsClient.GetQueueAttributes(request);

        if (outcome.IsSuccess()) {
            std::cout << "SQS::GetQueueAttributes was successful." << std::endl;
            const Aws::Map<Aws::SQS::Model::QueueAttributeName, Aws::String>& attributes =
                    outcome.GetResult().GetAttributes();
            const auto& iter = attributes.find(Aws::SQS::Model::QueueAttributeName::QueueArn);
            if (iter != attributes.end())
            {
                queueARN = iter->second;
            }
            else{
                std::cerr << "Error ARN attribute not returned by GetQueueAttribute." << std::endl;
                return false;
            }
        }
        else {
            std::cerr << "Error with SQS::GetQueueAttributes. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
    
    // Subscribe the queue.
    {
        Aws::SNS::Model::SubscribeRequest request;
        request.SetTopicArn(topicARN);
        request.SetProtocol("sqs");
        request.SetEndpoint(queueARN);

        Aws::SNS::Model::SubscribeOutcome outcome = client.Subscribe(request);

        if (outcome.IsSuccess()) {
            std::cout << "SNS::Subscribe was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::Subscribe. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    // Post to the topic.
    {
        Aws::SNS::Model::PublishRequest request;
        request.SetTopicArn(topicARN);
        request.SetMessage("Greetings SQS queue!");
        request.SetMessageGroupId("1");

        Aws::SNS::Model::PublishOutcome outcome = client.Publish(request);

        if (outcome.IsSuccess()) {
            std::cout << "SNS::Publish was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::Publish. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }
    
    // Poll the queue.
    std::vector<Aws::String> receiptHandles;
    {
        Aws::SQS::Model::ReceiveMessageRequest request;
        request.SetQueueUrl(queueURL);

        Aws::SQS::Model::ReceiveMessageOutcome outcome = sqsClient.ReceiveMessage(request);

        if (outcome.IsSuccess()) {
            std::cout << "SQS::ReceiveMessage was successful." << std::endl;
            for (const Aws::SQS::Model::Message& message : outcome.GetResult().GetMessages())
            {
                std::cout << "  Message : '" << message.GetBody() << "'." << std::endl;
                receiptHandles.push_back(message.GetReceiptHandle());
            }
        }
        else {
            std::cerr << "Error with SQS::ReceiveMessage. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    // Delete the messages.
    if (!receiptHandles.empty())
    {
        Aws::SQS::Model::DeleteMessageBatchRequest request;
        request.SetQueueUrl(queueURL);
        for (const Aws::String& receiptHandle : receiptHandles) {
            request.AddEntries(Aws::SQS::Model::DeleteMessageBatchRequestEntry().WithReceiptHandle(receiptHandle));
        }

        Aws::SQS::Model::DeleteMessageBatchOutcome outcome = sqsClient.DeleteMessageBatch(request);

        if (outcome.IsSuccess()) {
            std::cout << "SQS::DeleteMessageBatch was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SQS::DeleteMessageBatch. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return true;
}

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    (void)argc;
    (void)argv;

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SNS::gettingStartedWithSNSTopics(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif // TESTING_BUILD