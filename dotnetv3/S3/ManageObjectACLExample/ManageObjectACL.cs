// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example shows how to work with the access control list (ACL) of an
/// object in an Amazon Simple Storage Service (Amazon S3) bucket. The
/// example was created with the AWS SDK for .NET version 3.7 and .NET
/// Core 5.0.
/// </summary>
namespace ManageObjectACLExample
{
    // snippet-start:[S3.dotnetv3.ManageObjectACLExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class ManageObjectACL
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            string keyName = "example-bucket.txt";
            string emailAddress = "someone@example.com";

            // If the AWS Region of the default user is different from the AWS
            // Region where the Amazon S3 bucket is located, pass the AWS Region
            // to the Amazon S3 client constructor. Like this:
            // RegionEndpoint bucketRegion = RegionEndpoint.USWest2;
            IAmazonS3 client = new AmazonS3Client();
            await TestObjectACLTestAsync(client, bucketName, keyName, emailAddress);
        }

        /// <summary>
        /// This method first retrieves and then clears the ACL for an object.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object which will be
        /// used to get and change the ACL for the Amazon S3 object.</param>
        /// <param name="bucketName">A string representing the name of the Amazon S3
        /// bucket where the object whose ACL will be modified is stored.</param>
        /// <param name="keyName">The key name of the Amazon S3 object whose ACL will
        /// be modified.</param>
        /// <param name="emailAddress">The email address to use in defining the
        /// grant for the new ACL.</param>
        public static async Task TestObjectACLTestAsync(IAmazonS3 client, string bucketName, string keyName, string emailAddress)
        {
            try
            {
                // Retrieve the ACL for the object.
                GetACLResponse aclResponse = await client.GetACLAsync(new GetACLRequest
                {
                    BucketName = bucketName,
                    Key = keyName,
                });

                S3AccessControlList acl = aclResponse.AccessControlList;

                // Retrieve the owner (we use this to re-add permissions after we clear the ACL).
                Owner owner = acl.Owner;

                // Clear existing grants.
                acl.Grants.Clear();

                // Add a grant to reset the owner's full permission (the previous clear statement removed all permissions).
                S3Grant fullControlGrant = new()
                {
                    Grantee = new S3Grantee { CanonicalUser = owner.Id },
                    Permission = S3Permission.FULL_CONTROL,
                };

                // Describe the grant for the permission using an email address.
                S3Grant grantUsingEmail = new()
                {
                    Grantee = new S3Grantee { EmailAddress = emailAddress },
                    Permission = S3Permission.WRITE_ACP,
                };
                acl.Grants.AddRange(new List<S3Grant> { fullControlGrant, grantUsingEmail });

                // Set a new ACL.
                PutACLResponse response = await client.PutACLAsync(new PutACLRequest
                {
                    BucketName = bucketName,
                    Key = keyName,
                    AccessControlList = acl,
                });
            }
            catch (AmazonS3Exception amazonS3Exception)
            {
                Console.WriteLine($"Error: {amazonS3Exception.Message}");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.ManageObjectACLExample]
}
