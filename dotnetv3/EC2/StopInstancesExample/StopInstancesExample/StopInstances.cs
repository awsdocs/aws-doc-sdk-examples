// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace StopInstancesExample
{
    // snippet-start:[EC2.dotnetv3.StopInstancesExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to stop a list of Amazon Elastic Compute Cloud (Amazon EC2)
    /// instances using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class StopInstances
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and uses it to call the
        /// StartInstancesAsync method to stop the listed Amazon EC2 instances.
        /// </summary>
        public static async Task Main()
        {
            string ec2InstanceId = "i-0123456789abcdef0";

            // If your EC2 instances are not in the same AWS Region as
            // the default user on your system, you need to supply
            // the AWS Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            // In addition to the list of instance Ids, the
            // request can also include the following properties:
            //     Force      When true forces the instances to
            //                stop but you have to check the integrity
            //                of the file system. Not recommended on
            //                Windows instances.
            //     Hibernate  When true, hibernates the instance if the
            //                instance was enabled for hibernation when
            //                it was launched.
            var request = new StopInstancesRequest
            {
                InstanceIds = new List<string> { ec2InstanceId },
            };

            var response = await client.StopInstancesAsync(request);

            if (response.StoppingInstances.Count > 0)
            {
                var instances = response.StoppingInstances;
                instances.ForEach(i =>
                {
                    Console.WriteLine($"Successfully stopped the EC2 Instance " +
                                      $"with InstanceID: {i.InstanceId}.");
                });
            }
        }
    }
    // snippet-end:[EC2.dotnetv3.StopInstancesExample]
}
