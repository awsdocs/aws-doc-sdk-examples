// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Uses Amazon Simple Queue Service (Amazon SQS) to retrieve attributes
/// of the Amazon SQS queue. This example was created using AWS SDK for
/// .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace GetQueueAttributesExample
{
    // snippet-start:[SQS.dotnetv3.GetQueueAttributesExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.SQS;
    using Amazon.SQS.Model;

    public class GetQueueAttributes
    {
        /// <summary>
        /// Initializes the Amazon SQS client and then uses it to call the
        /// GetQueueAttributesAsync method to retrieve the attributes for the
        /// Amazon SQS queue.
        /// </summary>
        public static async Task Main()
        {
            // If the Amazon SQS message queue is not in the same AWS Region as your
            // default user, you need to provide the AWS Region as a parameter to the
            // client constructor.
            var client = new AmazonSQSClient();

            var queueUrl = "https://sqs.us-east-2.amazonaws.com/0123456789ab/New-Example-Queue";
            var attrs = new List<string>() { "All" };

            var request = new GetQueueAttributesRequest
            {
                QueueUrl = queueUrl,
                AttributeNames = attrs,
            };

            var response = await client.GetQueueAttributesAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                DisplayAttributes(response);
            }
        }

        /// <summary>
        /// Displays the attributes passed to the method on the console.
        /// </summary>
        /// <param name="attrs">The attributes for the Amazon SQS queue.</param>
        public static void DisplayAttributes(GetQueueAttributesResponse attrs)
        {
            Console.WriteLine($"Attributes for queue ARN '{attrs.QueueARN}':");
            Console.WriteLine($"  Approximate number of messages: {attrs.ApproximateNumberOfMessages}");
            Console.WriteLine($"  Approximate number of messages delayed: {attrs.ApproximateNumberOfMessagesDelayed}");
            Console.WriteLine($"  Approximate number of messages not visible: {attrs.ApproximateNumberOfMessagesNotVisible}");
            Console.WriteLine($"  Queue created on: {attrs.CreatedTimestamp}");
            Console.WriteLine($"  Delay seconds: {attrs.DelaySeconds}");
            Console.WriteLine($"  Queue last modified on: {attrs.LastModifiedTimestamp}");
            Console.WriteLine($"  Maximum message size: {attrs.MaximumMessageSize}");
            Console.WriteLine($"  Message retention period: {attrs.MessageRetentionPeriod}");
            Console.WriteLine($"  Visibility timeout: {attrs.VisibilityTimeout}");
            Console.WriteLine($"  Policy: {attrs.Policy}\n");
            Console.WriteLine("  Attributes:");

            foreach (var attr in attrs.Attributes)
            {
                Console.WriteLine($"    {attr.Key}: {attr.Value}");
            }
        }
    }

    // snippet-end:[SQS.dotnetv3.GetQueueAttributesExample]
}
