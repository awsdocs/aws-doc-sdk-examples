// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates messaging with topics and queues using Amazon Simple Notification
 * Service (Amazon SNS) and Amazon Simple Queue Service (Amazon SQS).
 *
 * 1.  Create an SNS topic, either FIFO (First-In-First-Out) or non-FIFO. (CreateTopic)
 * 2.  Create an SQS queue. (CreateQueue)
 * 3.  Get the SQS queue ARN attribute. (GetQueueAttributes)
 * 4.  Set the SQS queue policy attribute with a policy enabling the receipt of SNS messages. (SetQueueAttributes)
 * 5.  Subscribe the SQS queue to the SNS topic. (Subscribe)
 * 6.  Publish a message to the SNS topic. (Publish)
 * 7.  Poll an SQS queue for its messages. (ReceiveMessage)
 * 8.  Delete a batch of messages from an SQS queue. (DeleteMessageBatch)
 * 9.  Delete an SQS queue. (DeleteQueue)
 * 10. Unsubscribe an SNS subscription. (Unsubscribe)
 * 11. Delete an SNS topic. (DeleteTopic)
 *
 */

#include <aws/core/Aws.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/DeleteTopicRequest.h>
#include <aws/sns/model/PublishRequest.h>
#include <aws/sns/model/SubscribeRequest.h>
#include <aws/sns/model/UnsubscribeRequest.h>
#include <aws/sns/SNSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/DeleteMessageBatchRequest.h>
#include <aws/sqs/model/DeleteQueueRequest.h>
#include <aws/sqs/model/GetQueueAttributesRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <aws/sqs/SQSClient.h>
#include <iomanip>
#include "topics_and_queues_samples.h"

namespace AwsDoc {
    namespace TopicsAndQueues {
        static const Aws::String FIFO_SUFFIX = ".fifo";
        static const int NUMBER_OF_QUEUES = 2;
        // snippet-start:[cpp.example_code.cross-service.topics_and_queues.attribute_declare]
        static const Aws::String TONE_ATTRIBUTE("tone");
        static const Aws::Vector<Aws::String> TONES = {"cheerful", "funny", "serious",
                                                       "sincere"};
        // snippet-end:[cpp.example_code.cross-service.topics_and_queues.attribute_declare]

        //! Create an AWS Identity and Access Management (IAM) policy that gives an
        //! SQS queue permission to receive messages from an SNS topic.
        /*!
         \sa createPolicyForQueue()
         \param queueARN: The SQS queue Amazon Resource Name (ARN).
         \param topicARN: The SNS topic ARN.
         \return Aws::String: The policy as JSON.
         */
        static Aws::String createPolicyForQueue(const Aws::String &queueARN,
                                                const Aws::String &topicARN);

        //! Routine that lets the user select attributes for a subscription filter policy.
        /*!
         \sa getFilterPolicyFromUser()
         \return Aws::String: The filter policy as JSON.
         */
        static Aws::String getFilterPolicyFromUser();

