// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace GetDashboardExample
{
    // snippet-start:[CloudWatch.dotnetv3.GetDashboardExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example shows how to retrieve the details of an Amazon CloudWatch
    /// dashboard. The return value from the call to GetDashboard is a json
    /// object representing the widgets in the dashboard. The example was
    /// created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class GetDashboard
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();
            string dashboardName = "CloudWatch-Default";

            var body = await GetDashboardAsync(cwClient, dashboardName);

            Console.WriteLine(body);
        }

        /// <summary>
        /// Get the json that represents the dashboard.
        /// </summary>
        /// <param name="client">An initialized CloudWatch client.</param>
        /// <param name="dashboardName">The name of the dashboard.</param>
        /// <returns>The string containing the json value describing the
        /// contents and layout of the CloudWatch dashboard.</returns>
        public static async Task<string> GetDashboardAsync(IAmazonCloudWatch client, string dashboardName)
        {

            var request = new GetDashboardRequest
            {
                DashboardName = dashboardName,
            };

            var response = await client.GetDashboardAsync(request);

            return response.DashboardBody;
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.GetDashboardExample]
}
