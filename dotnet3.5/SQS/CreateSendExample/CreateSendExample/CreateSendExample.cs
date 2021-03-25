// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.SQS;
using Amazon.SQS.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CreateSendExample
{
    class CreateSendExample
    {
        // The following example creates a queue using the Amazon Simple Queue
        // Service (Amazon SQS) and then sends a message to the queue. It was
        // created using AWS SDK for .NET 3.5 and .NET 5.0.

        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint _serviceRegion = RegionEndpoint.USWest2;
        private static IAmazonSQS _client;

        private static readonly string _queueName = "Example_Queue";

        static async Task Main()
        {
            _client = new AmazonSQSClient(_serviceRegion);

        }

        static async Task CreateQueue(IAmazonSQS client, string queueName)
        {
            var request = new CreateQueueRequest
            {
                QueueName = queueName,
                Attributes = new Dictionary<string, string>
                {
                    { "DelaySeconds", "60"},
                    { "MessageRetentionPeriod", "86400"}
                }
            };

            var response = await client.CreateQueueAsync(request);
            Console.WriteLine($"Created a queue with URL : {response.QueueUrl}");
        }

        static async Task<GetQueueUrlResponse> GetQueueURL(IAmazonSQS client, string queueName)
        {
            var request = new GetQueueUrlRequest
            {
                QueueName = queueName
            };

            GetQueueUrlResponse response = await client.GetQueueUrlAsync(request);
            Console.WriteLine($"The SQS queue's URL is {response.QueueUrl}");

            return response;
        }

        static async Task<SendMessageResponse> SendMessage(
            IAmazonSQS client,
            string queueName,
            string messageBody,
            Dictionary<string, MessageAttributeValue> messageAttribute
        )
        {
            var sendMessageRequest = new SendMessageRequest
            {
                DelaySeconds = 10,
                messageAttribute,
                MessageBody = messageBody,
                QueueUrl = queueName
            };

            var response = await client.SendMessageAsync(sendMessageRequest);
            Console.WriteLine($"Sent a message with id : {response.MessageId}");

            return response;
        }
    }
}
