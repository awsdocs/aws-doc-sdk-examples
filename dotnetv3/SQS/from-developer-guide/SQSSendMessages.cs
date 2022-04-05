// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to send messages to an existing Amazon Simple Queue Service (Amazon SQS) queue.
///
/// Sends a single message to the SQS queue, then a batch of messages, then allows the user
/// to send their own messages.
/// Can be used in conjunction with SQSReceiveMessages to see an example of message flow.
///
/// See the following for more information:
/// https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/SendMessage.html
/// </summary>

// snippet-start:[SQS.dotnetv3.SQSSendMessages.complete]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.SQS;
using Amazon.SQS.Model;

namespace SQSSendMessages
{
    class Program
    {
        // Some example messages to send to the queue.
        private const string JsonMessage = "{\"product\":[{\"name\":\"Product A\",\"price\": \"32\"},{\"name\": \"Product B\",\"price\": \"27\"}]}";
        private const string XmlMessage = "<products><product name=\"Product A\" price=\"32\" /><product name=\"Product B\" price=\"27\" /></products>";
        private const string CustomMessage = "||product|Product A|32||product|Product B|27||";
        private const string TextMessage = "Just a plain text message.";

        static async Task Main(string[] args)
        {
            // Do some checks for the command-line.
            if (args.Length == 0)
            {
                Console.WriteLine("\nUsage: SQSSendMessages queue_url");
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
            // Send some example messages to the given queue.
            // A single message:
            await SendMessage(sqsClient, args[0], JsonMessage);

            // A batch of messages:
            var batchMessages = new List<SendMessageBatchRequestEntry>{
                new SendMessageBatchRequestEntry("xmlMsg", XmlMessage),
                new SendMessageBatchRequestEntry("customeMsg", CustomMessage),
                new SendMessageBatchRequestEntry("textMsg", TextMessage)};
            await SendMessageBatch(sqsClient, args[0], batchMessages);

            // Let the user send their own messages or quit:
            await InteractWithUser(sqsClient, args[0]);

            // Delete all messages that are still in the queue.
            await DeleteAllMessages(sqsClient, args[0]);
        }


        // snippet-start:[SQS.dotnetv3.SQSSendMessages.SendMessage]
        //
        // Method to put a message on a queue.
        // Could be expanded to include message attributes, etc., in a
        // SendMessageRequest object.
        private static async Task SendMessage(
          IAmazonSQS sqsClient, string qUrl, string messageBody)
        {
            SendMessageResponse responseSendMsg =
              await sqsClient.SendMessageAsync(qUrl, messageBody);
            Console.WriteLine($"Message added to queue\n  {qUrl}");
            Console.WriteLine($"HttpStatusCode: {responseSendMsg.HttpStatusCode}");
        }
        // snippet-end:[SQS.dotnetv3.SQSSendMessages.SendMessage]


        // snippet-start:[SQS.dotnetv3.SQSSendMessages.SendMessageBatch]
        //
        // Method to put a batch of messages on a queue.
        // Could be expanded to include message attributes, etc., in the
        // SendMessageBatchRequestEntry objects.
        private static async Task SendMessageBatch(
          IAmazonSQS sqsClient, string qUrl, List<SendMessageBatchRequestEntry> messages)
        {
            Console.WriteLine($"\nSending a batch of messages to queue\n  {qUrl}");
            SendMessageBatchResponse responseSendBatch =
              await sqsClient.SendMessageBatchAsync(qUrl, messages);
            // Could test responseSendBatch.Failed here
            foreach (SendMessageBatchResultEntry entry in responseSendBatch.Successful)
                Console.WriteLine($"Message {entry.Id} successfully queued.");
        }
        // snippet-end:[SQS.dotnetv3.SQSSendMessages.SendMessageBatch]


        //
        // Method to get input from the user.
        // They can provide messages to put in the queue or exit the application.
        private static async Task InteractWithUser(IAmazonSQS sqsClient, string qUrl)
        {
            string response;
            while (true)
            {
                // Get the user's input.
                Console.WriteLine("\nType a message for the queue or \"exit\" to quit:");
                response = Console.ReadLine();
                if (response.ToLower() == "exit") break;

                // Put the user's message in the queue.
                await SendMessage(sqsClient, qUrl, response);
            }
        }


        // snippet-start:[SQS.dotnetv3.SQSSendMessages.DeleteAllMessages]
        //
        // Method to delete all messages from the queue.
        private static async Task DeleteAllMessages(IAmazonSQS sqsClient, string qUrl)
        {
            Console.WriteLine($"\nPurging messages from queue\n  {qUrl}...");
            PurgeQueueResponse responsePurge = await sqsClient.PurgeQueueAsync(qUrl);
            Console.WriteLine($"HttpStatusCode: {responsePurge.HttpStatusCode}");
        }
        // snippet-end:[SQS.dotnetv3.SQSSendMessages.DeleteAllMessages]

    }
}
// snippet-end:[SQS.dotnetv3.SQSSendMessages.complete]
