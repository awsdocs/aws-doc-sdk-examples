// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace RebootInstancesExample
{
    // snippet-start:[EC2.dotnetv3.RebootInstancesExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to reboot Amazon Elastic Compute Cloud (Amazon EC2) instances
    /// using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class RebootInstances
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then calls
        /// RebootInstancesAsync to reboot the instance(s) in the ectInstanceId
        /// list.
        /// </summary>
        public static async Task Main()
        {
            string ec2InstanceId = "i-0123456789abcdef0";

            // If your EC2 instances are not in the same AWS Region as
            // the default users on your system, you need to supply
            // the AWS Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var request = new RebootInstancesRequest
            {
                InstanceIds = new List<string> { ec2InstanceId },
            };

            var response = await client.RebootInstancesAsync(request);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Instance(s) successfully rebooted.");
            }
            else
            {
                Console.WriteLine("Could not reboot one or more instances.");
            }
        }
    }
    // snippet-end:[EC2.dotnetv3.RebootInstancesExample]
}