        //! Routine that deletes AWS resources created by this workflow.
        /*!
         \sa cleanUp()
         \param topicARN: The ARN of an SNS topic.
         \param queueURLS: A vector of SQS queue URLs.
         \param subscriptionARNS: A vector of SNS subscriptions.
         \param snsClient: An SNS client.
         \param sqsClient: An SQS client.
         \param askUser: If true, user interaction is enabled.
         \return bool: Function succeeded.
         */
        static bool cleanUp(const Aws::String &topicARN,
                            const Aws::Vector<Aws::String> &queueURLS,
                            const Aws::Vector<Aws::String> &subscriptionARNS,
                            const Aws::SNS::SNSClient &snsClient,
                            const Aws::SQS::SQSClient &sqsClient,
                            bool askUser = false);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        static bool testForEmptyString(const Aws::String &string);

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        static Aws::String askQuestion(const Aws::String &string,
                                       const std::function<bool(
                                               Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        static bool askYesNoQuestion(const Aws::String &string);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        static int askQuestionForIntRange(const Aws::String &string, int low,
                                          int high);

        //! Utility routine to print a line of asterisks to standard out.
        /*!
         \\sa printAsterisksLine()
        \return void:
         */
        static void printAsterisksLine() {
            std::cout << "\n" << std::setfill('*') << std::setw(88) << "\n"
                      << std::endl;
        }

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        static bool alwaysTrueTest(const Aws::String &) { return true; }
    } // namespace TopicsAndQueues
} // namespace AwsDoc

// snippet-start:[cpp.example_code.cross-service.topics_and_queues.messaging_with_topics_and_queues]
//! Workflow for messaging with topics and queues using Amazon SNS and Amazon SQS.
/*!
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */
bool AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << "Welcome to messaging with topics and queues." << std::endl;
    printAsterisksLine();
    std::cout << "In this workflow, you will create an SNS topic and subscribe "
              << NUMBER_OF_QUEUES <<
              " SQS queues to the topic." << std::endl;
    std::cout
            << "You can select from several options for configuring the topic and the subscriptions for the "
            << NUMBER_OF_QUEUES << " queues." << std::endl;
    std::cout << "You can then post to the topic and see the results in the queues."
              << std::endl;

    // snippet-start:[cpp.example_code.cross-service.topics_and_queues.sns_client]
    Aws::SNS::SNSClient snsClient(clientConfiguration);
    // snippet-end:[cpp.example_code.cross-service.topics_and_queues.sns_client]

    printAsterisksLine();

    std::cout << "SNS topics can be configured as FIFO (First-In-First-Out)."
              << std::endl;
    std::cout
            << "FIFO topics deliver messages in order and support deduplication and message filtering."
            << std::endl;
    bool isFifoTopic = askYesNoQuestion(
            "Would you like to work with FIFO topics? (y/n) ");

    bool contentBasedDeduplication = false;
    Aws::String topicName;
    if (isFifoTopic) {
        printAsterisksLine();
        std::cout << "Because you have chosen a FIFO topic, deduplication is supported."
                  << std::endl;
        std::cout
                << "Deduplication IDs are either set in the message or automatically generated "
                << "from content using a hash function." << std::endl;
        std::cout
                << "If a message is successfully published to an SNS FIFO topic, any message "
                << "published and determined to have the same deduplication ID, "
                << std::endl;
        std::cout
                << "within the five-minute deduplication interval, is accepted but not delivered."
                << std::endl;
        std::cout
                << "For more information about deduplication, "
                << "see https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html."
                << std::endl;
        contentBasedDeduplication = askYesNoQuestion(
                "Use content-based deduplication instead of entering a deduplication ID? (y/n) ");
    }

    printAsterisksLine();

    // snippet-start:[cpp.example_code.cross-service.topics_and_queues.sqs_client]
    Aws::SQS::SQSClient sqsClient(clientConfiguration);
    // snippet-end:[cpp.example_code.cross-service.topics_and_queues.sqs_client]
    Aws::Vector<Aws::String> queueURLS;
    Aws::Vector<Aws::String> subscriptionARNS;

    Aws::String topicARN;
    {
        topicName = askQuestion("Enter a name for your SNS topic. ");

        // 1.  Create an Amazon SNS topic, either FIFO or non-FIFO.
        Aws::SNS::Model::CreateTopicRequest request;

        if (isFifoTopic) {
            request.AddAttributes("FifoTopic", "true");
            if (contentBasedDeduplication) {
                request.AddAttributes("ContentBasedDeduplication", "true");
            }
            topicName = topicName + FIFO_SUFFIX;

            std::cout
                    << "Because you have selected a FIFO topic, '.fifo' must be appended to the topic name."
                    << std::endl;
        }

        request.SetName(topicName);

        Aws::SNS::Model::CreateTopicOutcome outcome = snsClient.CreateTopic(request);

        if (outcome.IsSuccess()) {
            topicARN = outcome.GetResult().GetTopicArn();
            std::cout << "Your new topic with the name '" << topicName
                      << "' and the topic Amazon Resource Name (ARN) " << std::endl;
            std::cout << "'" << topicARN << "' has been created." << std::endl;

        }
        else {
            std::cerr << "Error with TopicsAndQueues::CreateTopic. "
                      << outcome.GetError().GetMessage()
                      << std::endl;

            cleanUp(topicARN,
                    queueURLS,
                    subscriptionARNS,
                    snsClient,
                    sqsClient);

            return false;
        }
    }

