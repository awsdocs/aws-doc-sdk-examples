// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace AuthorizeS3ToSendMessageExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.SQS;

    /// <summary>
    /// Shows how to authorize an Amazon Simple Storage Service (Amazon S3)
    /// bucket to send messages to an Amazon Simple Queue Service (Amazon SQS)
    /// queue. The example was created using the AWS SDK for .NET version 3.7
    /// and .NET Core 5.0.
    /// </summary>
    public class AuthorizeS3ToSendMessage
    {
        /// <summary>
        /// Initializes the Amazon SQS client object and then calls the
        /// AuthorizeS3ToSendMessageAsync method to authorize the named
        /// bucket to send messages in response to S3 events.
        /// </summary>
        public static async Task Main()
        {
            string queueUrl = "https://sqs.us-east-2.amazonaws.com/0123456789ab/Example_Queue";
            string bucketName = "doc-example-bucket";

            // Create an Amazon SQS client object using the
            // default user. If the AWS Region you want to use
            // is different, supply the AWS Region as a parameter.
            IAmazonSQS client = new AmazonSQSClient();

            var queueARN = await client.AuthorizeS3ToSendMessageAsync(queueUrl, bucketName);

            if (!string.IsNullOrEmpty(queueARN))
            {
                Console.WriteLine($"The Amazon S3 bucket: {bucketName} has been successfully authorized.");
                Console.WriteLine($"{bucketName} can now send messages to the queue with ARN: {queueARN}.");
            }
        }
    }
}
