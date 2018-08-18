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
#include <aws/sns/model/ListTopicsRequest.h>
#include <aws/sns/model/ListTopicsResult.h>
#include <iostream>

/**
 * Lists topics
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_topics" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::ListTopicsRequest lt_req;

    auto lt_out = sns.ListTopics(lt_req);

    if (lt_out.IsSuccess())
    {
      std::cout << "Topics list " << lt_out.GetResult().GetTopics() << std::endl;
    }
    else
    {
      std::cout << "Error listing topics " << lt_out.GetError().GetMessage() <<
        std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
