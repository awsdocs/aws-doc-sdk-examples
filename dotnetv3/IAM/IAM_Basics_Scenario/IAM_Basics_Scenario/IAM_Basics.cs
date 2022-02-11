// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows some of the basic Amazon Identity and Access Management
/// (IAM) procedures for working with IAM users, roles, and policies.
/// </summary>
namespace IAM_Basics_Scenario
{
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class IAM_Basics
    {
        static readonly string RolePermissions = @"{
            'Version': '2012-10-17',
            'Statement': [
                {
                    'Effect': 'Allow',
                    'Action': 's3:ListAllMyBuckets',
                    'Resource': 'arn:aws:s3:::*'
                }
            ]
        }";

        public static async Task Main()
        {
            // First create the IAM client object.
            var client = new AmazonIdentityManagementServiceClient();

            string userName = "example-user";
            var user = await CreateUserAsync(client, userName);

            // Role names are not case sensitive and must be unique
            // to the account for which it is created.
            string roleName = "temporary-role";
            var role = await CreateRoleAsync(client, roleName, RolePermissions);
        }

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

        public static async Task<Policy> CreatePolicyAsync(
            AmazonIdentityManagementServiceClient client,
            string policyName)
        {
            var request = new CreatePolicyRequest
            {
                PolicyName = policyName,
                PolicyDocument = 
            }
        }

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
    }
}
