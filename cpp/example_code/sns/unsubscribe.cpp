// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/UnsubscribeRequest.h>
#include <iostream>

/**
 * Unsubscribe - demonstrates how to delete a subscription to an Amazon SNS topic.
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: unsubscribe <topic_subscription_arn>" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.unsubscribe.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String subscription_arn = argv[1];

    Aws::SNS::Model::UnsubscribeRequest s_req;
    s_req.SetSubscriptionArn(subscription_arn);

    auto s_out = sns.Unsubscribe(s_req);

    if (s_out.IsSuccess())
    {
      std::cout << "Unsubscribed successfully " << std::endl;
    }
    else
    {
      std::cout << "Error while unsubscribing " << s_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.unsubscribe.code]
  return 0;
}