    printAsterisksLine();

    std::cout << "Now you will create " << NUMBER_OF_QUEUES
              << " SQS queues to subscribe to the topic." << std::endl;
    Aws::Vector<Aws::String> queueNames;
    bool filteringMessages = false;
    bool first = true;
    for (int i = 1; i <= NUMBER_OF_QUEUES; ++i) {
        Aws::String queueURL;
        Aws::String queueName;
        {
            printAsterisksLine();
            std::ostringstream ostringstream;
            ostringstream << "Enter a name for " << (first ? "an" : "the next")
                          << " SQS queue. ";
            queueName = askQuestion(ostringstream.str());

            // 2.  Create an SQS queue.
            Aws::SQS::Model::CreateQueueRequest request;
            if (isFifoTopic) {
                request.AddAttributes(Aws::SQS::Model::QueueAttributeName::FifoQueue,
                                      "true");
                queueName = queueName + FIFO_SUFFIX;

                if (first) // Only explain this once.
                {
                    std::cout
                            << "Because you are creating a FIFO SQS queue, '.fifo' must "
                            << "be appended to the queue name." << std::endl;
                }
            }

            request.SetQueueName(queueName);
            queueNames.push_back(queueName);

            Aws::SQS::Model::CreateQueueOutcome outcome =
                    sqsClient.CreateQueue(request);

            if (outcome.IsSuccess()) {
                queueURL = outcome.GetResult().GetQueueUrl();
                std::cout << "Your new SQS queue with the name '" << queueName
                          << "' and the queue URL " << std::endl;
                std::cout << "'" << queueURL << "' has been created." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::CreateQueue. "
                          << outcome.GetError().GetMessage()
                          << std::endl;

                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
            }
        }
        queueURLS.push_back(queueURL);

        if (first) // Only explain this once.
        {
            std::cout
                    << "The queue URL is used to retrieve the queue ARN, which is "
                    << "used to create a subscription." << std::endl;
        }

        Aws::String queueARN;
        {
            // 3.  Get the SQS queue ARN attribute.
            // snippet-start:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes1]
            Aws::SQS::Model::GetQueueAttributesRequest request;
            request.SetQueueUrl(queueURL);
            request.AddAttributeNames(Aws::SQS::Model::QueueAttributeName::QueueArn);

            Aws::SQS::Model::GetQueueAttributesOutcome outcome =
                    sqsClient.GetQueueAttributes(request);

            if (outcome.IsSuccess()) {
                const Aws::Map<Aws::SQS::Model::QueueAttributeName, Aws::String> &attributes =
                        outcome.GetResult().GetAttributes();
                const auto &iter = attributes.find(
                        Aws::SQS::Model::QueueAttributeName::QueueArn);
                if (iter != attributes.end()) {
                    queueARN = iter->second;
                    std::cout << "The queue ARN '" << queueARN
                              << "' has been retrieved."
                              << std::endl;
                }
                    // snippet-end:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes1]
                else {
                    std::cerr
                            << "Error ARN attribute not returned by GetQueueAttribute."
                            << std::endl;

                    cleanUp(topicARN,
                            queueURLS,
                            subscriptionARNS,
                            snsClient,
                            sqsClient);

                    return false;
                }
                // snippet-start:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes2]
            }
            else {
                std::cerr << "Error with SQS::GetQueueAttributes. "
                          << outcome.GetError().GetMessage()
                          << std::endl;

                // snippet-end:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes2]
                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
                // snippet-start:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes3]
            }
            // snippet-end:[cpp.example_code.cross-service.topics_and_queues.GetQueueAttributes3]
        }

        if (first) {
            std::cout
                    << "An IAM policy must be attached to an SQS queue, enabling it to receive "
                       "messages from an SNS topic." << std::endl;
        }

        {
            // 4.  Set the SQS queue policy attribute with a policy enabling the receipt of SNS messages.
            Aws::SQS::Model::SetQueueAttributesRequest request;
            request.SetQueueUrl(queueURL);
            Aws::String policy = createPolicyForQueue(queueARN, topicARN);
            request.AddAttributes(Aws::SQS::Model::QueueAttributeName::Policy,
                                  policy);

            Aws::SQS::Model::SetQueueAttributesOutcome outcome =
                    sqsClient.SetQueueAttributes(request);

            if (outcome.IsSuccess()) {
                std::cout << "The attributes for the queue '" << queueName
                          << "' were successfully updated." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::SetQueueAttributes. "
                          << outcome.GetError().GetMessage()
                          << std::endl;

                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
            }
        }

        printAsterisksLine();

        {
            // 5.  Subscribe the SQS queue to the SNS topic.
            // snippet-start:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue_with_filter]
            // snippet-start:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue1]
            Aws::SNS::Model::SubscribeRequest request;
            request.SetTopicArn(topicARN);
            request.SetProtocol("sqs");
            request.SetEndpoint(queueARN);
            // snippet-end:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue1]
            if (isFifoTopic) {
                if (first) {
                    std::cout << "Subscriptions to a FIFO topic can have filters."
                              << std::endl;
                    std::cout
                            << "If you add a filter to this subscription, then only the filtered messages "
                            << "will be received in the queue." << std::endl;
                    std::cout << "For information about message filtering, "
                              << "see https://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html"
                              << std::endl;
                    std::cout << "For this example, you can filter messages by a \""
                              << TONE_ATTRIBUTE << "\" attribute." << std::endl;
                }

                std::ostringstream ostringstream;
                ostringstream << "Filter messages for \"" << queueName
                              << "\"'s subscription to the topic \""
                              << topicName << "\"?  (y/n)";

                // Add filter if user answers yes.
                if (askYesNoQuestion(ostringstream.str())) {
                    Aws::String jsonPolicy = getFilterPolicyFromUser();
                    if (!jsonPolicy.empty()) {
                        filteringMessages = true;

                        std::cout << "This is the filter policy for this subscription."
                                  << std::endl;
                        std::cout << jsonPolicy << std::endl;

                        request.AddAttributes("FilterPolicy", jsonPolicy);
                    }
                    else {
                        std::cout
                                << "Because you did not select any attributes, no filter "
                                << "will be added to this subscription." << std::endl;
                    }
                }
            }  // if (isFifoTopic)
            // snippet-start:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue2]
            Aws::SNS::Model::SubscribeOutcome outcome = snsClient.Subscribe(request);

            if (outcome.IsSuccess()) {
                Aws::String subscriptionARN = outcome.GetResult().GetSubscriptionArn();
                std::cout << "The queue '" << queueName
                          << "' has been subscribed to the topic '"
                          << "'" << topicName << "'" << std::endl;
                std::cout << "with the subscription ARN '" << subscriptionARN << "."
                          << std::endl;
                subscriptionARNS.push_back(subscriptionARN);
            }
            else {
                std::cerr << "Error with TopicsAndQueues::Subscribe. "
                          << outcome.GetError().GetMessage()
                          << std::endl;

                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
            }
            // snippet-end:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue2]
            // snippet-end:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue_with_filter]
        }

        first = false;
    }

