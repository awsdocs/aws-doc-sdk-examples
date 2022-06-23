// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Delete an existing AWS Identity and Access Management (IAM) User. This
/// example was created using the AWS SDK for .NET and .NET Core 5.0.
/// </summary>
namespace DeleteUserExample
{
    // snippet-start:[IAM.dotnetv3.DeleteUserExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class DeleteUser
    {
        /// <summary>
        /// Initializes the IAM client object and then calls DeleteUserAsync
        /// to delete and existing IAM user.
        /// </summary>
        public static async Task Main()
        {
            string userName = "IAMUser";

            var client = new AmazonIdentityManagementServiceClient();

            var deleteUserRequest = new DeleteUserRequest()
            {
                UserName = userName,
            };

            var response = await client.DeleteUserAsync(deleteUserRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully deleted the user {userName}.");
            }
            else
            {
                Console.WriteLine($"Could not delete the user {userName}.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.DeleteUserExample]
}
