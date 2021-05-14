// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

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
        private static IAmazonS3 _s3Client;

        private const string BUCKET_NAME = "doc-example-bucket";

        static async Task Main()
        {
            _s3Client = new AmazonS3Client();

            Console.WriteLine($"Listing the objects contained in {BUCKET_NAME}:\n");
            await ListingObjectsAsync(_s3Client, BUCKET_NAME);
        }

        /// <summary>
        /// This method uses a paginator to retrieve the list of objects in an
        /// an Amazon S3 bucket.
        /// </summary>
        /// <param name="client">An Amazon S3 client object.</param>
        /// <param name="bucketName">The name of the S3 bucket whose objects
        /// you want to list.</param>
        static async Task ListingObjectsAsync(IAmazonS3 client, string bucketName)
        {
            var listObjectsV2Paginator = client.Paginators.ListObjectsV2(new ListObjectsV2Request
            {
                BucketName = bucketName
            });

            await foreach(var entry in listObjectsV2Paginator.S3Objects)
            {
                Console.WriteLine($"key = {entry.Key} size = {entry.Size}");
            }
        }
    }
}
