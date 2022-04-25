// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to receive messages from an existing Amazon Simple Queue Service (Amazon SQS) queue.
///
/// Reads a single message from the SQS queue, displays the message, then deletes it from the queue.
/// Can be used in conjunction with SQSSendMessages to see an example of message flow.
///
/// See the following for more information:
/// https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/ReceiveMessage.html
/// </summary>

// snippet-start:[SQS.dotnetv3.SQSReceiveMessages.complete]
using System;
using System.Threading.Tasks;
using Amazon.SQS;
using Amazon.SQS.Model;

namespace SQSReceiveMessages
{
    class Program
    {
        private const int MaxMessages = 1;
        private const int WaitTime = 2;

        static async Task Main(string[] args)
        {
            // Do some checks on the command-line.
            if (args.Length == 0)
            {
                Console.WriteLine("\nUsage: SQSReceiveMessages queue_url");
                Console.WriteLine("   queue_url - The URL of an existing SQS queue.");
                return;
            }
            if (!args[0].StartsWith("https://sqs."))
            {
                Console.WriteLine("\nThe command-line argument isn't a queue URL:");
                Console.WriteLine($"{args[0]}");
                return;
            }

            // Create the Amazon SQS client.
            var sqsClient = new AmazonSQSClient();

            // (Could verify that the queue exists.)
            // Read messages from the queue and perform appropriate actions.
            Console.WriteLine($"Reading messages from queue\n  {args[0]}");
            Console.WriteLine("Press any key to stop. (Response might be slightly delayed.)");
            do
            {
                var msg = await GetMessage(sqsClient, args[0], WaitTime);
                if (msg.Messages.Count != 0)
                {
                    if (ProcessMessage(msg.Messages[0]))
                        await DeleteMessage(sqsClient, msg.Messages[0], args[0]);
                }
            } while (!Console.KeyAvailable);
        }


        // snippet-start:[SQS.dotnetv3.SQSReceiveMessages.GetMessage]
        //
        // Method to read a message from the given queue.
        // In this example, it gets one message at a time.
        private static async Task<ReceiveMessageResponse> GetMessage(
          IAmazonSQS sqsClient, string qUrl, int waitTime = 0)
        {
            return await sqsClient.ReceiveMessageAsync(new ReceiveMessageRequest
            {
                QueueUrl = qUrl,
                MaxNumberOfMessages = MaxMessages,
                WaitTimeSeconds = waitTime
                // (Could also request attributes, set visibility timeout, etc.)
            });
        }
        // snippet-end:[SQS.dotnetv3.SQSReceiveMessages.GetMessage]


        //
        // Method to process a message.
        // In this example, it simply prints the message.
        private static bool ProcessMessage(Message message)
        {
            Console.WriteLine($"\nMessage body of {message.MessageId}:");
            Console.WriteLine($"{message.Body}");
            return true;
        }


        // snippet-start:[SQS.dotnetv3.SQSReceiveMessages.DeleteMessage]
        //
        // Method to delete a message from a queue.
        private static async Task DeleteMessage(
          IAmazonSQS sqsClient, Message message, string qUrl)
        {
            Console.WriteLine($"\nDeleting message {message.MessageId} from queue...");
            await sqsClient.DeleteMessageAsync(qUrl, message.ReceiptHandle);
        }
        // snippet-end:[SQS.dotnetv3.SQSReceiveMessages.DeleteMessage]

    }
}
// snippet-end:[SQS.dotnetv3.SQSReceiveMessages.complete]
