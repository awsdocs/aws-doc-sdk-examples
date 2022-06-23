// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace StartInstancesExample
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to start a list of Amazon Elastic Compute Cloud (Amazon EC2)
    /// instances using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class StartInstances
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and uses it to call the
        /// StartInstancesAsync method to start the listed Amazon EC2 instances.
        /// </summary>
        public static async Task Main()
        {
            string ec2InstanceId = "i-0123456789abcdef0";

            // If your EC2 instances are not in the same AWS Region as
            // the default users on your system, you need to supply
            // the AWS Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var request = new StartInstancesRequest
            {
                InstanceIds = new List<string> { ec2InstanceId },
            };

            var response = await client.StartInstancesAsync(request);

            if (response.StartingInstances.Count > 0)
            {
                var instances = response.StartingInstances;
                instances.ForEach(i =>
                {
                    Console.WriteLine($"Successfully started the EC2 Instance with InstanceID: {i.InstanceId}.");
                });
            }
        }
    }
}
