// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DeleteAlarmsExample
{
    // snippet-start:[CloudWatch.dotnetv3.DeleteAlarmsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example shows how to delete Amazon CloudWatch alarms. The example
    /// was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteAlarms
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();

            var alarmNames = CreateAlarmNameList();
            await DeleteAlarmsAsyncExample(cwClient, alarmNames);
        }

        /// <summary>
        /// Delete the alarms whose names are listed in the alarmNames parameter.
        /// </summary>
        /// <param name="client">The initialized Amazon CloudWatch client.</param>
        /// <param name="alarmNames">A list of names for the alarms to be
        /// deleted.</param>
        public static async Task DeleteAlarmsAsyncExample(IAmazonCloudWatch client, List<string> alarmNames)
        {
            var request = new DeleteAlarmsRequest
            {
                AlarmNames = alarmNames,
            };

            try
            {
                var response = await client.DeleteAlarmsAsync(request);

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    Console.WriteLine("Alarms successfully deleted:");
                    alarmNames
                        .ForEach(name => Console.WriteLine($"{name}"));
                }
            }
            catch (ResourceNotFoundException ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }

        /// <summary>
        /// Defines and returns the list of alarm names to delete.
        /// </summary>
        /// <returns>A list of alarm names.</returns>
        public static List<string> CreateAlarmNameList()
        {
            // The list of alarm names passed to DeleteAlarmsAsync
            // can contain up to 100 alarm names.
            var theList = new List<string>
            {
                "ALARM_NAME_1",
                "ALARM_NAME_2",
            };

            return theList;
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.DeleteAlarmsExample]
}
