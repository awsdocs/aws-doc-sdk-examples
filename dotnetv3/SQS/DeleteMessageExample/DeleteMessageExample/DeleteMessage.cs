// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to delete one or more messages from an Amazon Simple Queue
/// Service (Amazon SQS) queue. This example was created using the AWS SDK
/// for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace DeleteMessageExample
{
    // snippet-start:[SQS.dotnetv3.DeleteMessageExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.SQS;
    using Amazon.SQS.Model;

    public class DeleteMessage
    {
        /// <summary>
        /// Initializes the Amazon SQS client object. It then calls the
        /// ReceiveMessageAsync method to retrieve information about the
        /// available methods before deleting them.
        /// </summary>
        public static async Task Main()
        {
            string queueUrl = "https://sqs.us-east-2.amazonaws.com/0123456789ab/Example_Queue";
            var attributeNames = new List<string>() { "All" };
            int maxNumberOfMessages = 5;
            var visibilityTimeout = (int)TimeSpan.FromMinutes(10).TotalSeconds;
            var waitTimeSeconds = (int)TimeSpan.FromSeconds(5).TotalSeconds;

            // If the Amazon SQS message queue is not in the same AWS Region as your
            // default user, you need to provide the AWS Region as a parameter to the
            // client constructor.
            var client = new AmazonSQSClient();

            var request = new ReceiveMessageRequest
            {
                QueueUrl = queueUrl,
                AttributeNames = attributeNames,
                MaxNumberOfMessages = maxNumberOfMessages,
                VisibilityTimeout = visibilityTimeout,
                WaitTimeSeconds = waitTimeSeconds,
            };

            var response = await client.ReceiveMessageAsync(request);

            if (response.Messages.Count > 0)
            {
                response.Messages.ForEach(async m =>
                {
                    Console.Write($"Message ID: '{m.MessageId}'");

                    var delRequest = new DeleteMessageRequest
                    {
                        QueueUrl = "https://sqs.us-east-1.amazonaws.com/0123456789ab/MyTestQueue",
                        ReceiptHandle = m.ReceiptHandle,
                    };

                    var delResponse = await client.DeleteMessageAsync(delRequest);
                });
            }
            else
            {
                Console.WriteLine("No messages to delete.");
            }
        }
    }

    // snippet-end:[SQS.dotnetv3.DeleteMessageExample]
}
