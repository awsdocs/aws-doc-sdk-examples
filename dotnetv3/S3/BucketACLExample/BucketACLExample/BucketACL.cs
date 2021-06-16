// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace BucketACLExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    /// <summary>
    /// This example shows how to work with Access control lists (ACLs) in an
    /// Amazon Simple Storage Service (Amazon S3) bucket. The example was
    /// created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class BucketACL
    {
        public static async Task Main()
        {
            const string NewBucketName = "irene-new-doc-example-bucket";

            IAmazonS3 client = new AmazonS3Client();
            var success = await CreateBucketUseCannedACLAsync(client, NewBucketName);

            if (success)
            {
                Console.WriteLine("S3 bucket created and ACL applied successfully.");
            }
        }

        /// <summary>
        /// Creates an Amazon S3 bucket and adds and an ACL to control
        /// access to the bucket and the objects stored in it.
        /// </summary>
        /// <param name="client">The initialized client object used to create
        /// the ACL, create an S3 bucket, and then apply the ACL to the bucket.
        /// </param>
        /// <param name="newBucketName">The name of the bucket to create.</param>
        /// <returns>A boolean value indicating success or failure.</returns>
        public static async Task<bool> CreateBucketUseCannedACLAsync(IAmazonS3 client, string newBucketName)
        {
            try
            {
                // Create a new S3 bucket with Canned ACL.
                PutBucketRequest putBucketRequest = new ()
                {
                    BucketName = newBucketName,
                    BucketRegion = S3Region.USE2, // S3Region.US,
                    CannedACL = S3CannedACL.LogDeliveryWrite,
                };
                PutBucketResponse putBucketResponse = await client.PutBucketAsync(putBucketRequest);

                // Retrieve bucket ACL to show that the Access Control List
                // was properly applied to the new bucket.
                GetACLResponse getACLResponse = await client.GetACLAsync(new GetACLRequest
                {
                    BucketName = newBucketName,
                });

                return getACLResponse.HttpStatusCode == System.Net.HttpStatusCode.OK;
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"S3 error: {ex.Message}");
            }

            return false;
        }
    }
}
