// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example configures the endpoint calculation for a service to go
/// to a dual stack (IPv6 enabled) endpoint for the configured AWS Region.
/// The example was created using the AWS SDK for .NET verion 3.7 and .NET
/// Core version 5.0.
/// </summary>
namespace DualStackEndpointExample
{
    // snippet-start:[S3.dotnetv3.DualStackEndpointExample]
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class DualStackEndpoint
    {
        public static async Task Main()
        {
            const string bucketName = "doc-example-bucket";
            var region = RegionEndpoint.USWest2;

            var config = new AmazonS3Config
            {
                UseDualstackEndpoint = true,
                RegionEndpoint = region,
            };
            IAmazonS3 client = new AmazonS3Client(config);

            Console.WriteLine($"Listing objects stored in {bucketName}");
            await ListingObjectsAsync(client, bucketName);
        }

        private static async Task ListingObjectsAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var request = new ListObjectsV2Request
                {
                    BucketName = bucketName,
                    MaxKeys = 10,
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

                    Console.WriteLine("Next Continuation Token: {response.NextContinuationToken}");
                    request.ContinuationToken = response.NextContinuationToken;
                }
                while (response.IsTruncated);
            }
            catch (AmazonS3Exception amazonS3Exception)
            {
                Console.WriteLine($"An AmazonS3Exception was thrown. Exception: {amazonS3Exception.Message}");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.DualStackEndpointExample]
}
