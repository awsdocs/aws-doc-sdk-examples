/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
#include "sns_samples.h"

namespace AwsDoc {
    namespace SNS {
        static const Aws::String FIFO_SUFFIX = ".fifo";
        static const int NUMBER_OF_QUEUES = 2;
        static const Aws::String TONE_ATTRIBUTE("tone");
        static const Aws::Vector<Aws::String> TONES = {"friendly", "funny", "earnest",
                                                       "sincere"};

        Aws::String createPolicyForQueue(const Aws::String& queueARN,
                                         const Aws::String& topicARN);

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

        //! Utility routine to print a line of asterisks to standard out.
        /*!
         \\sa printAsterisksLine()
        \return void:
         */
        inline void printAsterisksLine() {
            std::cout << "\n" << std::setfill('*') << std::setw(88) << "\n"
                      << std::endl;
        }

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        bool alwaysTrueTest(const Aws::String &) { return true; }
    } // namespace SNS
} // namespace AwsDoc

bool AwsDoc::SNS::gettingStartedWithSNSTopics(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << "Welcome to getting started with SNS topics." << std::endl;
    printAsterisksLine();
    std::cout << "In this workflow, you will create an SNS topic and subscribe "
              << NUMBER_OF_QUEUES <<
              " SQS queues to the topic." << std::endl;
    std::cout
            << "You can select from several options for configuring the topic and the subscriptions for the "
               << NUMBER_OF_QUEUES << " queues." << std::endl;
    std::cout << "You can then post to the topic and see the results in the queues."
              << std::endl;
    Aws::SNS::SNSClient client(clientConfiguration);

    printAsterisksLine();

    // Create a new topic. This call will succeed even if a topic with the same name was
    // already created.
    std::cout << "SNS topics can be configured as FIFO (First-In-First-Out)." << std::endl;
    std::cout << "FIFO topics deliver messages in order and support deduplication and message filtering." << std::endl;
    bool isFifoTopic = askYesNoQuestion(
            "Would you like to work with FIFO topics? (y/n) ");

    bool contentBasedDeduplication = false;
    Aws::String topicName;
    if (isFifoTopic) {
        printAsterisksLine();
        std::cout << "Because you have chosen a FIFO topic, deduplication is supported." << std::endl;
        std::cout << "Duplication is determined by either a duplication ID or content-based deduplication." << std::endl;
        std::cout << "If a message is successfully published to an SNS FIFO topic, any message published and determined to be a duplicate, " << std::endl;
        std::cout << "within the five-minute deduplication interval, is accepted but not delivered." << std::endl;
        std::cout << "For more information about deduplication, see https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html." << std::endl;
        contentBasedDeduplication = askYesNoQuestion(
                "Would you like to use content-based deduplication instead of a duplication ID? (y/n) ");
    }

    printAsterisksLine();

    Aws::String topicARN;
    {
        topicName = askQuestion("Enter a name for your SNS topic: ");
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

        Aws::SNS::Model::CreateTopicOutcome outcome = client.CreateTopic(request);

        if (outcome.IsSuccess()) {
            topicARN = outcome.GetResult().GetTopicArn();
            std::cout << "Your new topic with the name '" << topicName
                      << "' and the topic Amazon Resource Name (ARN) " << std::endl;
            std::cout << "'" << topicARN << "' has been created." << std::endl;

        }
        else {
            std::cerr << "Error with SNS::CreateTopic. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    printAsterisksLine();

    // Create an SQS queue.
    std::cout << "Now you will create " << NUMBER_OF_QUEUES
              << " SNS queues to subscribe to the topic." << std::endl;
    Aws::Vector<Aws::String> queueURLS;
    Aws::Vector<Aws::String> queueNames;
    Aws::Vector<Aws::String> subscriptionARNS;
    bool filteringMessages = false;
    bool first = true;
    for (int i = 1; i <= NUMBER_OF_QUEUES; ++i) {
        Aws::String queueURL;
        Aws::String queueName;
        {
            printAsterisksLine();
            std::ostringstream  ostringstream;
            ostringstream << "Enter a name for " << (first ? "an" : "the next")
            << " SQS queue. ";
            queueName = askQuestion(ostringstream.str());
            Aws::SQS::Model::CreateQueueRequest request;
            if (isFifoTopic) {
                request.AddAttributes(Aws::SQS::Model::QueueAttributeName::FifoQueue,
                                      "true");
                queueName = queueName + FIFO_SUFFIX;

                if (first) // Only explain this once.
                {
                    std::cout
                            << "Because you are creating a FIFO SQS queue, '.fifo' must be appended to the queue name."
                            << std::endl;
                }
            }

            request.SetQueueName(queueName);
            queueNames.push_back(queueName);

            Aws::SQS::Model::CreateQueueOutcome outcome = sqsClient.CreateQueue(
                    request);

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
            }
        }

        if (first) // Only explain this once.
        {
            std::cout
                    << "The queue URL will be used to retrieve the queue ARN, which will be used to create a subscription."
                    << std::endl;
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
                std::cout << "The queue ARN '" << queueARN << "' has been retrieved."
                          << std::endl;
            }
            else {
                std::cerr << "Error with SQS::GetQueueAttributes. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }
            // Give the queue permission to receive messages.
            if (first)
            {
                std::cout << "An IAM policy must be attached to an SQS queue enabling it to receive "
                             "messages from an SNS topic." << std::endl;
            }
        {
            Aws::SQS::Model::SetQueueAttributesRequest request;
            request.SetQueueUrl(queueURL);
            Aws::String policy = createPolicyForQueue(queueARN, topicARN);
            request.AddAttributes(Aws::SQS::Model::QueueAttributeName::Policy,
                                  policy);

            Aws::SQS::Model::SetQueueAttributesOutcome outcome = sqsClient.SetQueueAttributes(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "The attributes for the queue '" << queueName << "' were successfully updated." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::SetQueueAttributes. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        printAsterisksLine();

        // Subscribe the queue.
        {
            Aws::SNS::Model::SubscribeRequest request;
            request.SetTopicArn(topicARN);
            request.SetProtocol("sqs");
            request.SetEndpoint(queueARN);
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
                ostringstream << "Would you like to filter messages for \"" << queueName
                              << "\"'s subscription to the topic \""
                              << topicName << "\"?  (y/n)";

                if (askYesNoQuestion(ostringstream.str())) {
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
                                "Enter a number (or enter zero to not add anything more): ",
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

                    if (!filterSelections.empty()) {
                        filteringMessages = true;
                        std::ostringstream jsonPolicyStream;
                        jsonPolicyStream << "{ \"" << TONE_ATTRIBUTE << "\": [";


                        for (size_t j = 0; j < filterSelections.size(); ++j) {
                            jsonPolicyStream << "\"" << filterSelections[j] << "\"";
                            if (j < filterSelections.size() - 1) {
                                jsonPolicyStream << ",";
                            }
                        }
                        jsonPolicyStream << "] }";

                        Aws::String jsonPolicy = jsonPolicyStream.str();

                        std::cout << "This is the filter policy for this subscription."
                                  << std::endl;
                        std::cout << jsonPolicy << std::endl;

                        request.AddAttributes("FilterPolicy", jsonPolicy);
                    }
                    else {
                        std::cout
                                << "Because you did not select any attributes, no filter will be added to this subscription."
                                << std::endl;
                    }
                }
            }  // if (isFifoTopic)

            Aws::SNS::Model::SubscribeOutcome outcome = client.Subscribe(request);

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
                std::cerr << "Error with SNS::Subscribe. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        first = false;
        queueURLS.push_back(queueURL);
    }

    // Post to the topic.
    first = true;
     do {
         printAsterisksLine();

        Aws::SNS::Model::PublishRequest request;
        request.SetTopicArn(topicARN);
        Aws::String message = askQuestion("Enter a message text to publish.  ");
        request.SetMessage(message);
        if (isFifoTopic) {
            if (first) {
                std::cout
                        << "Because your are using a FIFO topic, you must set a message group ID."
                        << std::endl;
                std::cout
                        << "All messages within the same group will be received in the order they were published."
                        << std::endl;
            }
            Aws::String messageGroupID = askQuestion(
                    "Enter a message group ID for this message. ");
            request.SetMessageGroupId(messageGroupID);
            if (!contentBasedDeduplication) {
                if (first) {
                    std::cout
                            << "Because you are not using content-based deduplication, you must enter a deduplication ID."
                            << std::endl;
                }
                Aws::String deduplicationID = askQuestion(
                        "Enter a deduplication ID for this message. ");
                request.SetMessageDeduplicationId(deduplicationID);
            }
        }

        if (filteringMessages && askYesNoQuestion(
                "Would you like to add an attribute to this message? (y/n) ")) {
            for (size_t i = 0; i < TONES.size(); ++i) {
                std::cout << "  " << (i + 1) << ". " << TONES[i] << std::endl;
            }
            int selection = askQuestionForIntRange(
                    "Enter a number for an attribute: ",
                    1, static_cast<int>(TONES.size()));
            Aws::SNS::Model::MessageAttributeValue messageAttributeValue;
            messageAttributeValue.SetDataType("String");
            messageAttributeValue.SetStringValue(TONES[selection - 1]);
            request.AddMessageAttributes(TONE_ATTRIBUTE, messageAttributeValue);
        }

        Aws::SNS::Model::PublishOutcome outcome = client.Publish(request);

        if (outcome.IsSuccess()) {
            std::cout << "Your message was successfully published." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::Publish. " << outcome.GetError().GetMessage()
                      << std::endl;
        }

        first = false;
    } while (askYesNoQuestion("Would you like to post another message? (y/n) "));

    printAsterisksLine();

    std::cout << "Now the SQS queue will be polled to retrieve the messages." << std::endl;
    askQuestion("Press any key to continue...", alwaysTrueTest);

    for (size_t i = 0; i < queueURLS.size(); ++i) {
        // Poll the queue
        std::vector<Aws::String> messages;
        std::vector<Aws::String> receiptHandles;
       while(1)
        {
            Aws::SQS::Model::ReceiveMessageRequest request;
            request.SetMaxNumberOfMessages(10);
            request.SetQueueUrl(queueURLS[i]);

            // Setting WaitTimeSecondes to non-zero enables long polling.
            // For information about long polling, see
            // https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-short-and-long-polling.html
            request.SetWaitTimeSeconds(1);
            Aws::SQS::Model::ReceiveMessageOutcome outcome = sqsClient.ReceiveMessage(
                    request);

            if (outcome.IsSuccess()) {
                const Aws::Vector<Aws::SQS::Model::Message>& newMessages = outcome.GetResult().GetMessages();
                if (newMessages.empty())
                {
                    break;
                }
                else{
                    for (const Aws::SQS::Model::Message &message: newMessages)
                    {
                        messages.push_back(message.GetBody());
                        receiptHandles.push_back(message.GetReceiptHandle());
                    }
                }
            }
            else {
                std::cerr << "Error with SQS::ReceiveMessage. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                break;
            }
        }

        printAsterisksLine();

        if (messages.empty())
        {
            std::cout << "No messages were ";
        }
        else if (messages.size() == 1)
        {
            std::cout << "One message was ";
        }
        else{
            std::cout << messages.size() << " messages were ";
        }
        std::cout << "received by the queue '" << queueNames[i]
                  << "'." << std::endl;
        for (const Aws::String &message: messages) {
            std::cout << "  Message : '" << message << "'."
                      << std::endl;
        }

        // Delete the messages.
        if (!receiptHandles.empty()) {
            Aws::SQS::Model::DeleteMessageBatchRequest request;
            request.SetQueueUrl(queueURLS[i]);
            int id = 1; // Id's must be unique within a batch delete request.
            for (const Aws::String &receiptHandle: receiptHandles) {
                Aws::SQS::Model::DeleteMessageBatchRequestEntry entry;
                entry.SetId(std::to_string(id));
                ++id;
                entry.SetReceiptHandle(receiptHandle);
                request.AddEntries(entry);
            }

            Aws::SQS::Model::DeleteMessageBatchOutcome outcome = sqsClient.DeleteMessageBatch(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "The batch delete of messages were successful." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::DeleteMessageBatch. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }
    }

    printAsterisksLine();

    if (askYesNoQuestion("Would you like to delete the SQS queues? (y/n) ")) {
        for (const auto &queueURL: queueURLS) {
            Aws::SQS::Model::DeleteQueueRequest request;
            request.SetQueueUrl(queueURL);

            Aws::SQS::Model::DeleteQueueOutcome outcome = sqsClient.DeleteQueue(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "The queue with URL '" << queueURL << "' was successfully deleted." << std::endl;
            }
            else {
                std::cerr << "Error with SQS::DeleteQueue. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        for (const auto &subscriptionARN: subscriptionARNS) {
            Aws::SNS::Model::UnsubscribeRequest request;
            request.SetSubscriptionArn(subscriptionARN);

            Aws::SNS::Model::UnsubscribeOutcome outcome = client.Unsubscribe(request);

            if (outcome.IsSuccess()) {
                std::cout << "Unsubscribe of subscritpion ARN '" << subscriptionARN << "' was successful." << std::endl;
            }
            else {
                std::cerr << "Error with SNS::Unsubscribe. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }
    }

    printAsterisksLine();

    if (askYesNoQuestion("Would you like to delete the SNS topic? (y/n) ")) {
        Aws::SNS::Model::DeleteTopicRequest request;
        request.SetTopicArn(topicARN);

        Aws::SNS::Model::DeleteTopicOutcome outcome = client.DeleteTopic(request);

        if (outcome.IsSuccess()) {
            std::cout << "The topic with ARN '" << topicARN << "' was successfully deleted." << std::endl;
        }
        else {
            std::cerr << "Error with SNS::DeleteTopicRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return true;
}

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    (void) argc;
    (void) argv;

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;

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

Aws::String AwsDoc::SNS::createPolicyForQueue(const Aws::String &queueARN,
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
