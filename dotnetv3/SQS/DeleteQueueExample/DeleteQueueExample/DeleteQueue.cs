// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to delete an Amazon Simple Queue Service (Amazon SQS) queue.
/// This example was created using the AWS SDK for .NET version 3.7 and
/// .NET Core 5.0.
/// </summary>
namespace DeleteQueueExample
{
    // snippet-start:[SQS.dotnetv3.DeleteQueueExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.SQS;

    public class DeleteQueue
    {
        /// <summary>
        /// Initializes the Amazon SQS client object and then calls the
        /// DeleteQueueAsync method to delete the queue.
        /// </summary>
        public static async Task Main()
        {
            // If the Amazon SQS message queue is not in the same AWS Region as your
            // default user, you need to provide the AWS Region as a parameter to the
            // client constructor.
            var client = new AmazonSQSClient();

            string queueUrl = "https://sqs.us-east-2.amazonaws.com/0123456789ab/New-Example-Queue";

            var response = await client.DeleteQueueAsync(queueUrl);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Successfully deleted the queue.");
            }
            else
            {
                Console.WriteLine("Could not delete the crew.");
            }
        }
    }

    // snippet-end:[SQS.dotnetv3.DeleteQueueExample]
}
