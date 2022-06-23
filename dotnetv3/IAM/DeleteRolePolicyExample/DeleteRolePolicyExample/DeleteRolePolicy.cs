// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to delete a policy that has been attached to an AWS Identity
/// and Access Management (IAM) role. The example was created using the AWS
/// SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace DeleteRolePolicyExample
{
    // snippet-start:[IAM.dotnetv3.DeleteRolePolicyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class DeleteRolePolicy
    {
        /// <summary>
        /// Initializes the IAM client object and then calls DeleteRolePolicyAsync
        /// to delete the Policy attached to the Role.
        /// </summary>
        public static async Task Main()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var response = await client.DeleteRolePolicyAsync(new DeleteRolePolicyRequest
            {
                PolicyName = "ExamplePolicy",
                RoleName = "Test-Role",
            });

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Policy successfully deleted.");
            }
            else
            {
                Console.WriteLine("Could not delete pollicy.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.DeleteRolePolicyExample]
}
