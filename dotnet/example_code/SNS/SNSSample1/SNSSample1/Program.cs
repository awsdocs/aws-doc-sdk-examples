 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;

namespace SNSSample1
{
    class Program
    {
        static void Main(string[] args)
        {
            AmazonSimpleNotificationServiceClient snsClient = new AmazonSimpleNotificationServiceClient(Amazon.RegionEndpoint.USWest2);

            Dictionary<string, MessageAttributeValue> messageAttributes = new Dictionary<string, MessageAttributeValue>();
            MessageAttributeValue v1 = new MessageAttributeValue();
            v1.DataType = "String";
            v1.StringValue = "senderidx";
            messageAttributes.Add("AWS.SNS.SMS.SenderID", v1);
            MessageAttributeValue v2 = new MessageAttributeValue();
            v2.DataType = "String";
            v2.StringValue = "0.50";
            messageAttributes.Add("AWS.SNS.SMS.MaxPrice", v2);
            MessageAttributeValue v3 = new MessageAttributeValue();
            v3.DataType = "String";
            // Options: Promotional, Transactional
            v3.StringValue = "Promotional";
            messageAttributes.Add("AWS.SNS.SMS.SMSType", v3);
            SendSMSMessageAsync(snsClient, "Hello from AWS SNS!", "+1 XXX YYYYYY", messageAttributes).Wait();
        }


        static async Task SendSMSMessageAsync(AmazonSimpleNotificationServiceClient snsClient, string message, string phoneNumber,
            Dictionary<string, MessageAttributeValue> messageAttributes)
        {
            PublishRequest publishRequest = new PublishRequest();
            publishRequest.PhoneNumber = phoneNumber;
            publishRequest.Message = message;
            publishRequest.MessageAttributes = messageAttributes;
            try
            {
                var response = await snsClient.PublishAsync(publishRequest);
                Console.WriteLine(response.MessageId);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
