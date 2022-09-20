// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DescribeInstancesExample
{
    // snippet-start:[EC2.dotnetv3.DescribeInstancesExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// This example shows how to list your Amazon Elastic Compute Cloud
    /// (Amazon EC2) instances. It was created using the AWS SDK for .NET
    /// version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DescribeInstances
    {
        /// <summary>
        /// The Main method creates the Amazon EC2 client object and then calls
        /// first GetInstanceDescriptions and then GetInstanceDescriptionsFiltered
        /// to display the list of Amazon EC2 Instances attached to the default
        /// account.
        /// </summary>
        public static async Task Main()
        {
            // If the region of the EC2 instances you want to list is different
            // from the default user's region, you need to specify the region
            // when you create the client object.
            // For example: RegionEndpoint.USWest1.
            var eC2Client = new AmazonEC2Client();

            // List all EC2 instances.
            await GetInstanceDescriptions(eC2Client);

            string tagName = "IncludeInList";
            string tagValue = "Yes";
            await GetInstanceDescriptionsFiltered(eC2Client, tagName, tagValue);
        }

        /// <summary>
        /// This method uses a paginator to list all of the EC2 Instances
        /// attached to the default account.
        /// </summary>
        /// <param name="client">The Amazon EC2 client object used to call
        /// the DescribeInstances method.</param>
        public static async Task GetInstanceDescriptions(AmazonEC2Client client)
        {
            var request = new DescribeInstancesRequest();

            Console.WriteLine("Showing all instances:");
            var paginator = client.Paginators.DescribeInstances(request);

            await foreach (var response in paginator.Responses)
            {
                foreach (var reservation in response.Reservations)
                {
                    foreach (var instance in reservation.Instances)
                    {
                        Console.Write($"Instance ID: {instance.InstanceId}");
                        Console.WriteLine($"\tCurrent State: {instance.State.Name}");
                    }
                }
            }
        }

        /// <summary>
        /// This method lists the EC2 instances for this account which have set
        /// the tag named in the tagName parameter with the value in the tagValue
        /// parameter.
        /// </summary>
        /// <param name="client">The Amazon EC2 client object used to call
        /// the DescribeInstances method.</param>
        /// <param name="tagName">A string representing the name of the tag to
        /// filter on.</param>
        /// <param name="tagValue">A string representing the value of the tag
        /// to filter on.</param>
        public static async Task GetInstanceDescriptionsFiltered(AmazonEC2Client client, 
            string tagName, string tagValue)
        {
            // This is the tag we want to use to filter
            // the results of our list of instances.
            var filters = new List<Filter>
            {
                new Filter
                {
                    Name = $"tag:{tagName}",
                    Values = new List<string>
                    {
                        tagValue,
                    },
                },
            };
            var request = new DescribeInstancesRequest
            {
                Filters = filters,
            };

            Console.WriteLine("\nShowing instances with tag: \"IncludeInList\" set to \"Yes\".");
            var paginator = client.Paginators.DescribeInstances(request);

            await foreach (var response in paginator.Responses)
            {
                foreach (var reservation in response.Reservations)
                {
                    foreach (var instance in reservation.Instances)
                    {
                        Console.Write($"Instance ID: {instance.InstanceId} ");
                        Console.WriteLine($"\tCurrent State: {instance.State.Name}");
                    }
                }
            }
        }
    }
    // snippet-end:[EC2.dotnetv3.DescribeInstancesExample]
}