    first = true;
    do {
        printAsterisksLine();

        // 6.  Publish a message to the SNS topic.
        // snippet-start:[cpp.example_code.cross-service.topics_and_queues.publish_message_with_attributes]
        Aws::SNS::Model::PublishRequest request;
        request.SetTopicArn(topicARN);
        Aws::String message = askQuestion("Enter a message text to publish.  ");
        request.SetMessage(message);
        // snippet-end:[cpp.example_code.cross-service.topics_and_queues.publish_message_with_attributes]
        if (isFifoTopic) {
            if (first) {
                std::cout
                        << "Because you are using a FIFO topic, you must set a message group ID."
                        << std::endl;
                std::cout
                        << "All messages within the same group will be received in the "
                        << "order they were published." << std::endl;
            }
            Aws::String messageGroupID = askQuestion(
                    "Enter a message group ID for this message. ");
            request.SetMessageGroupId(messageGroupID);
            if (!contentBasedDeduplication) {
                if (first) {
                    std::cout
                            << "Because you are not using content-based deduplication, "
                            << "you must enter a deduplication ID." << std::endl;
                }
                Aws::String deduplicationID = askQuestion(
                        "Enter a deduplication ID for this message. ");
                request.SetMessageDeduplicationId(deduplicationID);
            }
        }

        // snippet-start:[cpp.example_code.cross-service.topics_and_queues.publish_message_with_attributes2]
        if (filteringMessages && askYesNoQuestion(
                "Add an attribute to this message? (y/n) ")) {
            for (size_t i = 0; i < TONES.size(); ++i) {
                std::cout << "  " << (i + 1) << ". " << TONES[i] << std::endl;
            }
            int selection = askQuestionForIntRange(
                    "Enter a number for an attribute. ",
                    1, static_cast<int>(TONES.size()));
            Aws::SNS::Model::MessageAttributeValue messageAttributeValue;
            messageAttributeValue.SetDataType("String");
            messageAttributeValue.SetStringValue(TONES[selection - 1]);
            request.AddMessageAttributes(TONE_ATTRIBUTE, messageAttributeValue);
        }

        Aws::SNS::Model::PublishOutcome outcome = snsClient.Publish(request);

        if (outcome.IsSuccess()) {
            std::cout << "Your message was successfully published." << std::endl;
        }
        else {
            std::cerr << "Error with TopicsAndQueues::Publish. "
                      << outcome.GetError().GetMessage()
                      << std::endl;

            cleanUp(topicARN,
                    queueURLS,
                    subscriptionARNS,
                    snsClient,
                    sqsClient);

            return false;
        }
        // snippet-end:[cpp.example_code.cross-service.topics_and_queues.publish_message_with_attributes2]

        first = false;
    } while (askYesNoQuestion("Post another message? (y/n) "));

