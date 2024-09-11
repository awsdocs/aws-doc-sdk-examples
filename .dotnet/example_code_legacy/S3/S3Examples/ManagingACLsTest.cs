// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[s3.dotNET.ManageACLsTest.cs]
using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Amazon.DocSamples.S3
{
    class ManageACLsTest
    {
        private const string bucketName    = "*** existing bucket name ***";
        private const string newBucketName = "*** new bucket name ***";
        private const string keyName = "*** object key name ***";
        private const string emailAddress = "***  email address ***";
        // Specify your bucket region (an example region is shown).
        private static readonly RegionEndpoint bucketRegion = RegionEndpoint.USWest2;
        private static IAmazonS3 client;

        public static void Main()
        {
            client = new AmazonS3Client(bucketRegion);
            TestBucketObjectACLsAsync().Wait();
        }

        private static async Task TestBucketObjectACLsAsync()
        {
            try
            {
                    // Add a bucket (specify canned ACL).
                    await CreateBucketWithCannedACLAsync();

                    // Get the ACL on a bucket.
                    await GetBucketACLAsync(bucketName);

                    // Add (replace) the ACL on an object in a bucket.
                    await AddACLToExistingObjectAsync(bucketName, keyName);
            }
            catch (AmazonS3Exception amazonS3Exception)
            {
                Console.WriteLine("An AmazonS3Exception was thrown. Exception: " + amazonS3Exception.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.ToString());
            }
        }

        static async Task CreateBucketWithCannedACLAsync()
        {
            var request = new PutBucketRequest()
            {
                BucketName = newBucketName,
                BucketRegion = S3Region.EUW1,
                // Add a canned ACL.
                CannedACL = S3CannedACL.LogDeliveryWrite
            };
            var response = await client.PutBucketAsync(request);
        }

        static async Task GetBucketACLAsync(string bucketName)
        {
            GetACLResponse response = await client.GetACLAsync(new GetACLRequest
            {
                BucketName = bucketName
            });
            S3AccessControlList accessControlList = response.AccessControlList;
        }

        static async Task AddACLToExistingObjectAsync(string bucketName, string keyName)
        {
            // Retrieve the ACL for an object.
            GetACLResponse aclResponse = await client.GetACLAsync(new GetACLRequest
            {
                BucketName = bucketName,
                Key = keyName
            });

            S3AccessControlList acl = aclResponse.AccessControlList;

            // Retrieve the owner.
            Owner owner = acl.Owner;

            // Clear existing grants.
            acl.Grants.Clear();

            // Add a grant to reset the owner's full permission
            // (the previous clear statement removed all permissions).
            S3Grant fullControlGrant = new S3Grant
            {
                Grantee = new S3Grantee { CanonicalUser = acl.Owner.Id }
            };
            acl.AddGrant(fullControlGrant.Grantee, S3Permission.FULL_CONTROL);

            // Specify email to identify grantee for granting permissions.
            S3Grant grantUsingEmail = new S3Grant
            {
                Grantee = new S3Grantee { EmailAddress = emailAddress },
                Permission = S3Permission.WRITE_ACP
            };

            // Specify log delivery group as grantee.
            S3Grant grantLogDeliveryGroup = new S3Grant
            {
                Grantee = new S3Grantee { URI = "http://acs.amazonaws.com/groups/s3/LogDelivery" },
                Permission = S3Permission.WRITE
            };

            // Create a new ACL.
            S3AccessControlList newAcl = new S3AccessControlList
            {
                Grants = new List<S3Grant> { grantUsingEmail, grantLogDeliveryGroup },
                Owner = owner
            };

            // Set the new ACL.
            PutACLResponse response = await client.PutACLAsync(new PutACLRequest
            {
                BucketName = bucketName,
                Key = keyName,
                AccessControlList = newAcl
            });
        }
    }
}
// snippet-end:[s3.dotNET.ManageACLsTest.cs]
