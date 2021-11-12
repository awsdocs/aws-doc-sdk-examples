// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows how to enable logging on an Amazon Simple Storage
/// Service (Amazon S3) bucket. You need to have two Amazon S3 buckets for
/// this example. The first is the bucket for which you wish to enable
/// logging, and the second is the location where you want to store the
/// logs. The example was created using the AWS SDK for .NET version 3.7
/// and .NET Core 5.0.
/// </summary>
namespace ServerAccessLoggingExample
{
    // snippet-start:[S3.dotnetv3.ServerAccessLoggingExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class ServerAccessLogging
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            string logBucketName = "doc-example-logs-bucket";
            string logObjectKeyPrefix = "Logs";

            // If the AWS Region defined for your default user is different
            // from the Region where your Amazon S3 bucket is located,
            // pass the Region name to the Amazon S3 client object's constructor.
            // For example: RegionEndpoint.USWest2 or RegionEndpoint.USEast2.
            IAmazonS3 client = new AmazonS3Client();

            try
            {
                // Grant Log Delivery group permission to write log entries
                // to the target bucket.
                await GrantPermissionsToWriteLogsAsync(client, logBucketName);

                // Enable logging on the source bucket.
                await EnableLoggingAsync(client, bucketName, logBucketName, logObjectKeyPrefix);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error: {e.Message}");
            }
        }

        /// <summary>
        /// This method grants appropriate permissions for logging to the
        /// Amazon S3 bucket where the logs will be stored.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client which will be used
        /// to create and apply an ACL for the logging bucket.</param>
        /// <param name="logBucketName">The name of the bucket where logging
        /// information will be stored.</param>
        public static async Task GrantPermissionsToWriteLogsAsync(IAmazonS3 client, string logBucketName)
        {
            var aclResponse = await client.GetACLAsync(new GetACLRequest { BucketName = logBucketName });
            var bucketACL = aclResponse.AccessControlList;

            bucketACL.AddGrant(new S3Grantee { URI = "http://acs.amazonaws.com/groups/s3/LogDelivery" }, S3Permission.WRITE);
            bucketACL.AddGrant(new S3Grantee { URI = "http://acs.amazonaws.com/groups/s3/LogDelivery" }, S3Permission.READ_ACP);
            var setACLRequest = new PutACLRequest
            {
                AccessControlList = bucketACL,
                BucketName = logBucketName,
            };
            await client.PutACLAsync(setACLRequest);
        }

        /// <summary>
        /// This method enables logging for an Amazon S3 bucket. Logs will be stored
        /// in the bucket you selected for logging. Each the logObjectKeyPrefix
        /// will be prepended to each log oject.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client which will be used
        /// to configure and apply logging to the selected Amazon S3 bucket.</param>
        /// <param name="bucketName">The name of the Amazon S3 bucket for which you
        /// wish to enable logging.</param>
        /// <param name="logBucketName">The name of the Amazon S3 bucket where logging
        /// information will be stored.</param>
        /// <param name="logObjectKeyPrefix">The prefix to prepend to each
        /// object key</param>
        public static async Task EnableLoggingAsync(
            IAmazonS3 client,
            string bucketName,
            string logBucketName,
            string logObjectKeyPrefix)
        {
            var loggingConfig = new S3BucketLoggingConfig
            {
                TargetBucketName = logBucketName,
                TargetPrefix = logObjectKeyPrefix,
            };

            var putBucketLoggingRequest = new PutBucketLoggingRequest
            {
                BucketName = bucketName,
                LoggingConfig = loggingConfig,
            };
            await client.PutBucketLoggingAsync(putBucketLoggingRequest);
        }
    }

    // snippet-end:[S3.dotnetv3.ServerAccessLoggingExample]
}
