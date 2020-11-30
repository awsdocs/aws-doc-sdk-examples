// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[S3.dotnet35.ListObjects]

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

namespace ListObjects
{
    // The following example lists objects in an Amazon Simple Storage
    // Service (Amazon S3) bucket. It was created using AWS SDK for .NET 3.5
    // and .NET 5.0.
    public class ListObjects
    {
        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        private const string BUCKET_NAME = "doc-example-bucket";

        static void Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

            Console.WriteLine($"Listing the objects contained in {BUCKET_NAME}:\n");
            ListingObjectsAsync(_s3Client, BUCKET_NAME).Wait();
        }

        static async Task ListingObjectsAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                ListObjectsV2Request request = new ListObjectsV2Request
                {
                    BucketName = bucketName,
                    MaxKeys = 10
                };
                ListObjectsV2Response response;
                do
                {
                    response = await client.ListObjectsV2Async(request);

                    // Process the response.
                    foreach (S3Object entry in response.S3Objects)
                    {
                        Console.WriteLine($"key = {entry.Key} size = {entry.Size}");
                    }
                    request.ContinuationToken = response.NextContinuationToken;
                } while (response.IsTruncated);
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error listing bucket objects. Exception: {ex.ToString()}");
                Console.ReadKey();
            }

        }
    }
}
// snippet-end:[S3.dotnet35.ListObjects]