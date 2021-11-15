// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/ListTopicsRequest.h>
#include <aws/sns/model/ListTopicsResult.h>
#include <iostream>

/**
 * Lists topics - demonstrates how to retrieve a list of Amazon SNS topics.
 */

int main(int argc, char ** argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: list_topics" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.list_topics.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::ListTopicsRequest lt_req;

    auto lt_out = sns.ListTopics(lt_req);

    if (lt_out.IsSuccess())
    {
      std::cout << "Topics list:" << std::endl;
      for (auto const &topic : lt_out.GetResult().GetTopics())
      {
        std::cout << "  * " << topic.GetTopicArn() << std::endl;
      }
    }
    else
    {
      std::cout << "Error listing topics " << lt_out.GetError().GetMessage() <<
        std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.list_topics.code]
  return 0;
}