    printAsterisksLine();

    std::cout << "Now the SQS queue will be polled to retrieve the messages."
              << std::endl;
    askQuestion("Press any key to continue...", alwaysTrueTest);

    for (size_t i = 0; i < queueURLS.size(); ++i) {
        // 7.  Poll an SQS queue for its messages.
        std::vector<Aws::String> messages;
        std::vector<Aws::String> receiptHandles;
        while (true) {
            Aws::SQS::Model::ReceiveMessageRequest request;
            request.SetMaxNumberOfMessages(10);
            request.SetQueueUrl(queueURLS[i]);

            // Setting WaitTimeSeconds to non-zero enables long polling.
            // For information about long polling, see
            // https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-short-and-long-polling.html
            request.SetWaitTimeSeconds(1);
            Aws::SQS::Model::ReceiveMessageOutcome outcome =
                    sqsClient.ReceiveMessage(request);

            if (outcome.IsSuccess()) {
                const Aws::Vector<Aws::SQS::Model::Message> &newMessages = outcome.GetResult().GetMessages();
                if (newMessages.empty()) {
                    break;
                }
                else {
                    for (const Aws::SQS::Model::Message &message: newMessages) {
                        messages.push_back(message.GetBody());
                        receiptHandles.push_back(message.GetReceiptHandle());
                    }
                }
            }
            else {
                std::cerr << "Error with SQS::ReceiveMessage. "
                          << outcome.GetError().GetMessage()
                          << std::endl;

                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
            }
        }

        printAsterisksLine();

        if (messages.empty()) {
            std::cout << "No messages were ";
        }
        else if (messages.size() == 1) {
            std::cout << "One message was ";
        }
        else {
            std::cout << messages.size() << " messages were ";
        }
        std::cout << "received by the queue '" << queueNames[i]
                  << "'." << std::endl;
        for (const Aws::String &message: messages) {
            std::cout << "  Message : '" << message << "'."
                      << std::endl;
        }

        // 8.  Delete a batch of messages from an SQS queue.
        if (!receiptHandles.empty()) {
            // snippet-start:[cpp.example_code.cross-service.topics_and_queues.sqs.DeleteMessageBatch]
            Aws::SQS::Model::DeleteMessageBatchRequest request;
            request.SetQueueUrl(queueURLS[i]);
            int id = 1; // Ids must be unique within a batch delete request.
            for (const Aws::String &receiptHandle: receiptHandles) {
                Aws::SQS::Model::DeleteMessageBatchRequestEntry entry;
                entry.SetId(std::to_string(id));
                ++id;
                entry.SetReceiptHandle(receiptHandle);
                request.AddEntries(entry);
            }

            Aws::SQS::Model::DeleteMessageBatchOutcome outcome =
                    sqsClient.DeleteMessageBatch(request);

            if (outcome.IsSuccess()) {
                std::cout << "The batch deletion of messages was successful."
                          << std::endl;
            }
            else {
                std::cerr << "Error with SQS::DeleteMessageBatch. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                cleanUp(topicARN,
                        queueURLS,
                        subscriptionARNS,
                        snsClient,
                        sqsClient);

                return false;
            }
            // snippet-end:[cpp.example_code.cross-service.topics_and_queues.sqs.DeleteMessageBatch]
        }
    }

