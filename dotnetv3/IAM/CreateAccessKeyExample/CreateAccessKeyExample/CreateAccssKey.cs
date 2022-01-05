// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateAccessKeyExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    /// <summary>
    /// Shows how to create an AWS Identity and Access Management (IAM) Access
    /// Key for an IAM User. The example was created using the AWS SDK for .NET
    /// version 3.7 and .NET Core 5.0.
    /// </summary>
    public class CreateAccssKey
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
}
