// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/ListSubscriptionsRequest.h>
#include <aws/sns/model/ListSubscriptionsResult.h>
#include <iostream>

/**
 * Lists subscriptions - demonstrates how to retrieve a list of Amazon SNS subscriptions.
 */

int main(int argc, char ** argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: list_subscriptions" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.list_subscriptions.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::ListSubscriptionsRequest ls_req;

    auto ls_out = sns.ListSubscriptions(ls_req);

    if (ls_out.IsSuccess())
    {
        std::cout << "Subscriptions list:" << std::endl;
        for (auto const& subscription : ls_out.GetResult().GetSubscriptions())
        {
            std::cout << "  * " << subscription.GetSubscriptionArn() << std::endl;
        }
    }
    else
    {
      std::cout << "Error listing subscriptions " << ls_out.GetError().GetMessage() <<
        std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.list_subscriptions.code]
  return 0;
}
