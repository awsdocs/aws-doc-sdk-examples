// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/DeleteTopicRequest.h>
#include <iostream>

/**
 * Delete an SNS topic - demonstrates how to delete an Amazon SNS topic and all its subscriptions.
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_topic <topic_arn>" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.delete_topic.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String topic_arn = argv[1];
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::DeleteTopicRequest dt_req;
    dt_req.SetTopicArn(topic_arn);

    auto dt_out = sns.DeleteTopic(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully deleted topic " << topic_arn << std::endl;
    }
    else
    {
      std::cout << "Error deleting topic " << topic_arn << ":" <<
        dt_out.GetError().GetMessage() << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.delete_topic.code]
  return 0;
}
