// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
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
        // to reate a new Amazon S3 bucket. The examples uses AWS .NET SDK 3.5 and
        // .NET 5.0

        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Specify the name of the new bucket
        private const string NEW_BUCKET_NAME = "doc-example-bucket";

        static void Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);
            Console.WriteLine($"\nCreating a new bucket, named: {NEW_BUCKET_NAME}.");

            CreatingBucketAsync(_s3Client, NEW_BUCKET_NAME).Wait();

            var bucketLocation = FindBucketLocationAsync(_s3Client, NEW_BUCKET_NAME);

            if(!String.IsNullOrEmpty(bucketLocation.ToString()))
            {
                Console.WriteLine($"\n\nBucket {NEW_BUCKET_NAME} successfully created.");
            }
            else
            {
                Console.WriteLine("The bucket does not exist.");
            }
        }

        /// <summary>
        /// Uses Amazon .NET Framework PutBucketAsync to create a new
        /// Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The client object used to connect to Amazon S3</param>
        /// <param name="bucketName">The name of the bucket to create.</param>
        static async Task CreatingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                // Check to see if the bucket already exists.
                var bucketExists = AmazonS3Util.DoesS3BucketExistV2Async(client, bucketName).Result;
                if (!bucketExists)
                {
                    var putBucketRequest = new PutBucketRequest
                    {
                        BucketName = bucketName,
                        UseClientRegion = true
                    };

                    var putBucketResponse = await client.PutBucketAsync(putBucketRequest);
                }
                else
                {
                    Console.WriteLine($"A bucket with the name \"{bucketName}\" already exists.");
                    return;
                }
                string bucketLocation = await FindBucketLocationAsync(client, bucketName);

            } catch(AmazonS3Exception ex)
            {
                Console.WriteLine($"Error creating bucket: '{ex.Message}'");
            }
        }

        /// <summary>
        /// Finds the bucket location for bucketName using the SDK method
        /// GetBucketLocationAsync
        /// </summary>
        /// <param name="client">The Amazon S3 client used to connect to
        /// Amazon S3.</param>
        /// <param name="bucketName">The name of the bucket for which
        /// we want to retrieve the location.</param>
        /// <returns>Returns a string representing the location of the
        /// bucket</returns>
        static async Task<string> FindBucketLocationAsync(IAmazonS3 client, string bucketName)
        {
            string bucketLocation = null;
            try
            {
                var request = new GetBucketLocationRequest()
                {
                    BucketName = bucketName
                };
                GetBucketLocationResponse response = await client.GetBucketLocationAsync(request);
                bucketLocation = response.Location.ToString();
            } catch(AmazonS3Exception ex)
            {
                Console.WriteLine($"Error locating bucket: '{ex.Message}'");
            }
            return bucketLocation;
        }
    }
}
// snippet-end:[S3.dotnet35.CreateBucket]