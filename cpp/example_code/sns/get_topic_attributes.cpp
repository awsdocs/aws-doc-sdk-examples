/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
#include <aws/sns/model/GetTopicAttributesRequest.h>
#include <aws/sns/model/GetTopicAttributesResult.h>
#include <iostream>

/**
 * Get the topic attributes based on command line input for topic arn
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: get_topic_attributes <topic_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  AWS::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String topic_arn = argv[1];

    Aws::SNS::Model::GetTopicAttributesRequest gta_req;
    gta_req.SetTopicArn(topic_arn);

    auto gta_out = sns.GetTopicAttributes(gta_req);

    if (gta_out.IsSuccess())
    {
      std::cout << "Topic Attributes " << gta_out.GetResult() << std::endl;
    }
    else
    {
      std::cout << "Error while getting Topic attributes " << gta_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
