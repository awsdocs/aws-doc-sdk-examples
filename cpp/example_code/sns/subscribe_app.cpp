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
// snippet-start:[sns.cpp.subscribe_app.code]
/**
 * Subscribe an app endpoint to a topic - demonstrates how to initiate a subscription to an Amazon SNS topic
 * with delivery to a mobile app.
 * 
 *  NOTE: You must first create an endpoint by registering an app and device.  
 *     See https://docs.aws.amazon.com/sns/latest/dg/mobile-push-send-devicetoken.html for more information.
 * 
 * <protocol_value> set to "application" provides delivery of JSON-encoded message to an EndpointArn
 *         for a mobile app and device (see https://docs.aws.amazon.com/sns/latest/api/API_Subscribe.html for available protocols).
 * <topic_arn_value> can be obtained from run_list_topics executable and includes the "arn:" prefix.
 * <mobile_endpoint_arn> is the EndpointArn of a mobile app and device.
 */
int main(int argc, char ** argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: subscribe_app <protocol_value/application> <topic_arn_value>"
                 " <mobile_endpoint_arn>" << std::endl;
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
// snippet-end:[sns.cpp.subscribe_app.code]
