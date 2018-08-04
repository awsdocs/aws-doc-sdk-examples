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
#include <aws/sns/model/DeleteTopicRequest.h>
#include <aws/sns/model/DeleteTopicResult.h>
#include <iostream>

/**
 * Creates an sns topic based on command line input
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_topic <topic_name>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  AWS::InitAPI(options);
  {
    Aws::String topic_name = argv[1];
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::DeleteTopicRequest dt_req;
    dt_req.SetTopicName(topic_name);

    auto dt_out = sns.DeleteTopic(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully deleted topic " << topic_name << std::endl;
    }
    else
    {
      std::cout << "Error deleting topic " << topic_name << ":" <<
        dt_out.GetError().GetMessage() << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
