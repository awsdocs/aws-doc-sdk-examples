
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/PublishRequest.h>
#include <aws/sns/model/PublishResult.h>
#include <iostream>

/**
 * Publish SMS to topic - demonstrates how to send a message to an Amazon SNS topic.
 *
 * <topic_arn_value> can be obtained from run_list_topics executable and includes the "arn:" prefix.
*/

int main(int argc, char ** argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: publish_to_topic <message_value> <topic_arn_value> " << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.publish_to_topic.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String message = argv[1];
    Aws::String topic_arn = argv[2];

    Aws::SNS::Model::PublishRequest psms_req;
    psms_req.SetMessage(message);
    psms_req.SetTopicArn(topic_arn);

    auto psms_out = sns.Publish(psms_req);

    if (psms_out.IsSuccess())
    {
      std::cout << "Message published successfully " << std::endl;
    }
    else
    {
      std::cout << "Error while publishing message " << psms_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.publish_to_topic.code]
  return 0;
}
