// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to attach the AWS managed AmazonS3FullAccess policy
/// to an AWS Identity and Access Management (IAM) role. The example
/// was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace AttachRolePolicyExample
{
    // snippet-start:[IAM.dotnetv3.AttachRolePolicyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class AttachRolePolicy
    {
        /// <summary>
        /// Initializes the IAM client and attaches the IAM Policy to the
        /// selected IAM Role.
        /// </summary>
        public static async Task Main()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var response = await client.AttachRolePolicyAsync(new AttachRolePolicyRequest
            {
                PolicyArn = "arn:aws:iam::aws:policy/AmazonS3FullAccess",
                RoleName = "S3FullAccessRole",
            });

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("AmazonS3FullAccess policy attached to S3FullAccessRole.");
            }
            else
            {
                Console.WriteLine("Coudln't attach policy.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.AttachRolePolicyExample]
}
