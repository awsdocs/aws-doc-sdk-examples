// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to create a new Amazon Simple Queue Service (Amazon SQS)
/// queue. The example was created using the AWS SDK for .NET version 3.7
/// and .NET Core 5.0.
/// </summary>
namespace CreateQueueExample
{
    // snippet-start:[SQS.dotnetv3.CreateQueueExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.SQS;
    using Amazon.SQS.Model;

    public class CreateQueue
    {
        /// <summary>
        /// Initializes the Amazon SQS client object and then calls the
        /// CreateQueueAsync method to create the new queue. If the call is
        /// successful, it displays the URL of the new queue on the console.
        /// </summary>
        public static async Task Main()
        {
            // If the Amazon SQS message queue is not in the same AWS Region as your
            // default user, you need to provide the AWS Region as a parameter to the
            // client constructor.
            var client = new AmazonSQSClient();

            string queueName = "New-Example-Queue";
            int maxMessage = 256 * 1024;
            var attrs = new Dictionary<string, string>
            {
                {
                    QueueAttributeName.DelaySeconds,
                    TimeSpan.FromSeconds(5).TotalSeconds.ToString()
                },
                {
                    QueueAttributeName.MaximumMessageSize,
                    maxMessage.ToString()
                },
                {
                    QueueAttributeName.MessageRetentionPeriod,
                    TimeSpan.FromDays(4).TotalSeconds.ToString()
                },
                {
                    QueueAttributeName.ReceiveMessageWaitTimeSeconds,
                    TimeSpan.FromSeconds(5).TotalSeconds.ToString()
                },
                {
                    QueueAttributeName.VisibilityTimeout,
                    TimeSpan.FromHours(12).TotalSeconds.ToString()
                },
            };

            var request = new CreateQueueRequest
            {
                Attributes = attrs,
                QueueName = queueName,
            };

            var response = await client.CreateQueueAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Successfully created Amazon SQS queue.");
                Console.WriteLine($"Queue URL: {response.QueueUrl}");
            }
        }
    }

    // snippet-end:[SQS.dotnetv3.CreateQueueExample]
}
