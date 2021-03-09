// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[S3.dotnet35.CreateBucket]

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.S3.Util;
using System;
using System.Threading.Tasks;

namespace CreateBucket
{
    public class CreateBucket
    {
        // This example shows how to use Amazon Simple Storage Service (Amazon S3)
        // to create a new Amazon S3 bucket. The examples uses AWS SDK for .NET 3.5 and
        // .NET 5.0.

        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USEast1; // RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Specify the name of the new bucket.
        private const string NEW_BUCKET_NAME = "igsmith-bucket2"; // "doc-example-bucket";

        static async Task Main()
        {

            _s3Client = new AmazonS3Client(BUCKET_REGION);
            Console.WriteLine($"\nCreating a new bucket, named: {NEW_BUCKET_NAME}.");

            await CreatingBucketAsync(_s3Client, NEW_BUCKET_NAME);

        }

        /// <summary>
        /// Uses Amazon SDK for .NET PutBucketAsync to create a new
        /// Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The client object used to connect to Amazon S3.</param>
        /// <param name="bucketName">The name of the bucket to create.</param>
        static async Task CreatingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var putBucketRequest = new PutBucketRequest
                {
                    BucketName = bucketName,
                    UseClientRegion = true
                };

                var putBucketResponse = await client.PutBucketAsync(putBucketRequest);

            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error creating bucket: '{ex.Message}'");
            }
        }
    }
}
// snippet-end:[S3.dotnet35.CreateBucket]