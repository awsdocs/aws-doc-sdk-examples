// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[sns.dotNET.PublishAsync]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon;
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
// snippet-end:[sns.dotNET.PublishAsync]