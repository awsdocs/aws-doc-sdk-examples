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
