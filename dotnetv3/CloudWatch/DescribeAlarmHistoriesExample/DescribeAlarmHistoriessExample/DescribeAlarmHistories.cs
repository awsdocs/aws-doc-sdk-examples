// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DescribeAlarmHistoriesExample
{
    // snippet-start:[CloudWatch.dotnetv3.DescribeAlarmHistoriesExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example retrieves a list of Amazon CloudWatch alarms and, for
    /// each one, displays its history. The example was created using the
    /// AWS SDK for .NET 3.7 and .NET Core 5.0.
    /// </summary>
    public class DescribeAlarmHistories
    {
        /// <summary>
        /// Retrieves a list of alarms and then passes each name to the
        /// DescribeAlarmHistoriesAsync method to retrieve its history.
        /// </summary>
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();
            var response = await cwClient.DescribeAlarmsAsync();

            foreach (var alarm in response.MetricAlarms)
            {
                await DescribeAlarmHistoriesAsync(cwClient, alarm.AlarmName);
            }
        }

        /// <summary>
        /// Retrieves the CloudWatch alarm history for the alarm name passed
        /// to the method.
        /// </summary>
        /// <param name="client">An initialized CloudWatch client object.</param>
        /// <param name="alarmName">The CloudWatch alarm for which to retrieve
        /// history information.</param>
        public static async Task DescribeAlarmHistoriesAsync(IAmazonCloudWatch client, string alarmName)
        {
            var request = new DescribeAlarmHistoryRequest
            {
                AlarmName = alarmName,
                EndDateUtc = DateTime.Today,
                HistoryItemType = HistoryItemType.Action,
                MaxRecords = 1,
                StartDateUtc = DateTime.Today.Subtract(TimeSpan.FromDays(30)),
            };

            var response = new DescribeAlarmHistoryResponse();

            do
            {
                response = await client.DescribeAlarmHistoryAsync(request);

                foreach (var item in response.AlarmHistoryItems)
                {
                    Console.WriteLine(item.AlarmName);
                    Console.WriteLine(item.HistorySummary);
                    Console.WriteLine();
                }

                request.NextToken = response.NextToken;
            }
            while (!string.IsNullOrEmpty(response.NextToken));
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.DescribeAlarmHistoriesExample]
}
