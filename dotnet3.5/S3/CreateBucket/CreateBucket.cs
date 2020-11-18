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
        // Specify your bucket region (an example region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Specify the name of the new bucket
        private const string NEW_BUCKET_NAME = "*** provide the name of the bucket with source object ***";

        static void Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);
            Console.WriteLine($"\nCreating a new bucket, named: {NEW_BUCKET_NAME}.");

            CreatingBucketAsync(_s3Client, NEW_BUCKET_NAME).Wait();

            // If FindBucketLocationAsyn doesn't return a value, the creation failed.
            var bucketLocation = FindBucketLocationAsync(_s3Client, NEW_BUCKET_NAME);

            if(!String.IsNullOrEmpty(bucketLocation.ToString()))
            {
                Console.WriteLine($"\n\nBucket {NEW_BUCKET_NAME} created at {bucketLocation}.");
            } else
            {
                Console.WriteLine("Could not create bucket.");
            }
        }

        static async Task CreatingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                // Check to see if the bucket already exists.
                if (!await AmazonS3Util.DoesS3BucketExistV2Async(client, bucketName))
                {
                    var putBucketRequest = new PutBucketRequest
                    {
                        BucketName = bucketName,
                        UseClientRegion = true
                    };

                    var putBucketResponse = await client.PutBucketAsync(putBucketRequest);
                }

                // Retrieve the bucket location.
                string bucketLocation = await FindBucketLocationAsync(client, bucketName);
            } catch(AmazonS3Exception ex)
            {
                Console.WriteLine($"Error creating bucket: '{ex.Message}'");
            }
        }

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