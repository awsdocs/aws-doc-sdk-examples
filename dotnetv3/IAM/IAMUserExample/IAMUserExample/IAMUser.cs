// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows a typical use case for the AWS Identity and Access
/// Management (IAM) service. It was created using the AWS SDK for .NET
/// version 3.x and .NET Core 5.x.
/// </summary>
namespace IAMUserExample
{
    // snippet-start:[IAM.dotnetv3.IAMUserExample]
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class IAMUser
    {
        // Represents json code for AWS full access policy for Amazon Simple
        // Storage Service (Amazon S3).
        private const string S3FullAccessPolicy = "{" +
            "	\"Statement\" : [{" +
                "	\"Action\" : [\"s3:*\"]," +
                "	\"Effect\" : \"Allow\"," +
                "	\"Resource\" : \"*\"" +
            "}]" +
        "}";

        private const string PolicyName = "S3FullAccess";

        private static readonly string UserName = "S3FullAccessUser";
        private static readonly string GroupName = "S3FullAccessGroup";

        private static readonly string BucketName = "temporary-doc-example-bucket";

        public static async Task Main()
        {
            var iamClient = new AmazonIdentityManagementServiceClient();
            var s3Client = new AmazonS3Client();

            // Clear the console screen before displaying any text.
            Console.Clear();

            // Create an IAM group.
            var createGroupResponse = await CreateNewGroupAsync(iamClient, GroupName);

            // Create a policy and add it to the group.
            var success = await AddGroupPermissionsAsync(iamClient, createGroupResponse.Group);

            // Now create a new user.
            User readOnlyUser;
            var userRequest = new CreateUserRequest
            {
                UserName = UserName,
            };

            readOnlyUser = await CreateNewUserAsync(iamClient, userRequest);

            // Create access and secret keys for the user.
            CreateAccessKeyResponse createKeyResponse = await CreateNewAccessKeyAsync(iamClient, UserName);

            // Add the new user to the group.
            success = await AddNewUserToGroupAsync(iamClient, readOnlyUser.UserName, createGroupResponse.Group.GroupName);

            // Show that the user can access Amazon S3 by listing the buckets on
            // the account.
            Console.Write("Waiting for user status to be Active.");
            do
            {
                Console.Write(" .");
            }
            while (createKeyResponse.AccessKey.Status != StatusType.Active);

            await ListBucketsAsync(createKeyResponse.AccessKey);

            // Show that the user also has write access to Amazon S3 by creating
            // a new bucket.
            success = await CreateS3BucketAsync(createKeyResponse.AccessKey, BucketName);

            if (success)
            {
                Console.WriteLine($"Successfully created the bucket: {BucketName}.");
            }

            // Delete the user, the group, and the new bucket.
            await CleanUpResources(
                iamClient,
                s3Client,
                UserName,
                GroupName,
                BucketName,
                createKeyResponse.AccessKey.AccessKeyId);

            Console.WriteLine("Press <Enter> to close the program.");
            Console.ReadLine();
        }

        // snippet-start:[IAM.dotnetv3.CreateGroup]

        /// <summary>
        /// Creates a new IAM group.
        /// </summary>
        /// <param name="client">The IAM Client object.</param>
        /// <param name="groupName">The string representing the name for the
        /// new group.</param>
        /// <returns>Returns the response object returned by CreateGroupAsync.</returns>
        public static async Task<CreateGroupResponse> CreateNewGroupAsync(
            AmazonIdentityManagementServiceClient client,
            string groupName)
        {
            var createGroupRequest = new CreateGroupRequest
            {
                GroupName = groupName,
            };

            Console.WriteLine("--------------------------------------------------------------------------------------------------------------");
            Console.WriteLine("Start by creating the group...");
            var response = await client.CreateGroupAsync(createGroupRequest);
            Console.WriteLine($"Successfully created the group: {response.Group.GroupName}");
            Console.WriteLine("--------------------------------------------------------------------------------------------------------------\n");

            return response;
        }

        // snippet-end:[IAM.dotnetv3.CreateGroup]

        // snippet-start:[IAM.dotnetv3.PutGroupPolicy]

        /// <summary>
        /// This method adds Amazon S3 readonly permissions to the group
        /// created earlier.
        /// </summary>
        /// <param name="client">The IAM client object.</param>
        /// <param name="group">The name of the group to create.</param>
        /// <returns>Returns a boolean value that indicates the success of the
        /// PutGroupPolicyAsync call.</returns>
        public static async Task<bool> AddGroupPermissionsAsync(AmazonIdentityManagementServiceClient client, Group group)
        {
            // Add appropriate permissions so the new user can access S3 on
            // a readonly basis.
            var groupPolicyRequest = new PutGroupPolicyRequest
            {
                GroupName = group.GroupName,
                PolicyName = PolicyName,
                PolicyDocument = S3FullAccessPolicy,
            };

            Console.WriteLine("--------------------------------------------------------------------------------------------------------------");
            var response = await client.PutGroupPolicyAsync(groupPolicyRequest);
            Console.WriteLine($"Successfully added S3 full access access policy to {group.GroupName}.");

            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[IAM.dotnetv3.PutGroupPolicy]

        // snippet-start:[IAM.dotnetv3.CreateUser]

        /// <summary>
        /// This method creates a new IAM user.
        /// </summary>
        /// <param name="client">The IAM client object.</param>
        /// <param name="request">The user creation request.</param>
        /// <returns>The object returned by the call to CreateUserAsync.</returns>
        public static async Task<User> CreateNewUserAsync(AmazonIdentityManagementServiceClient client, CreateUserRequest request)
        {
            CreateUserResponse response = null;
            try
            {
                response = await client.CreateUserAsync(request);

                // Show the information about the user from the response.
                Console.WriteLine("\n--------------------------------------------------------------------------------------------------------------");
                Console.WriteLine($"New user: {response.User.UserName} ARN = {response.User.Arn}.");
                Console.WriteLine($"{response.User.UserName} has {response.User.PermissionsBoundary}.");
            }
            catch (EntityAlreadyExistsException ex)
            {
                Console.WriteLine($"{ex.Message}");
            }

            if (response is not null)
            {
                return response.User;
            }
            else
            {
                return null;
            }
        }

        // snippet-end:[IAM.dotnetv3.CreateUser]

        // snippet-start:[IAM.dotnetv3.AddUserToGroup]

        /// <summary>
        /// Adds the user represented by the userName parameter to the group
        /// represented by the groupName parameter.
        /// </summary>
        /// <param name="client">The client which will be used to make the
        /// method call to add a user to an IAM group.</param>
        /// <param name="userName">A string representing the name of the IAM
        /// group to which the new user will be added.</param>
        /// <param name="groupName">A string representing the name of the group
        /// to which to add the user.</param>
        /// <returns>A boolean value that indicates the success or failure of
        /// the clean up procedure.</returns>
        public static async Task<bool> AddNewUserToGroupAsync(AmazonIdentityManagementServiceClient client, string userName, string groupName)
        {
            var response = await client.AddUserToGroupAsync(new AddUserToGroupRequest
            {
                GroupName = groupName,
                UserName = userName,
            });

            Console.WriteLine("\n--------------------------------------------------------------------------------------------------------------");
            Console.WriteLine($"The user, {userName} has been added to {groupName}.");
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[IAM.dotnetv3.AddUserToGroup]

        // snippet-start:[IAM.dotnetv3.CreateNewAccessKey]

        /// <summary>
        /// Creates a new access key for the user represented by the userName
        /// parameter.
        /// </summary>
        /// <param name="client">The client object which will call
        /// CreateAccessKeyAsync.</param>
        /// <param name="userName">The name of the user for whom an access key
        /// is created by the call to CreateAccessKeyAsync.</param>
        /// <returns>Returns the response from the call to
        /// CreateAccessKeyAsync.</returns>
        public static async Task<CreateAccessKeyResponse> CreateNewAccessKeyAsync(
            AmazonIdentityManagementServiceClient client,
            string userName)
        {
            try
            {
                // Create an access key for the IAM user that can be used by the SDK.
                var response = await client.CreateAccessKeyAsync(new CreateAccessKeyRequest
                {
                    // Use the user you created in the CreateUser example
                    UserName = userName,
                });
                return response;
            }
            catch (LimitExceededException e)
            {
                Console.WriteLine(e.Message);
                return null;
            }
        }

        // snippet-end:[IAM.dotnetv3.CreateNewAccessKey]

        /// <summary>
        /// Proves that the user has the proper permissions to view the
        /// contents of an Amazon S3 bucket.
        /// </summary>
        /// <param name="accessKey">The AccessKey that will provide permissions
        /// for the new user to call ListBucketsAsync.</param>
        public static async Task ListBucketsAsync(AccessKey accessKey)
        {
            Console.WriteLine("\nPress <Enter> to list the S3 buckets using the new user.\n");
            Console.ReadLine();

            // Creating a client that works with this user.
            var client = new AmazonS3Client(accessKey.AccessKeyId, accessKey.SecretAccessKey);

            // Get the list of buckets accessible by the new user.
            var response = await client.ListBucketsAsync();

            // Loop through the list and print each bucket's name
            // and creation date.
            Console.WriteLine("\n--------------------------------------------------------------------------------------------------------------");
            Console.WriteLine("Listing S3 buckets:\n");
            response.Buckets
                .ForEach(b => Console.WriteLine($"Bucket name: {b.BucketName}, created on: {b.CreationDate}"));
        }

        /// <summary>
        /// Create a new Amazon S3 bucket using the supplied Access Key to
        /// create an Amazon S3 client using the new user.
        /// </summary>
        /// <param name="accessKey">The AccessKey that will provide permissions
        /// for the new user to call ListBucketsAsync.</param>
        /// <param name="bucketName">The name of the Amazon S3 bucket to create.</param>
        /// <returns>A boolean value indicating the success or failure of the operation.</returns>
        public static async Task<bool> CreateS3BucketAsync(
            AccessKey accessKey,
            string bucketName)
        {
            // Creating a client that works with this user.
            var client = new AmazonS3Client(accessKey.AccessKeyId, accessKey.SecretAccessKey);
            var success = false;

            try
            {
                var request = new PutBucketRequest
                {
                    BucketName = bucketName,
                    UseClientRegion = true,
                };

                var response = await client.PutBucketAsync(request);
                success = true;
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error creating bucket: '{ex.Message}'");
            }

            return success;
        }

        /// <summary>
        /// Deletes the User, Group, and AccessKey which were created for the purposes of
        /// this example.
        /// </summary>
        /// <param name="client">The IAM client used to delete the other
        /// resources.</param>
        /// <param name="s3Client">The Amazon S3 client object to delete the
        /// bucket that was created.</param>
        /// <param name="userName">The name of the user that will be deleted.</param>
        /// <param name="groupName">The name of the group that will be deleted.</param>
        /// <param name="bucketName">The name of the bucket to delete.</param>
        /// <param name="accessKeyId">The AccessKeyId that represents the
        /// AccessKey that was created for use with the ListBucketsAsync
        /// method.</param>
        public static async Task CleanUpResources(
            AmazonIdentityManagementServiceClient client,
            AmazonS3Client s3Client,
            string userName,
            string groupName,
            string bucketName,
            string accessKeyId)
        {
            // Remove the user from the group.
            var removeUserRequest = new RemoveUserFromGroupRequest()
            {
                UserName = userName,
                GroupName = groupName,
            };

            await client.RemoveUserFromGroupAsync(removeUserRequest);

            // Delete the client access keys before deleting the user.
            var deleteAccessKeyRequest = new DeleteAccessKeyRequest()
            {
                AccessKeyId = accessKeyId,
                UserName = userName,
            };

            await client.DeleteAccessKeyAsync(deleteAccessKeyRequest);

            // Now we can safely delete the user.
            var deleteUserRequest = new DeleteUserRequest()
            {
                UserName = userName,
            };

            await client.DeleteUserAsync(deleteUserRequest);

            // We have to delete the policy attached to the group first.
            var deleteGroupPolicyRequest = new DeleteGroupPolicyRequest()
            {
                GroupName = groupName,
                PolicyName = PolicyName,
            };

            await client.DeleteGroupPolicyAsync(deleteGroupPolicyRequest);

            // Now delete the group.
            var deleteGroupRequest = new DeleteGroupRequest()
            {
                GroupName = groupName,
            };

            await client.DeleteGroupAsync(deleteGroupRequest);

            // Now delete the bucket.
            var deleteBucketRequest = new DeleteBucketRequest
            {
                BucketName = bucketName,
            };

            await s3Client.DeleteBucketAsync(deleteBucketRequest);

            Console.WriteLine("\n--------------------------------------------------------------------------------------------------------------");
            Console.WriteLine("Deleted the user, the group, and the bucket created for this example.");
        }
    }

    // snippet-end:[IAM.dotnetv3.IAMUserExample]
}
