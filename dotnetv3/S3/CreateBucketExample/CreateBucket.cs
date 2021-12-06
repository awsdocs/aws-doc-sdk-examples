// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example shows how to use Amazon Simple Storage Service (Amazon S3)
/// to create a new Amazon S3 bucket. The examples uses AWS SDK for .NET 3.5 and
/// .NET 5.0.
/// </summary>
namespace CreateBucket
{
    // snippet-start:[S3.dotnetv3.CreateBucketExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class CreateBucket
    {
        public static async Task Main()
        {

            // Specify a name for the new bucket.
            const string newBucketName = "doc-example-bucket";

            var client = new AmazonS3Client();
            Console.WriteLine($"\nCreating a new bucket, named: {newBucketName}.");

            await CreatingBucketAsync(client, newBucketName);

        }

        /// <summary>
        /// Uses Amazon SDK for .NET PutBucketAsync to create a new
        /// Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The client object used to connect to Amazon S3.</param>
        /// <param name="bucketName">The name of the bucket to create.</param>
        public static async Task CreatingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var putBucketRequest = new PutBucketRequest
                {
                    BucketName = bucketName,
                    UseClientRegion = true,
                };

                var putBucketResponse = await client.PutBucketAsync(putBucketRequest);

            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error creating bucket: '{ex.Message}'");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.CreateBucketExample]
}
