// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/CreateTopicRequest.h>
#include <aws/sns/model/CreateTopicResult.h>
#include <iostream>

/**
 * Create an SNS topic - demonstrates how to create an Amazon SNS topic to which notifications can be published.
 *
 * If you are using the AWS Management Console to verify/corroborate the creation of the topic, be sure to
 * select the appropriate AWS Region in the upper right-hand dropdown selector.  The topic and any subscriptions
 * will be created under the Region specified via the 'region' global setting
 *  (https://docs.aws.amazon.com/sdkref/latest/guide/setting-global-region.html).
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_topic <topic_name>" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.create_topic.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String topic_name = argv[1];
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::CreateTopicRequest ct_req;
    ct_req.SetName(topic_name);

    auto ct_out = sns.CreateTopic(ct_req);

    if (ct_out.IsSuccess())
    {
      std::cout << "Successfully created topic " << topic_name << std::endl;
    }
    else
    {
      std::cout << "Error creating topic " << topic_name << ":" <<
        ct_out.GetError().GetMessage() << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.create_topic.code]
  return 0;
}
