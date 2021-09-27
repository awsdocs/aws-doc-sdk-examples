// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DeleteSecurityGroupExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to use Amazon Elastic Compute Cloud (Amazon EC2) and the
    /// AWS SDK for .NET to delete an existing security group. This example
    /// was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteSecurityGroup
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then uses it to call
        /// the DeleteSecurityGroupAsync method to delete the security group.
        /// </summary>
        public static async Task Main()
        {
            string secGroupId = "sg-05c1b890f35c2c89e";
            string groupName = "sample-security-group";

            // If your Amazon EC2 security grup is not defined in the same AWS
            // Region as the default user on your system, you need to supply
            // the AWS Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var request = new DeleteSecurityGroupRequest
            {
                GroupId = secGroupId,
                GroupName = groupName,
            };

            var response = await client.DeleteSecurityGroupAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully deleted {groupName}.");
            }
            else
            {
                Console.WriteLine($"Could not delete {groupName}.");
            }
        }
    }
}
