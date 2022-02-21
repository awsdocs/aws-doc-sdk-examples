// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows some of the basic Amazon Identity and Access Management
/// (IAM) procedures for working with IAM users, roles, and policies.
/// </summary>
namespace IAM_Basics_Scenario
{
    // snippet-start:[IAM.dotnetv3.IAM_BasicsScenario]
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class IAM_Basics
    {
        public static RegionEndpoint Region = RegionEndpoint.USEast2;

        // Values needed for user, role, and policies.
        const string UserName = "example-user";
        const string S3PolicyName = "s3-list-buckets-policy";
        const string RoleName = "temporary-role";
        const string AssumePolicyName = "sts-trust-user";

        const string RolePermissions = @"{
                'Version': '2012-10-17',
                'Statement': [
                    {
                        'Effect': 'Allow',
                        'Action': 's3:ListAllMyBuckets',
                        'Resource': 'arn:aws:s3:::*'
                    }
                ]
            }";

        const string ManagedPolicy = @"{
          'Version': '2012-10-17',
          'Statement': [
            {
              'Effect': 'Allow',
              'Action': ['s3:ListAllMyBuckets', 'sts:AssumeRole'],
              'Resource': '*',
            },
          ],
        }";

public static async Task Main()
        {
            // First create the IAM client object.
            var client = new AmazonIdentityManagementServiceClient(Region);

            // First create a user. When created, the new user has
            // no permissions.
            var user = await CreateUserAsync(client, UserName);
            var userArn = user.Arn;

            // Create an AccessKey for the user.
            var accessKey = await CreateAccessKeyAsync(client, userArn);
            var accessKeyId = accessKey.AccessKeyId;
            var secretAccessKey = accessKey.SecretAccessKey;

            // Try listing the Amazon Simple Storage Service (Amazon S3)
            // buckets. This should fail at this point because the user doesn't
            // have permissions to perform this task.
            var s3Client = new AmazonS3Client(accessKeyId, secretAccessKey);


            string AccessPermissions = @"{
                'Version': '2012-10-17',
                'Statement': [
                    {
                        'Effect': 'Allow',
                        'Principal': {
                            AWS:" + userArn + @",
                        },
                        'Action': 'sts:AssumeRole',
                    },
                ],
            }";

            // Create the role to allow listing the Amazon Simple Storage Service
            // (Amazon S3) buckets. Role names are not case sensitive and must
            // be unique to the account for which it is created.
            var role = await CreateRoleAsync(client, RoleName, RolePermissions);
            var policyArn = role.Arn;


        }

        // snippet-start:[IAM.dotnetv3.CreateUserAsync]

        /// <summary>
        /// Create a new IAM user.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="userName">A string representing the user name of the
        /// new user.</param>
        /// <returns>The newly created user.</returns>
        public static async Task<User> CreateUserAsync(
            AmazonIdentityManagementServiceClient client,
            string userName)
        {
            var request = new CreateUserRequest
            {
                UserName = userName,
            };

            var response = await client.CreateUserAsync(request);

            return response.User;
        }

        // snippet-end:[IAM.dotnetv3.CreateUserAsync]

        // snippet-start:[IAM.dotnetv3.CreateAccessKey]

        public static async Task<AccessKey> CreateAccessKeyAsync(
            AmazonIdentityManagementServiceClient client,
            string userName)
        {
            var request = new CreateAccessKeyRequest
            {
                UserName = userName,
            };

            var response = await client.CreateAccessKeyAsync(request);

            return response.AccessKey;
        }

        // snippet-end:[IAM.dotnetv3.CreateAccessKey]

        // snippet-start:[IAM.dotnetv3.CreatePolicyAsync]

        /// <summary>
        /// Create a policy to allow a user to list the b uckets in an account.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="policyName">The name of the poicy to create.</param>
        /// <param name="rolePermissions">The permissions policy document.</param>
        /// <returns>The newly created ManagedPolicy object.</returns>
        public static async Task<ManagedPolicy> CreatePolicyAsync(
            AmazonIdentityManagementServiceClient client,
            string policyName,
            string rolePermissions)
        {
            var request = new CreatePolicyRequest
            {
                PolicyName = policyName,
                PolicyDocument = rolePermissions,
            };

            var response = await client.CreatePolicyAsync(request);

            return response.Policy;
        }

        // snippet-end:[IAM.dotnetv3.CreatePolicyAsync]

        // snippet-start:[IAM.dotnetv3.CreateRoleAsync]

        /// <summary>
        /// Create a new IAM role which we can attach to a user.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="roleName">The name of the IAM role to create.</param>
        /// <param name="rolePermissions">The permissions which the role will have.</param>
        /// <returns>A Role object representing the newly created role.</returns>
        public static async Task<Role> CreateRoleAsync(
            AmazonIdentityManagementServiceClient client,
            string roleName,
            string rolePermissions)
        {
            var request = new CreateRoleRequest
            {
                RoleName = roleName,
            };

            var response = await client.CreateRoleAsync(request);

            return response.Role;
        }

        // snippet-end:[IAM.dotnetv3.CreateRoleAsync]

        // snippet-start:[S3.dotnetv3.ListBucketsAsync]

        /// <summary>
        /// List the Amazon S3 buckets owned by the user.
        /// </summary>
        /// <param name="client">Initialized Amazon S3 client.</param>
        public static async Task ListMyBucketsAsync(AmazonS3Client client)
        {
            Console.WriteLine("\nPress <Enter> to list the S3 buckets using the new user.\n");
            Console.ReadLine();

            try
            {
                // Get the list of buckets accessible by the new user.
                var response = await client.ListBucketsAsync();

                // Loop through the list and print each bucket's name
                // and creation date.
                Console.WriteLine("\n--------------------------------------------------------------------------------------------------------------");
                Console.WriteLine("Listing S3 buckets:\n");
                response.Buckets
                    .ForEach(b => Console.WriteLine($"Bucket name: {b.BucketName}, created on: {b.CreationDate}"));
            }
            catch (AmazonS3Exception ex)
            {
                // This is the expected error if the role has not been assigned
                // to the user associated with the Amazon S3 client.
                Console.WriteLine($"Error: {ex}");
                Console.WriteLine("The user associated with this client does not have permission to call ListBucketsAsync.");
            }
        }

        // snippet-end:[S3.dotnetv3.ListBucketsAsync]
    }

    // snippet-end:[IAM.dotnetv3.IAM_BasicsScenario]
}
