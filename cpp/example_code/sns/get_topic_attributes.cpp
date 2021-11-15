// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/GetTopicAttributesRequest.h>
#include <aws/sns/model/GetTopicAttributesResult.h>
#include <iostream>

/**
 * Get the topic attributes - demonstrates how to retrieve the properties of an Amazon SNS topic.
 *
 * <topic_arn_value> can be obtained from run_list_topics executable and includes the "arn:" prefix.
*/

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: get_topic_attributes <topic_arn_value>" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.get_topic_attributes.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String topic_arn = argv[1];

    Aws::SNS::Model::GetTopicAttributesRequest gta_req;
    gta_req.SetTopicArn(topic_arn);

    auto gta_out = sns.GetTopicAttributes(gta_req);

    if (gta_out.IsSuccess())
    {
      std::cout << "Topic Attributes:" << std::endl;
      for (auto const &attribute : gta_out.GetResult().GetAttributes())
      {
        std::cout << "  * " << attribute.first << " : " << attribute.second << std::endl;
      }
    }
    else
    {
      std::cout << "Error while getting Topic attributes " << gta_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.get_topic_attributes.code]
  return 0;
}
