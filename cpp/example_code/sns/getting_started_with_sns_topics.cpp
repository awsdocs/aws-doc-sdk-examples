/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/DeleteTopicRequest.h>
#include <aws/sns/model/GetTopicAttributesRequest.h>
#include <aws/sns/model/ListTopicsRequest.h>
#include <aws/sns/model/PublishRequest.h>
#include <aws/sns/model/SubscribeRequest.h>
#include <aws/sns/SNSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/DeleteMessageBatchRequest.h>
#include <aws/sqs/model/DeleteQueueRequest.h>
#include <aws/sqs/model/GetQueueAttributesRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/SQSClient.h>
#include "sns_samples.h"

namespace AwsDoc {
    namespace SNS {
static const char TOPIC_NAME[] = "getting_started_topic";
        static const Aws::Vector<Aws::String> QUEUE_NAME = {"getting_started_queue1", "getting_started_queue2"};
        static Aws::String FIFO_SUFFIX = ".fifo";


        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        bool testForEmptyString(const Aws::String &string);

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        bool askYesNoQuestion(const Aws::String &string);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);

    } // namespace SNS
} // namespace AwsDoc

bool AwsDoc::SNS::gettingStartedWithSNSTopics(const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SNS::SNSClient client(clientConfiguration);
    // Create a new topic. This call will succeed even if a topic with the same name was
    // already created.
    bool isFifoTopic = askYesNoQuestion("Would you like to work with FIFO topics? (y/n) ");

    bool contentBasedDeduplication = false;
    if (isFifoTopic)
    {
        contentBasedDeduplication = askYesNoQuestion("Would you like to use content based deduplication? (y/n) ");
    }
    Aws::String topicARN;
    {
        Aws::SNS::Model::CreateTopicRequest request;
        Aws::String topicName(TOPIC_NAME);

        if (isFifoTopic) {
            request.AddAttributes("FifoTopic", "true");
            if (contentBasedDeduplication) {
                request.AddAttributes("ContentBasedDeduplication", "true");
            }
            topicName = topicName + FIFO_SUFFIX;
        }

        request.SetName(topicName);

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


    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    // Create an SQS queue.
    Aws::Vector<Aws::String> queueURLS;
    for (auto queueName : QUEUE_NAME) {
        Aws::String queueURL;
        {
            Aws::SQS::Model::CreateQueueRequest request;
            if (isFifoTopic) {
                request.AddAttributes(Aws::SQS::Model::QueueAttributeName::FifoQueue,
                                      "true");
                queueName = queueName + FIFO_SUFFIX;
            }

            request.SetQueueName(queueName);

            Aws::SQS::Model::CreateQueueOutcome outcome = sqsClient.CreateQueue(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "SQS::CreateQueue was successful." << std::endl;
                queueURL = outcome.GetResult().GetQueueUrl();
            }
            else {
                std::cerr << "Error with SQS::CreateQueue. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        // Get the queue ARN attribute.
        Aws::String queueARN;
        {
            Aws::SQS::Model::GetQueueAttributesRequest request;
            request.SetQueueUrl(queueURL);
            request.AddAttributeNames(Aws::SQS::Model::QueueAttributeName::QueueArn);

            Aws::SQS::Model::GetQueueAttributesOutcome outcome = sqsClient.GetQueueAttributes(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "SQS::GetQueueAttributes was successful." << std::endl;
                const Aws::Map<Aws::SQS::Model::QueueAttributeName, Aws::String> &attributes =
                        outcome.GetResult().GetAttributes();
                const auto &iter = attributes.find(
                        Aws::SQS::Model::QueueAttributeName::QueueArn);
                if (iter != attributes.end()) {
                    queueARN = iter->second;
                 }
                else {
                    std::cerr
                            << "Error ARN attribute not returned by GetQueueAttribute."
                            << std::endl;
                    return false;
                }
            }
            else {
                std::cerr << "Error with SQS::GetQueueAttributes. "
                          << outcome.GetError().GetMessage()
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
                std::cerr << "Error with SNS::Subscribe. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }
        queueURLS.push_back(queueURL);
    }

    int deduplicationID = 1;
    // Post to the topic.
    {
        Aws::SNS::Model::PublishRequest request;
        request.SetTopicArn(topicARN);
        request.SetMessage("Greetings SQS queue!");
        if (isFifoTopic) {
            request.SetMessageGroupId("1");
            if (!contentBasedDeduplication) {
                request.SetMessageDeduplicationId(std::to_string(deduplicationID));
                deduplicationID = deduplicationID + 1;
            }
        }

        Aws::SNS::Model::PublishOutcome outcome = client.Publish(request);

        if (outcome.IsSuccess()) {
            std::cout << "SNS::Publish was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::Publish. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    for (const auto& queueURL : queueURLS) {
        // Poll the queue
        std::vector<Aws::String> receiptHandles;
        {
            Aws::SQS::Model::ReceiveMessageRequest request;
            request.SetQueueUrl(queueURL);

            Aws::SQS::Model::ReceiveMessageOutcome outcome = sqsClient.ReceiveMessage(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "SQS::ReceiveMessage was successful." << std::endl;
                for (const Aws::SQS::Model::Message &message: outcome.GetResult().GetMessages()) {
                    std::cout << "  Message : '" << message.GetBody() << "'."
                              << std::endl;
                    receiptHandles.push_back(message.GetReceiptHandle());
                }
            }
            else {
                std::cerr << "Error with SQS::ReceiveMessage. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        // Delete the messages.
        if (!receiptHandles.empty()) {
            Aws::SQS::Model::DeleteMessageBatchRequest request;
            request.SetQueueUrl(queueURL);
            for (const Aws::String &receiptHandle: receiptHandles) {
                request.AddEntries(
                        Aws::SQS::Model::DeleteMessageBatchRequestEntry().WithReceiptHandle(
                                receiptHandle));
            }

            Aws::SQS::Model::DeleteMessageBatchOutcome outcome = sqsClient.DeleteMessageBatch(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "SQS::DeleteMessageBatch was successful." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::DeleteMessageBatch. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }
    }

    for (const auto& queueURL : queueURLS)
    {
        Aws::SQS::Model::DeleteQueueRequest request;
        request.SetQueueUrl(queueURL);

        Aws::SQS::Model::DeleteQueueOutcome outcome = sqsClient.DeleteQueue(request);

        if (outcome.IsSuccess()) {
            std::cout << "SQS::DeleteQueue was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SQS::DeleteQueue. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    {
        Aws::SNS::Model::DeleteTopicRequest request;
        request.SetTopicArn(topicARN);

        Aws::SNS::Model::DeleteTopicOutcome outcome = client.DeleteTopic(request);

        if (outcome.IsSuccess()) {
            std::cout << "SNS::DeleteTopicRequest was successful." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::DeleteTopicRequest. " << outcome.GetError().GetMessage()
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

//! Test routine passed as argument to askQuestion routine.
/*!
 \sa testForEmptyString()
 \param string: A string to test.
 \return bool: True if empty.
 */
bool AwsDoc::SNS::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::SNS::askQuestion(const Aws::String &string,
                                        const std::function<bool(
                                                Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \\sa askYesNoQuestion()
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::SNS::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \sa askQuestionForIntRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int AwsDoc::SNS::askQuestionForIntRange(const Aws::String &string, int low,
                                           int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}
