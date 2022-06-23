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
#include <aws/sns/model/SubscribeRequest.h>
#include <aws/sns/model/SubscribeResult.h>
#include <iostream>
// snippet-start:[sns.cpp.subscribe_lamda.code]
/**
 * Subscribe an AWS Lambda endpoint to a topic - demonstrates how to initiate a subscription to an Amazon SNS topic with delivery
 *  to an AWS Lambda function.
 * 
 *  NOTE: You must first configure AWS Lambda to run this example.  
 *         See https://docs.amazonaws.cn/en_us/lambda/latest/dg/with-sns-example.html for more information.
 *
 * <protocol_value> set to "lambda" provides delivery of JSON-encoded message to an AWS Lambda function
 *        (see https://docs.aws.amazon.com/sns/latest/api/API_Subscribe.html for available protocols).
 * <topic_arn_value> can be obtained from run_list_topics executable and includes the "arn:" prefix.
 * <lambda_function_arn> is the ARN of an AWS Lambda function.
 */

int main(int argc, char ** argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: subscribe_lamda <protocol_value=lambda> <topic_arn_value>"
                 " <lambda_function_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String protocol = argv[1];
    Aws::String topic_arn = argv[2];
    Aws::String endpoint = argv[3];

    Aws::SNS::Model::SubscribeRequest s_req;
    s_req.SetTopicArn(topic_arn);
    s_req.SetProtocol(protocol);
    s_req.SetEndpoint(endpoint);

    auto s_out = sns.Subscribe(s_req);

    if (s_out.IsSuccess())
    {
      std::cout << "Subscribed successfully " << std::endl;
    }
    else
    {
      std::cout << "Error while subscribing " << s_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
// snippet-end:[sns.cpp.subscribe_lamda.code]
