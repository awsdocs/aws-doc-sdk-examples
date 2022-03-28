// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows some of the basic AWS Identity and Access Management
/// (IAM) procedures for working with IAM users, roles, and policies.
/// </summary>
namespace IAM_Basics_Scenario
{
    // snippet-start:[IAM.dotnetv3.IAM_BasicsScenario]
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;
    using Amazon.S3;
    using Amazon.SecurityToken;
    using Amazon.SecurityToken.Model;

    public class IAM_Basics
    {
        // Values needed for user, role, and policies.
        private const string UserName = "example-user";
        private const string S3PolicyName = "s3-list-buckets-policy";
        private const string RoleName = "temporary-role";
        private const string AssumePolicyName = "sts-trust-user";

        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;

        public static async Task Main()
        {
            DisplayInstructions();

            // Create the IAM client object.
            var client = new AmazonIdentityManagementServiceClient(Region);

            // First create a user. By default, the new user has
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
            var s3Client1 = new AmazonS3Client(accessKeyId, secretAccessKey);
            await ListMyBucketsAsync(s3Client1);

            // Define a role policy document that allows the new user
            // to assume the role.
            // string assumeRolePolicyDocument = File.ReadAllText("assumePolicy.json");
            string assumeRolePolicyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                "\"Statement\": [{" +
                "\"Effect\": \"Allow\"," +
                "\"Principal\": {" +
                $"	\"AWS\": \"{userArn}\"" +
                "}," +
                    "\"Action\": \"sts:AssumeRole\"" +
                "}]" +
            "}";

            // Permissions to list all buckets.
            string policyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                "	\"Statement\" : [{" +
                    "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                    "	\"Effect\" : \"Allow\"," +
                    "	\"Resource\" : \"*\"" +
                "}]" +
            "}";

            // Create the role to allow listing the S3 buckets. Role names are
            // not case sensitive and must be unique to the account for which it
            // is created.
            var role = await CreateRoleAsync(client, RoleName, assumeRolePolicyDocument);
            var roleArn = role.Arn;

            // Create a policy with permissions to list S3 buckets
            var policy = await CreatePolicyAsync(client, S3PolicyName, policyDocument);

            // Wait 15 seconds for the policy to be created.
            WaitABit(15, "Waiting for the policy to be available.");

            // Attach the policy to the role you created earlier.
            await AttachRoleAsync(client, policy.Arn, RoleName);

            // Wait 15 seconds for the role to be updated.
            Console.WriteLine();
            WaitABit(15, "Waiting to time for the policy to be attached.");

            // Use the AWS Security Token Service (AWS STS) to have the user
            // assume the role we created.
            var stsClient = new AmazonSecurityTokenServiceClient(accessKeyId, secretAccessKey);

            // Wait for the new credentials to become valid.
            WaitABit(10, "Waiting for the credentials to be valid.");

            var assumedRoleCredentials = await AssumeS3RoleAsync(stsClient, "temporary-session", roleArn);

            // Try again to list the buckets using the client created with
            // the new user's credentials. This time, it should work.
            var s3Client2 = new AmazonS3Client(assumedRoleCredentials);

            await ListMyBucketsAsync(s3Client2);

            // Now clean up all the resources used in the example.
            await DeleteResourcesAsync(client, accessKeyId, UserName, policy.Arn, RoleName);

            Console.WriteLine("IAM Demo completed.");
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

            if (response.AccessKey is not null)
            {
                Console.WriteLine($"Successfully created Access Key for {userName}.");
            }

            return response.AccessKey;
        }

        // snippet-end:[IAM.dotnetv3.CreateAccessKey]

        // snippet-start:[IAM.dotnetv3.CreatePolicyAsync]

        /// <summary>
        /// Create a policy to allow a user to list the buckets in an account.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="policyName">The name of the poicy to create.</param>
        /// <param name="policyDocument">The permissions policy document.</param>
        /// <returns>The newly created ManagedPolicy object.</returns>
        public static async Task<ManagedPolicy> CreatePolicyAsync(
            AmazonIdentityManagementServiceClient client,
            string policyName,
            string policyDocument)
        {
            var request = new CreatePolicyRequest
            {
                PolicyName = policyName,
                PolicyDocument = policyDocument,
            };

            var response = await client.CreatePolicyAsync(request);

            return response.Policy;
        }

        // snippet-end:[IAM.dotnetv3.CreatePolicyAsync]

        // snippet-start:[IAM.dotnetv3.AttachPolicy]

