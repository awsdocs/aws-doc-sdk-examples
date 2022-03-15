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
    using Amazon.SecurityToken;
    using Amazon.SecurityToken.Model;

    public class IAM_Basics
    {
        // Values needed for user, role, and policies.
        private const string UserName = "example-user";
        private const string S3PolicyName = "s3-list-buckets-policy";
        private const string RoleName = "temporary-role";
        private const string AssumePolicyName = "sts-trust-user";

        private const string RolePermissions = @"{
                'Version': '2012-10-17',
                'Statement': [
                    {
                        'Effect': 'Allow',
                        'Action': 's3:ListAllMyBuckets',
                        'Resource': 'arn:aws:s3:::*'
                    }
                ]
            }";

        private const string ManagedPolicy = @"{
          'Version': '2012-10-17',
          'Statement': [
            {
              'Effect': 'Allow',
              'Action': ['s3:ListAllMyBuckets', 'sts:AssumeRole'],
              'Resource': '*',
            },
          ],
        }";

        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;

        public static async Task Main()
        {
            DisplayInstructions();

            // Create the IAM client object.
            var client = new AmazonIdentityManagementServiceClient(Region);

            // First create a user. When created, the new user has
            // no permissions.
            Console.WriteLine($"Creating a new user with user name: {UserName}.");
            var user = await CreateUserAsync(client, UserName);
            var userArn = user.Arn;
            Console.WriteLine($"Successfully created user: {UserName} with ARN: {userArn}.");

            // Create an AccessKey for the user.
            var accessKey = await CreateAccessKeyAsync(client, UserName);
            var accessKeyId = accessKey.AccessKeyId;
            var secretAccessKey = accessKey.SecretAccessKey;

            // Try listing the Amazon Simple Storage Service (Amazon S3)
            // buckets. This should fail at this point because the user doesn't
            // have permissions to perform this task.
            var s3Client = new AmazonS3Client(accessKeyId, secretAccessKey);

            // Try to list the buckets using the client created with
            // the new user's credentials.
            await ListMyBucketsAsync(s3Client);

            // Trust the user to assume the role.
            string accessPermissions = @"{
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
            var roleArn = role.Arn;

            // Use the Security Token Service (AWS STS) to have the user assume
            // the role we created.
            var stsClient = new AmazonSecurityTokenServiceClient();
            var assumedRoleUser = await AssumeS3RoleAsync(stsClient, UserName, 1600, "temporary-session", roleArn);

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

        /// <summary>
        /// Create a new AccessKey for the user.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="userName">The name of the user for whom to create the key.</param>
        /// <returns>A new IAM access key for the user.</returns>
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
                Console.WriteLine(new string('-', 80));
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

        // snippet-start:[STS.dotnetv3.AssumeRole]

        /// <summary>
        /// Have the user assume the role that allows the role to be used to
        /// list all S3 buckets.
        /// </summary>
        /// <param name="client">An initialized AWS STS client object.</param>
        /// <param name="userName">The name of the user to assume the role.</param>
        /// <param name="sessionDuration">The duration, in seconds, of the
        /// session when the role will be assumed.</param>
        /// <param name="roleSession">The name of the session where the role
        /// assumption will be active.</param>
        /// <param name="roleToAssume">The Amazon Resource Name (ARN) of the
        /// role to assume.</param>
        /// <returns>The AssumedRoleUser object needed to perform the list
        /// buckets procedure.</returns>
        public static async Task<AssumedRoleUser> AssumeS3RoleAsync(
            AmazonSecurityTokenServiceClient client,
            string userName,
            int sessionDuration,
            string roleSession
            string roleToAssume)
        {
            // Create the request to use with the AssumeRoleAsync call.
            var request = new AssumeRoleRequest()
            {
                DurationSeconds = 1600,
                RoleSessionName = sessionDuration.ToString(),
                RoleArn = roleToAssume,
            };

            var response = await client.AssumeRoleAsync(request);

            return response.AssumedRoleUser;
        }

        // snippet-end:[STS.dotnetv3.AssumeRole]

        // snippet-end:[S3.dotnetv3.ListBucketsAsync]

        /// <summary>
        /// Delete the user, and other resources created for this example.
        /// </summary>
        /// <param name="client">The initialized client object.</param>
        /// <param name="userName">The user name of the user to delete.</param>
        /// <returns>A Boolean value indicating the success or failure of the
        /// delete operations.</returns>
        public static async Task DeleteResources(
            AmazonIdentityManagementServiceClient client,
            string userName)
        {
            bool success = false;

            var request = new DeleteUserRequest
            {
                UserName = userName,
            };

            var response = await client.DeleteUserAsync(request);
            success = response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        /// <summary>
        /// Shows the a description of the features of the program.
        /// </summary>
        public static void DisplayInstructions()
        {
            var separator = new string('-', 80);

            Console.WriteLine(separator);
            Console.WriteLine("IAM Basics");
            Console.WriteLine("This application uses the basic features of the AWS Identity and Access");
            Console.WriteLine("Management (IAM) creating, managing, and controlling access to resources for");
            Console.WriteLine("users. The application was created using the AWS SDK for .NET version 3.7 and");
            Console.WriteLine(".NET Core 5. The application performs the following actions:");
            Console.WriteLine();
            Console.WriteLine("1. Creates a user with no permissions");
            Console.WriteLine("2. Creates a rolw and policy that grants s3:ListAllMyBuckets permission");
            Console.WriteLine("3. Grants the user permission to assume the role");
            Console.WriteLine("4. Creates an Amazon Simple Storage Service (Amazon S3) client and tries");
            Console.WriteLine("   to list buckets. (This should fail.)");
            Console.WriteLine("5. Gets temporary credentials by assuming the role.");
            Console.WriteLine("6. Creates an Amazon S3 client object with the temporary credentials and");
            Console.WriteLine("   lists the buckets. (This time it should work.)");
            Console.WriteLine("7. Deletes all of the resources created.");
            Console.WriteLine(separator);
            Console.WriteLine("Press <Enter> to continue.");
            Console.ReadLine();
        }
    }

    // snippet-end:[IAM.dotnetv3.IAM_BasicsScenario]
}
