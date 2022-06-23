// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListMetricsExample
{
    // snippet-start:[CloudWatch.dotnetv3.ListMetricsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example demonstrates how to list metrics for Amazon CloudWatch.
    /// The example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class ListMetrics
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();

            var filter = new DimensionFilter
            {
                Name = "InstanceType",
                Value = "t1.micro",
            };
            string metricName = "CPUUtilization";
            string namespaceName = "AWS/EC2";

            await ListMetricsAsync(cwClient, filter, metricName, namespaceName);
        }

        /// <summary>
        /// Retrieve CloudWatch metrics using the supplied filter, metrics name,
        /// and namespace.
        /// </summary>
        /// <param name="client">An initialized CloudWatch client.</param>
        /// <param name="filter">The filter to apply in retrieving metrics.</param>
        /// <param name="metricName">The metric name for which to retrieve
        /// information.</param>
        /// <param name="nameSpaceName">The name of the namespace from which
        /// to retrieve metric information.</param>
        public static async Task ListMetricsAsync(
            IAmazonCloudWatch client,
            DimensionFilter filter,
            string metricName,
            string nameSpaceName)
        {
            var request = new ListMetricsRequest
            {
                Dimensions = new List<DimensionFilter>() { filter },
                MetricName = metricName,
                Namespace = nameSpaceName,
            };

            var response = new ListMetricsResponse();
            do
            {
                response = await client.ListMetricsAsync(request);

                if (response.Metrics.Count > 0)
                {
                    foreach (var metric in response.Metrics)
                    {
                        Console.WriteLine(metric.MetricName +
                          " (" + metric.Namespace + ")");

                        foreach (var dimension in metric.Dimensions)
                        {
                            Console.WriteLine("  " + dimension.Name + ": "
                              + dimension.Value);
                        }
                    }
                }
                else
                {
                    Console.WriteLine("No metrics found.");
                }

                request.NextToken = response.NextToken;
            }
            while (!string.IsNullOrEmpty(response.NextToken));
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.ListMetricsExample]
}
