// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to receive Amazon Simple Queue Service messages from an
/// Amazon SQS queue. The example was created using the AWS SDK for .NET
/// version 3.7 and .NET Core 5.0.
/// </summary>
namespace ReceiveFromQueueExample
{
    // snippet-start:[SQS.dotnetv3.ReceiveFromQueueExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.SQS;
    using Amazon.SQS.Model;

    public class ReceiveFromQueue
    {
        public static async Task Main(string[] args)
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
                DisplayMessages(response.Messages);
            }
        }

        /// <summary>
        /// Display message information for a list of Amazon SQS messages.
        /// </summary>
        /// <param name="messages">The list of Amazon SQS Message objects to display.</param>
        public static void DisplayMessages(List<Message> messages)
        {
            messages.ForEach(m =>
            {
                Console.WriteLine($"For message ID {m.MessageId}:");
                Console.WriteLine($"  Body: {m.Body}");
                Console.WriteLine($"  Receipt handle: {m.ReceiptHandle}");
                Console.WriteLine($"  MD5 of body: {m.MD5OfBody}");
                Console.WriteLine($"  MD5 of message attributes: {m.MD5OfMessageAttributes}");
                Console.WriteLine("  Attributes:");

                foreach (var attr in m.Attributes)
                {
                    Console.WriteLine($"\t {attr.Key}: {attr.Value}");
                }
            });
        }
    }

    // snippet-end:[SQS.dotnetv3.ReceiveFromQueueExample]
}
