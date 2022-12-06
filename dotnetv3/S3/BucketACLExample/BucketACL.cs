// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example shows how to work with access control lists (ACLs) in an
/// Amazon Simple Storage Service (Amazon S3) bucket. The example was
/// created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace BucketACLExample
{
    // snippet-start:[S3.dotnetv3.BucketACLExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class BucketACL
    {
        public static async Task Main()
        {
            const string newBucketName = "doc-example-bucket";

            IAmazonS3 client = new AmazonS3Client();

            var success = await CreateBucketUseCannedACLAsync(client, S3Region.USEast1, newBucketName);

            if (success)
            {
                Console.WriteLine("Amazon S3 bucket created.");
                var aclList = await GetACLForBucketAsync(client, newBucketName);
                if (aclList.Grants.Count > 0)
                {
                    DisplayACL(aclList);
                }
            }
        }

        // snippet-start:[S3.dotnetv3.PutBucketACLExample]

        /// <summary>
        /// Creates an Amazon S3 bucket with an ACL to control access to the
        /// bucket and the objects stored in it.
        /// </summary>
        /// <param name="client">The initialized client object used to create
        /// an Amazon S3 bucket, with an ACL applied to the bucket.
        /// </param>
        /// <param name="region">The AWS Region where the bucket will be created.</param>
        /// <param name="newBucketName">The name of the bucket to create.</param>
        /// <returns>A boolean value indicating success or failure.</returns>
        public static async Task<bool> CreateBucketUseCannedACLAsync(IAmazonS3 client, S3Region region, string newBucketName)
        {
            try
            {
                // Create a new Amazon S3 bucket with Canned ACL.
                var putBucketRequest = new PutBucketRequest()
                {
                    BucketName = newBucketName,
                    BucketRegion = region,
                    CannedACL = S3CannedACL.LogDeliveryWrite,
                };

                PutBucketResponse putBucketResponse = await client.PutBucketAsync(putBucketRequest);

                return putBucketResponse.HttpStatusCode == System.Net.HttpStatusCode.OK;
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Amazon S3 error: {ex.Message}");
            }

            return false;
        }

        // snippet-end:[S3.dotnetv3.PutBucketACLExample]

        // snippet-start:[S3.dotnetv3.GetBucketACLExample]

        /// <summary>
        /// Get the access control list (ACL) for the new bucket.
        /// </summary>
        /// <param name="client">The initialized client object used to get the
        /// access control list (ACL) of the bucket.</param>
        /// <param name="newBucketName">The name of the newly created bucket.</param>
        /// <returns>An S3AccessControlList.</returns>
        public static async Task<S3AccessControlList> GetACLForBucketAsync(IAmazonS3 client, string newBucketName)
        {
            // Retrieve bucket ACL to show that the ACL was properly applied to
            // the new bucket.
            GetACLResponse getACLResponse = await client.GetACLAsync(new GetACLRequest
            {
                BucketName = newBucketName,
            });

            return getACLResponse.AccessControlList;
        }

        // snippet-end:[S3.dotnetv3.GetBucketACLExample]

        /// <summary>
        /// Display the contents of the ACL applied to the newly created bucket.
        /// </summary>
        /// <param name="acl">An S3AccessControlList for the newly created
        /// Amazon S3 bucket.</param>
        public static void DisplayACL(S3AccessControlList acl)
        {
            Console.WriteLine($"\nOwner: {acl.Owner}");

            acl.Grants.ForEach(g =>
            {
                Console.WriteLine($"{g.Grantee}, {g.Permission}");
            });
        }
    }

    // snippet-end:[S3.dotnetv3.BucketACLExample]
}
