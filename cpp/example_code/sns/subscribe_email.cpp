/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/SubscribeRequest.h>
#include <iostream>
#include "sns_samples.h"

// snippet-start:[sns.cpp.subscribe_email.code]
//! Subscribe to an Amazon Simple Notification Service (Amazon SNS) topic with delivery to an email address.
/*!
  \param topicARN: An SNS topic Amazon Resource Name (ARN).
  \param emailAddress: An email address.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SNS::subscribeEmail(const Aws::String &topicARN,
                                 const Aws::String &emailAddress,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SNS::SNSClient snsClient(clientConfiguration);

    Aws::SNS::Model::SubscribeRequest request;
    request.SetTopicArn(topicARN);
    request.SetProtocol("email");
    request.SetEndpoint(emailAddress);

    const Aws::SNS::Model::SubscribeOutcome outcome = snsClient.Subscribe(request);

    if (outcome.IsSuccess()) {
        std::cout << "Subscribed successfully." << std::endl;
        std::cout << "Subscription ARN '" << outcome.GetResult().GetSubscriptionArn()
                  << "'." << std::endl;
    }
    else {
        std::cerr << "Error while subscribing " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[sns.cpp.subscribe_email.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_subscribe_email <topic_arn> <email_address>'
 *
 *  Prerequisites: An existing SNS topic and its ARN.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_subscribe_email <topic_arn> <email_address>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String topicArn = argv[1];
        Aws::String emailAddress = argv[2];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SNS::subscribeEmail(topicArn, emailAddress, clientConfig);

    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD