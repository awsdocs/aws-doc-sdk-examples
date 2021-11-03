// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListObjectsExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class ListObjects
    {
        public static async Task Main()
        {
            string bucketName = "igsmithbucket"; // "doc-example-bucket";
            IAmazonS3 client;

            using (client = new AmazonS3Client())
            {
                Console.WriteLine($"Listing objects stored in the bucket {bucketName}.");
                await ListingObjectsAsync(client, bucketName);
            }
        }

        /// <summary>
        /// Uses the client object to get a  list of the objects in the S3
        /// bucket in the bucketName parameter.
        /// </summary>
        /// <param name="client">The initialized S3 client obect used to call
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
}
