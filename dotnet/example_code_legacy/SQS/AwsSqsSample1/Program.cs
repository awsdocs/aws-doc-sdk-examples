//snippet-sourcedescription:[AwsSqsSample1 example demonstrates how to use the various queue and message operations on the Amazon SQS client.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
﻿/*******************************************************************************
* Copyright 2009-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Amazon;
using Amazon.SQS;
using Amazon.SQS.Model;

namespace AwsSqsSample1
{
    class Program
    {
        public static void Main(string[] args)
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();
        }
        public static async Task ChangeMessageVisibilityBatch(IAmazonSQS client, string url)
        {
            // Receive messages.
            var msgRequest = new ReceiveMessageRequest
            {
                AttributeNames = new List<string>() { "All" },
                QueueUrl = url
            };

            var msgResponse = await client.ReceiveMessageAsync(msgRequest);

            // Change visibility timeout for each message.
            if (msgResponse.Messages.Count > 0)
            {
                var entries = new List<ChangeMessageVisibilityBatchRequestEntry>();

                int numMessages = 0;

                foreach (var message in msgResponse.Messages)
                {
                    numMessages += 1;

                    var entry = new ChangeMessageVisibilityBatchRequestEntry
                    {
                        Id = "Entry" + numMessages.ToString(),
                        ReceiptHandle = message.ReceiptHandle,
                        VisibilityTimeout = (int)TimeSpan.FromMinutes(10).TotalSeconds
                    };

                    entries.Add(entry);
                }

                var batRequest = new ChangeMessageVisibilityBatchRequest
                {
                    Entries = entries,
                    QueueUrl = url
                };

                var batResponse = await client.ChangeMessageVisibilityBatchAsync(batRequest);

                Console.WriteLine("Successes: " + batResponse.Successful.Count +
                  ", Failures: " + batResponse.Failed.Count);

                if (batResponse.Successful.Count > 0)
                {
                    foreach (var success in batResponse.Successful)
                    {
                        Console.WriteLine("  Success ID " + success.Id);
                    }
                }

                if (batResponse.Failed.Count > 0)
                {
                    foreach (var fail in batResponse.Failed)
                    {
                        Console.WriteLine("  Failure ID " + fail.Id + ":");
                        Console.WriteLine("    Code: " + fail.Code);
                        Console.WriteLine("    Message: " + fail.Message);
                        Console.WriteLine("    Sender's fault?: " + fail.SenderFault);
                    }
                }
            }

        }
        public async Task DeadLetterQueueExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var setQueueAttributeRequest = new SetQueueAttributesRequest
            {
                Attributes = new Dictionary<string, string>
                {
                    {"RedrivePolicy",   @"{ ""deadLetterTargetArn"" : ""DEAD_LETTER_QUEUE_ARN"", ""maxReceiveCount"" : ""10""}" }
                },
                QueueUrl = "SOURCE_QUEUE_URL"
            };

            await client.SetQueueAttributesAsync(setQueueAttributeRequest);
        }

        public async Task ListQueueExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            ListQueuesResponse response = await client.ListQueuesAsync(new ListQueuesRequest());
            foreach (var queueUrl in response.QueueUrls)
            {
                Console.WriteLine(queueUrl);
            }
        }

        public async Task CreateQueueExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new CreateQueueRequest
            {
                QueueName = "SQS_QUEUE_NAME",
                Attributes = new Dictionary<string, string>
                {
                    { "DelaySeconds", "60"},
                    { "MessageRetentionPeriod", "86400"}
                }
            };


            var response = await client.CreateQueueAsync(request);
            Console.WriteLine("Created a queue with URL : {0}", response.QueueUrl);
        }

        public async Task GetQueueUrlExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new GetQueueUrlRequest
            {
                QueueName = "SQS_QUEUE_NAME"
            };

            GetQueueUrlResponse response = await client.GetQueueUrlAsync(request);
            Console.WriteLine("The SQS queue's URL is {1}", response.QueueUrl);
        }

        public async Task DeleteQueueExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new DeleteQueueRequest
            {
                QueueUrl = "SQS_QUEUE_URL"
            };

            await client.DeleteQueueAsync(request);
        }

        public async Task SendMssageExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var sendMessageRequest = new SendMessageRequest
            {
                DelaySeconds = 10,
                MessageAttributes = new Dictionary<string, MessageAttributeValue>
                {
                    {"Title",   new MessageAttributeValue{DataType = "String", StringValue = "The Whistler"}},
                    {"Author",  new MessageAttributeValue{DataType = "String", StringValue = "John Grisham"}},
                    {"WeeksOn", new MessageAttributeValue{DataType = "Number", StringValue = "6"}}
                },
                MessageBody = "Information about current NY Times fiction bestseller for week of 12/11/2016.",
                QueueUrl = "SQS_QUEUE_URL"
            };

            var response = await client.SendMessageAsync(sendMessageRequest);
            Console.WriteLine("Sent a message with id : {0}", response.MessageId);
        }

        public async Task SendMessageBatchExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var sendMessageBatchRequest = new SendMessageBatchRequest
            {
                Entries = new List<SendMessageBatchRequestEntry>
                {
                    new SendMessageBatchRequestEntry("message1", "FirstMessageContent"),
                    new SendMessageBatchRequestEntry("message2", "SecondMessageContent"),
                    new SendMessageBatchRequestEntry("message3", "ThirdMessageContent")
                },
                QueueUrl = "SQS_QUEUE_URL"
            };

            var response = await client.SendMessageBatchAsync(sendMessageBatchRequest);

            Console.WriteLine("Messages successfully sent:");
            foreach (var success in response.Successful)
            {
                Console.WriteLine("    Message id : {0}", success.MessageId);
                Console.WriteLine("    Message content MD5 : {0}", success.MD5OfMessageBody);
            }

            Console.WriteLine("Messages failed to send:");
            foreach (var failed in response.Failed)
            {
                Console.WriteLine("    Message id : {0}", failed.Id);
                Console.WriteLine("    Message content : {0}", failed.Message);
                Console.WriteLine("    Sender's fault? : {0}", failed.SenderFault);
            }
        }

        public async Task ReceiveAndDeleteMessageExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();
            string queueUrl = "SQS_QUEUE_URL";

            //
            // Receive a single message
            //
            var receiveMessageRequest = new ReceiveMessageRequest
            {
                AttributeNames = { "SentTimestamp" },
                MaxNumberOfMessages = 1,
                MessageAttributeNames = { "All" },
                QueueUrl = queueUrl,
                VisibilityTimeout = 0,
                WaitTimeSeconds = 0
            };

            var receiveMessageResponse = await client.ReceiveMessageAsync(receiveMessageRequest);

            //
            // Delete the received single message
            //
            var deleteMessageRequest = new DeleteMessageRequest
            {
                QueueUrl = queueUrl,
                ReceiptHandle = receiveMessageResponse.Messages[0].ReceiptHandle
            };

            await client.DeleteMessageAsync(deleteMessageRequest);
        }
        public async Task ChangeMessageVisibility()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();
            string queueUrl = "SQS_QUEUE_URL";

            var receiveMessageRequest = new ReceiveMessageRequest
            {
                AttributeNames = { "SentTimestamp" },
                MaxNumberOfMessages = 1,
                MessageAttributeNames = { "All" },
                QueueUrl = queueUrl
            };

            var response = await client.ReceiveMessageAsync(receiveMessageRequest);

            var changeMessageVisibilityRequest = new ChangeMessageVisibilityRequest
            {
                QueueUrl = queueUrl,
                ReceiptHandle = response.Messages[0].ReceiptHandle,
                VisibilityTimeout = 36000, // 10 hour timeout
            };

            await client.ChangeMessageVisibilityAsync(changeMessageVisibilityRequest);
        }
        public async Task ChangeMessageVisibilityBatch()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();
            string queueUrl = "SQS_QUEUE_URL";

            var receiveMessageRequest = new ReceiveMessageRequest
            {
                AttributeNames = { "SentTimestamp" },
                MaxNumberOfMessages = 5,
                MessageAttributeNames = { "All" },
                QueueUrl = queueUrl
            };

            var receiveMessageResponse = await client.ReceiveMessageAsync(receiveMessageRequest);
            List<ChangeMessageVisibilityBatchRequestEntry> entries = new List<ChangeMessageVisibilityBatchRequestEntry>();

            foreach (var message in receiveMessageResponse.Messages)
            {
                entries.Add(new ChangeMessageVisibilityBatchRequestEntry
                {
                    Id = message.MessageId,
                    ReceiptHandle = message.ReceiptHandle,
                    VisibilityTimeout = 36000, // 10 hour timeout
                });
            }

            var changeMessageVisibilityBatchResponse = await client.ChangeMessageVisibilityBatchAsync(new ChangeMessageVisibilityBatchRequest
            {
                QueueUrl = queueUrl,
                Entries = entries
            });

            Console.WriteLine("Messages successfully changed:");
            foreach (var success in changeMessageVisibilityBatchResponse.Successful)
            {
                Console.WriteLine("    Message id : {0}", success.Id);
            }

            Console.WriteLine("Messages failed to change:");
            foreach (var failed in changeMessageVisibilityBatchResponse.Failed)
            {
                Console.WriteLine("    Message id : {0}", failed.Id);
                Console.WriteLine("    Sender's fault? : {0}", failed.SenderFault);
            }
        }
        public async Task OnCreateQueueExample()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new CreateQueueRequest
            {
                QueueName = "SQS_QUEUE_NAME",
                Attributes = new Dictionary<string, string>
                {
                    { "ReceiveMessageWaitTimeSeconds", "20"}
                }
            };

            var response = await client.CreateQueueAsync(request);
            Console.WriteLine("Created a queue with URL : {0}", response.QueueUrl);
        }

        public async Task OnExistingQueue()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new SetQueueAttributesRequest
            {
                Attributes = new Dictionary<string, string>
                {
                    { "ReceiveMessageWaitTimeSeconds", "20"}
                },
                QueueUrl = "SQS_QUEUE_URL"
            };

            var response = await client.SetQueueAttributesAsync(request);
        }

        public async Task OnMessageReceipt()
        {
            // Create service client using the SDK's default logic for determining AWS credentials and region to use.
            // For information configuring service clients checkout the .NET developer guide: https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
            AmazonSQSClient client = new AmazonSQSClient();

            var request = new ReceiveMessageRequest
            {
                AttributeNames = { "SentTimestamp" },
                MaxNumberOfMessages = 1,
                MessageAttributeNames = { "All" },
                QueueUrl = "SQS_QUEUE_URL",
                WaitTimeSeconds = 20
            };

            var response = await client.ReceiveMessageAsync(request);
        }
    }
}
