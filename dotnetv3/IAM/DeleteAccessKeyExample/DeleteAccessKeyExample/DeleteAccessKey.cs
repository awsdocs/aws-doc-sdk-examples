// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Shows how to delete an AWS Identity and Access Management (IAM) access key.
/// The example was created using the AWS SDK for .NET version 3.7 and .NET
/// Core 5.0.
/// </summary>
namespace DeleteAccessKeyExample
{
    // snippet-start:[IAM.dotnetv3.DeleteAccessKeyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    public class DeleteAccessKey
    {
        /// <summary>
        /// Initializes an IAM Client Object and then deletes the IAM AccessKey.
        /// </summary>
        public static async Task Main()
        {
            string accessKeyId = "ACCESS_KEY_ID";
            string userName = "DocExampleUser";
            var client = new AmazonIdentityManagementServiceClient();

            var response = await client.DeleteAccessKeyAsync(new DeleteAccessKeyRequest
            {
                AccessKeyId = accessKeyId,
                UserName = userName,
            });

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Policy successfully deleted.");
            }
            else
            {
                Console.WriteLine("Could not delete policy.");
            }
        }
    }

    // snippet-end:[IAM.dotnetv3.DeleteAccessKeyExample]
}