        /// <summary>
        /// Attach the policy to the role so that the user can assume it.
        /// </summary>
        /// <param name="client">The initialized IAM client object.</param>
        /// <param name="policyArn">The ARN of the policy to attach.</param>
        /// <param name="roleName">The name of the role to attach the policy to.</param>
        public static async Task AttachRoleAsync(
            AmazonIdentityManagementServiceClient client,
            string policyArn,
            string roleName)
        {
            var request = new AttachRolePolicyRequest
            {
                PolicyArn = policyArn,
                RoleName = roleName,
            };

            var response = await client.AttachRolePolicyAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Successfully attached the policy to the role.");
            }
            else
            {
                Console.WriteLine("Could not attach the policy.");
            }
        }

        // snippet-end:[IAM.dotnetv3.AttachPolicy]

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
                AssumeRolePolicyDocument = rolePermissions,
            };

            var response = await client.CreateRoleAsync(request);

            return response.Role;
        }

        // snippet-end:[IAM.dotnetv3.CreateRoleAsync]

        // snippet-start:[S3.dotnetv3.ListBucketsAsync]

        /// <summary>
        /// List the Amazon S3 buckets owned by the user.
        /// </summary>
        /// <param name="accessKeyId">The access key Id for the user.</param>
        /// <param name="secretAccessKey">The Secret access key for the user.</param>
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
                // Something else went wrong. Display the error message.
                Console.WriteLine($"Error: {ex.Message}");
            }

            Console.WriteLine("Press <Enter> to continue.");
            Console.ReadLine();
        }

        // snippet-end:[S3.dotnetv3.ListBucketsAsync]

        // snippet-start:[STS.dotnetv3.AssumeRoleAsync]

        /// <summary>
        /// Have the user assume the role that allows the role to be used to
        /// list all S3 buckets.
        /// </summary>
        /// <param name="client">An initialized AWS STS client object.</param>
        /// <param name="roleSession">The name of the session where the role
        /// assumption will be active.</param>
        /// <param name="roleToAssume">The Amazon Resource Name (ARN) of the
        /// role to assume.</param>
        /// <returns>The AssumedRoleUser object needed to perform the list
        /// buckets procedure.</returns>
        public static async Task<Credentials> AssumeS3RoleAsync(
            AmazonSecurityTokenServiceClient client,
            string roleSession,
            string roleToAssume)
        {
            // Create the request to use with the AssumeRoleAsync call.
            var request = new AssumeRoleRequest()
            {
                RoleSessionName = roleSession,
                RoleArn = roleToAssume,
            };

            var response = await client.AssumeRoleAsync(request);

            return response.Credentials;
        }

        // snippet-end:[STS.dotnetv3.AssumeRoleAsync]

        // snippet-start:[IAM.dotnetv3.DeleteResourcesAsync]

        /// <summary>
        /// Delete the user, and other resources created for this example.
        /// </summary>
        /// <param name="client">The initialized client object.</param>
        /// <param name=accessKeyId">The Id of the user's access key.</param>"
        /// <param name="userName">The user name of the user to delete.</param>
        /// <param name="policyName">The name of the policy to delete.</param>
        /// <param name="policyArn">The Amazon Resource Name ARN of the Policy to delete.</param>
        /// <param name="roleName">The name of the role that will be deleted.</param>
        public static async Task DeleteResourcesAsync(
            AmazonIdentityManagementServiceClient client,
            string accessKeyId,
            string userName,
            string policyArn,
            string roleName)
        {
            var detachPolicyResponse = await client.DetachRolePolicyAsync(new DetachRolePolicyRequest
            {
                PolicyArn = policyArn,
                RoleName = roleName,
            });

            var delPolicyResponse = await client.DeletePolicyAsync(new DeletePolicyRequest
            {
                PolicyArn = policyArn,
            });

            var delRoleResponse = await client.DeleteRoleAsync(new DeleteRoleRequest
            {
                RoleName = roleName,
            });

            var delAccessKey = await client.DeleteAccessKeyAsync(new DeleteAccessKeyRequest
            {
                AccessKeyId = accessKeyId,
                UserName = userName,
            });

            var delUserResponse = await client.DeleteUserAsync(new DeleteUserRequest
            {
                UserName = userName,
            });

        }

        // snippet-end:[IAM.dotnetv3.DeleteResourcesAsync]

        /// <summary>
        /// Display a countdown and wait for a number of seconds.
        /// </summary>
        /// <param name="numSeconds">The number of seconds to wait.</param>
        public static void WaitABit(int numSeconds, string msg)
        {
            Console.WriteLine(msg);

            // Wait for the requested number of seconds.
            for (int i = numSeconds; i > 0; i--)
            {
                System.Threading.Thread.Sleep(1000);
                Console.Write($"{i}...");
            }

            Console.WriteLine("\n\nPress <Enter> to continue.");
            Console.ReadLine();
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
