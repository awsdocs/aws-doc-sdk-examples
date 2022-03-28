// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to create an AWS Identity and Access Management (IAM) access
/// key for an IAM user. The example was created using the AWS SDK for .NET
/// version 3.7 and .NET Core 5.0.
/// </summary>
namespace CreateAccessKeyExample
{
    // snippet-start:[IAM.dotnetv3.CreateAccessKeyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class CreateAccessKey
    {
        /// <summary>
        /// Initialize the IAM Client object and then create the IAM Access Key
        /// for the user.
        /// </summary>
        public static async Task Main()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var userName = "DocExampleUser";

            var response = await client.CreateAccessKeyAsync(new CreateAccessKeyRequest
            {
                UserName = userName,
            });

            AccessKey accessKey = response.AccessKey;

            Console.WriteLine($"{accessKey.AccessKeyId} created for {accessKey.UserName}");
        }
    }

    // snippet-end:[IAM.dotnetv3.CreateAccessKeyExample]
}
