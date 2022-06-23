// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListDashboardsExample
{
    // snippet-start:[CloudWatch.dotnetv3.ListDashboardsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// Shows how to retrieve a list of Amazon CloudWatch dashboards. This
    /// example was written using AWSSDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class ListDashboards
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();
            var dashboards = await ListDashboardsAsync(cwClient);

            DisplayDashboardList(dashboards);
        }

        /// <summary>
        /// Get the list of available dashboards.
        /// </summary>
        /// <param name="client">The initialized CloudWatch client used to
        /// retrieve a list of defined dashboards.</param>
        /// <returns>A list of DashboardEntry objects.</returns>
        public static async Task<List<DashboardEntry>> ListDashboardsAsync(IAmazonCloudWatch client)
        {
            var response = await client.ListDashboardsAsync(new ListDashboardsRequest());
            return response.DashboardEntries;
        }

        /// <summary>
        /// Displays the name of each CloudWatch Dashboard in the list passed
        /// to the method.
        /// </summary>
        /// <param name="dashboards">A list of DashboardEntry objects.</param>
        public static void DisplayDashboardList(List<DashboardEntry> dashboards)
        {
            if (dashboards.Count > 0)
            {
                Console.WriteLine("The following dashboards are defined:");
                foreach (var dashboard in dashboards)
                {
                    Console.WriteLine($"Name: {dashboard.DashboardName} Last modified: {dashboard.LastModified}");
                }
            }
            else
            {
                Console.WriteLine("No dashboards found.");
            }
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.ListDashboardsExample]
}
