// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DeleteVPCExample
{
    // snippet-start:[EC2.dotnetv3.DeleteVPCExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to delete an existing Amazon Elastic Compute Cloud
    /// (Amazon EC2) VPC. The example was created using the AWS SDK for .NET
    /// version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteVPC
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then calls the
        /// DeleteVpcAsync method to delete the VPC.
        /// </summary>
        public static async Task Main()
        {
            string vpcId = "vpc-0123456789abc";

            // If your Amazon EC2 VPC is not defined in the same AWS Region as
            // the default AWS user on your system, you need to supply the AWS
            // Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var request = new DeleteVpcRequest
            {
                VpcId = vpcId,
            };

            var response = await client.DeleteVpcAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully deleted VPC with ID: {vpcId}.");
            }
        }
    }
    // snippet-end:[EC2.dotnetv3.DeleteVPCExample]
}
