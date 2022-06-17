// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateVPCExample
{
    // snippet-start:[EC2.dotnetv3.CreateVPCExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to create an Amazon Elastic Compute Cloud (Amazon EC2) VPC
    /// using the AWS SDK for .NET and .NET Core 5.0.
    /// </summary>
    public class CreateVPC
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then calls the
        /// CreateVpcAsync method to create the VPC.
        /// </summary>
        public static async Task Main()
        {
            // If you do not want to create the VPC in the same AWS Region as
            // the default users on your system, you need to supply the AWS
            // Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var response = await client.CreateVpcAsync(new CreateVpcRequest
            {
                CidrBlock = "10.0.0.0/16",
            });

            Vpc vpc = response.Vpc;

            if (vpc is not null)
            {
                Console.WriteLine($"Created VPC with ID: {vpc.VpcId}.");
            }
        }
    }

    // snippet-end:[EC2.dotnetv3.CreateVPCExample]
}
