// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Detaches the AmazonS3FullAccess policy from an AWS Identity and Access
/// Management (IAM) role. The example was created using the AWS SDK for .NET
/// version 3.7 and .NET Core 5.0.
/// </summary>
namespace DetachRolePolicyExample
{
    // snippet-start:[IAM.dotnetv3.DetachRolePolicyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class DetachRolePolicy
    {
        /// <summary>
        /// Initializes the IAM client object and then uses it to detach the
        /// AmazonS32FullAccess IAM Policy from the S3FullAccessRole.
        /// </summary>
        public static async Task Main()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var response = await client.DetachRolePolicyAsync(new DetachRolePolicyRequest
            {
                PolicyArn = "arn:aws:iam::aws:policy/AmazonS3FullAccess",
                RoleName = "S3FullAccessRole",
            });

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("The AmazonS3FullAccess policy was successfully detached.");
            }
            else
            {
                Console.WriteLine("Policy could not be detached from the role.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.DetachRolePolicyExample]
}
