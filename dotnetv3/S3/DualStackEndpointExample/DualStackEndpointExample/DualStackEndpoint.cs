// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DualStackEndpointExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;
    using Amazon.S3.Model;

    /// <summary>
    /// This example configures the endpoint calculation for a service to go
    /// to a dual stack (ipv6 enabled) endpoint for the configured AWS region.
    /// The example was created using the AWS SDK for .NET verion 3.7 and .NET
    /// Core version 5.0.
    /// </summary>
    public class DualStackEndpoint
    {
        private static readonly string BucketName = "doc-example-bucket";

        public static async Task Main()
        {
            var region = RegionEndpoint.USWest2;
            var config = new AmazonS3Config
            {
                UseDualstackEndpoint = true,
                RegionEndpoint = region,
            };

            IAmazonS3 client = new AmazonS3Client(config);
            Console.WriteLine($"Listing objects stored in {BucketName}");
            await ListingObjectsAsync(client);
        }

        private static async Task ListingObjectsAsync(IAmazonS3 client)
        {
            try
            {
                var request = new ListObjectsV2Request
                {
                    BucketName = BucketName,
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
}
