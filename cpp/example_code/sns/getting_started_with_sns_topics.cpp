/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/ListTopicsRequest.h>
#include <aws/sns/SNSClient.h>
#include "sns_samples.h"

namespace AwsDoc {
    namespace SNS {
static const char TOPIC_NAME[] = "getting_started_topic.fifo";

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

    // list topics
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