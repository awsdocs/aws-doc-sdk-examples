// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows hw to create a new AWS Identity and Access Management (IAM) User. The
/// example was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace CreateUserExample
{
    // snippet-start:[IAM.dotnetv3.CreateUserExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class CreateUser
    {
        /// <summary>
        /// Initializes the IAM Client object and then uses it to create
        /// a new IAM User.
        /// </summary>
        public static async Task Main()
        {
            var client = new AmazonIdentityManagementServiceClient();
            User readOnlyUser;
            string userName = "IAMUser";
            var userRequest = new CreateUserRequest
            {
                UserName = userName,
            };

            var response = await client.CreateUserAsync(userRequest);
            readOnlyUser = response.User;

            if (readOnlyUser is not null)
            {
                Console.WriteLine($"Successfully created {readOnlyUser.UserName}");
            }
            else
            {
                Console.WriteLine("Could not create user.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.CreateUserExample]
}
