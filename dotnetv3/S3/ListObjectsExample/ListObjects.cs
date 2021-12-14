// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// List the objects in an Amazon Simple Storage Service (Amazon S3) bucket.
/// The example was created using the AWS SDK for .NET version 3.7 and
/// .NET Core 5.0.
/// </summary>
namespace ListObjectsExample
{
    // snippet-start:[S3.dotNET.ListObjectsExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class ListObjects
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            IAmazonS3 client;

            using (client = new AmazonS3Client())
            {
                Console.WriteLine($"Listing objects stored in the bucket {bucketName}.");
                await ListingObjectsAsync(client, bucketName);
            }
        }

        /// <summary>
        /// Uses the client object to get a list of the objects in the Amazon
        /// S3 bucket in the bucketName parameter.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used to call
        /// the ListObjectsAsync method.</param>
        /// <param name="bucketName">The bucket name for which you want to
        /// retrieve a list of objects.</param>
        public static async Task ListingObjectsAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                ListObjectsV2Request request = new()
                {
                    BucketName = bucketName,
                    MaxKeys = 5,
                };

                var response = new ListObjectsV2Response();

                do
                {
                    response = await client.ListObjectsV2Async(request);

                    response.S3Objects
                        .ForEach(obj => Console.WriteLine($"{obj.Key,-35}{obj.LastModified.ToShortDateString(),10}{obj.Size,10}"));

                    // If the response is truncated, set the request ContinuationToken
                    // from the NextContinuationToken property of the response.
                    request.ContinuationToken = response.NextContinuationToken;
                } while (response.IsTruncated);
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error encountered on server. Message:'{ex.Message}' getting list of objects.");
            }
        }
    }

    // snippet-end:[S3.dotNET.ListObjectsExample]
}
