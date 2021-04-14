// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace ListBuckets
{
    class ListBuckets
    {
        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        static async Task Main(string[] args)
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);
            var response = await GetBuckets(_s3Client);
            DisplayBucketList(response.Buckets);
        }

        /// <summary>
        /// Get a list of the buckets owned by the default user.
        /// </summary>
        /// <param name="client">An initialized Amazon S3 client object.</param>
        /// <returns>The response from the ListingBuckets call that contains a
        /// list of the buckets owned by the default user.</returns>
        static public async Task<ListBucketsResponse> GetBuckets(IAmazonS3 client)
        {
            return await client.ListBucketsAsync();
        }

        /// <summary>
        /// This method lists the name and creation date for the buckets in
        /// the passed List of S3 buckets.
        /// </summary>
        /// <param name="bucketList">A List of S3 bucket objects.</param>
        static public void DisplayBucketList(List<S3Bucket> bucketList)
        {
            bucketList
                .ForEach(b => Console.WriteLine($"Bucket name: {b.BucketName}, created on: {b.CreationDate}"));
        }
    }
}
