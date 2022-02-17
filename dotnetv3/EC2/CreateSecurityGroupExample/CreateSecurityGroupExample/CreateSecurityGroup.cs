// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateSecurityGroupExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to create a security group for an Amazon Elastic Compute
    /// Cloud (Amazon EC2) VPC using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class CreateSecurityGroup
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and uses the
        /// CreateSecurityGroupAsync method to create the security group.
        /// </summary>
        public static async Task Main()
        {
            string vpcId = "vpc-0e304cc1627062b88";
            string vpcDescription = "Sample security group";
            string groupName = "sample-security-group";

            var client = new AmazonEC2Client();
            var response = await client.CreateSecurityGroupAsync(new CreateSecurityGroupRequest
            {
                Description = vpcDescription,
                GroupName = groupName,
                VpcId = vpcId,
            });

            string groupId = response.GroupId;

            Console.WriteLine($"Successfully created security group: {groupName} with ID: {groupId}");
        }
    }
}
