// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Uses Amazon Simple Queue Service (Amazon SQS) to send a message to an
/// Amazon SQS queue. This examples uses the AWS SDK for .NET version 3.7
/// and .NET Core 5.0.
/// </summary>
namespace SendMessageToQueueExample
{
    // snippet-start:[SQS.dotnetv3.SendMessageToQueueExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.SQS;
    using Amazon.SQS.Model;

    public class SendMessageToQueue
    {
        /// <summary>
        /// Initialize the Amazon SQS client object and use the
        /// SendMessageAsync method to send a message to an Amazon SQS queue.
        /// </summary>
        public static async Task Main()
        {
            string messageBody = "This is a sample message to send to the example queue.";
            string queueUrl = "https://sqs.us-east-2.amazonaws.com/0123456789ab/Example_Queue";

            // Create an Amazon SQS client object using the
            // default user. If the AWS Region you want to use
            // is different, supply the AWS Region as a parameter.
            IAmazonSQS client = new AmazonSQSClient();

            var request = new SendMessageRequest
            {
                MessageBody = messageBody,
                QueueUrl = queueUrl,
            };

            var response = await client.SendMessageAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully sent message. Message ID: {response.MessageId}");
            }
            else
            {
                Console.WriteLine("Could not send message.");
            }
        }
    }

    // snippet-end:[SQS.dotnetv3.SendMessageToQueueExample]
}