    return cleanUp(topicARN,
                   queueURLS,
                   subscriptionARNS,
                   snsClient,
                   sqsClient,
                   true); // askUser
}


bool AwsDoc::TopicsAndQueues::cleanUp(const Aws::String &topicARN,
                                      const Aws::Vector<Aws::String> &queueURLS,
                                      const Aws::Vector<Aws::String> &subscriptionARNS,
                                      const Aws::SNS::SNSClient &snsClient,
                                      const Aws::SQS::SQSClient &sqsClient,
                                      bool askUser) {
    bool result = true;
    printAsterisksLine();
    if (!queueURLS.empty() && askUser &&
        askYesNoQuestion("Delete the SQS queues? (y/n) ")) {

        for (const auto &queueURL: queueURLS) {
            // 9.  Delete an SQS queue.
            Aws::SQS::Model::DeleteQueueRequest request;
            request.SetQueueUrl(queueURL);

            Aws::SQS::Model::DeleteQueueOutcome outcome =
                    sqsClient.DeleteQueue(request);

            if (outcome.IsSuccess()) {
                std::cout << "The queue with URL '" << queueURL
                          << "' was successfully deleted." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::DeleteQueue. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }

        for (const auto &subscriptionARN: subscriptionARNS) {
            // 10. Unsubscribe an SNS subscription.
            Aws::SNS::Model::UnsubscribeRequest request;
            request.SetSubscriptionArn(subscriptionARN);

            Aws::SNS::Model::UnsubscribeOutcome outcome =
                    snsClient.Unsubscribe(request);

            if (outcome.IsSuccess()) {
                std::cout << "Unsubscribe of subscription ARN '" << subscriptionARN
                          << "' was successful." << std::endl;
            }
            else {
                std::cerr << "Error with TopicsAndQueues::Unsubscribe. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }
    }

    printAsterisksLine();
    if (!topicARN.empty() && askUser &&
        askYesNoQuestion("Delete the SNS topic? (y/n) ")) {

        // 11. Delete an SNS topic.
        Aws::SNS::Model::DeleteTopicRequest request;
        request.SetTopicArn(topicARN);

        Aws::SNS::Model::DeleteTopicOutcome outcome = snsClient.DeleteTopic(request);

        if (outcome.IsSuccess()) {
            std::cout << "The topic with ARN '" << topicARN
                      << "' was successfully deleted." << std::endl;
        }
        else {
            std::cerr << "Error with TopicsAndQueues::DeleteTopicRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            result = false;
        }
    }

    return result;
}

//! Create an IAM policy that gives an SQS queue permission to receive messages from an SNS topic.
/*!
 \sa createPolicyForQueue()
 \param queueARN: The SQS queue Amazon Resource Name (ARN).
 \param topicARN: The SNS topic ARN.
 \return Aws::String: The policy as JSON.
 */
Aws::String AwsDoc::TopicsAndQueues::createPolicyForQueue(const Aws::String &queueARN,
                                                          const Aws::String &topicARN) {
    std::ostringstream policyStream;
    policyStream << R"({
        "Statement": [
        {
            "Effect": "Allow",
                    "Principal": {
                "Service": "sns.amazonaws.com"
            },
            "Action": "sqs:SendMessage",
                    "Resource": ")" << queueARN << R"(",
                    "Condition": {
                "ArnEquals": {
                    "aws:SourceArn": ")" << topicARN << R"("
                }
            }
        }
        ]
    })";

    return policyStream.str();
}
// snippet-end:[cpp.example_code.cross-service.topics_and_queues.messaging_with_topics_and_queues]

/*
 *
 *  main function
 *
 *  Usage: 'run_messaging_with_topics_and_queues'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    (void) argc;
    (void) argv;

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        // snippet-start:[cpp.example_code.cross-service.topics_and_queues.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.cross-service.topics_and_queues.config]

        AwsDoc::TopicsAndQueues::messagingWithTopicsAndQueues(clientConfig);
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
bool AwsDoc::TopicsAndQueues::testForEmptyString(const Aws::String &string) {
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
Aws::String AwsDoc::TopicsAndQueues::askQuestion(const Aws::String &string,
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
bool AwsDoc::TopicsAndQueues::askYesNoQuestion(const Aws::String &string) {
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
int AwsDoc::TopicsAndQueues::askQuestionForIntRange(const Aws::String &string, int low,
                                                    int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cerr << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cerr << "\nNot a valid number." << std::endl;
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

// snippet-start:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue_with_filter2]
//! Routine that lets the user select attributes for a subscription filter policy.
/*!
 \sa getFilterPolicyFromUser()
 \return Aws::String: The filter policy as JSON.
 */
Aws::String AwsDoc::TopicsAndQueues::getFilterPolicyFromUser() {
    std::cout
            << "You can filter messages by one or more of the following \""
            << TONE_ATTRIBUTE << "\" attributes." << std::endl;

    std::vector<Aws::String> filterSelections;
    int selection;
    do {
        for (size_t j = 0; j < TONES.size(); ++j) {
            std::cout << "  " << (j + 1) << ". " << TONES[j]
                      << std::endl;
        }
        selection = askQuestionForIntRange(
                "Enter a number (or enter zero to stop adding more). ",
                0, static_cast<int>(TONES.size()));

        if (selection != 0) {
            const Aws::String &selectedTone(TONES[selection - 1]);
            // Add the tone to the selection if it is not already added.
            if (std::find(filterSelections.begin(),
                          filterSelections.end(),
                          selectedTone)
                == filterSelections.end()) {
                filterSelections.push_back(selectedTone);
            }
        }
    } while (selection != 0);

    Aws::String result;
    if (!filterSelections.empty()) {
        std::ostringstream jsonPolicyStream;
        jsonPolicyStream << "{ \"" << TONE_ATTRIBUTE << "\": [";


        for (size_t j = 0; j < filterSelections.size(); ++j) {
            jsonPolicyStream << "\"" << filterSelections[j] << "\"";
            if (j < filterSelections.size() - 1) {
                jsonPolicyStream << ",";
            }
        }
        jsonPolicyStream << "] }";

        result = jsonPolicyStream.str();
    }

    return result;
}
// snippet-end:[cpp.example_code.cross-service.topics_and_queues.subscribe_queue_with_filter2]
