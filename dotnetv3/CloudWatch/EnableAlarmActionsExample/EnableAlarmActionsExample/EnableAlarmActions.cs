// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace EnableAlarmActionsExample
{
    // snippet-start:[CloudWatch.dotnetv3.EnableAlarmActionsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example shows how to enable the Amazon CloudWatch actions for
    /// one or more CloudWatch alarms. The example was created using the
    /// AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class EnableAlarmActions
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();
            var alarmNames = new List<string>
            {
                "ALARM_NAME",
                "ALARM_NAME_2",
            };

            var success = await EnableAlarmActionsAsync(cwClient, alarmNames);

            if (success)
            {
                Console.WriteLine("Alarm action(s) successfully enabled.");
            }
            else
            {
                Console.WriteLine("Alarm action(s) were not enabled.")
            }
        }

        /// <summary>
        /// Enable the actions for the list of CloudWatch alarm names passed
        /// in the alarmNames parameter.
        /// </summary>
        /// <param name="client">An initialized CloudWatch client object.</param>
        /// <param name="alarmNames">The list of CloudWatch alarms to enable.</param>
        /// <returns>A Boolean value indicating the success of the call.</returns>
        public static async Task<bool> EnableAlarmActionsAsync(IAmazonCloudWatch client, List<string> alarmNames)
        {
            var request = new EnableAlarmActionsRequest
            {
                AlarmNames = alarmNames,
            };

            var response = await client.EnableAlarmActionsAsync(request);

            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.EnableAlarmActionsExample]
}
