// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DeleteAccessKeyExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    /// <summary>
    /// Shows how to delete an AWS Identity and Access Management Access Key.
    /// The example was created using the AWS SDK for .NET version 3.7 and .NET
    /// Core 5.0.
    /// </summary>
    public class DeleteAccessKey
    {
        /// <summary>
        /// Initializes an IAM Client Object and then deletes the IAM AccessKey.
        /// </summary>
        public static async Task Main()
        {
            string accessKeyId = "AKIA2IGW2OYQN4D7PKW3";
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
}
