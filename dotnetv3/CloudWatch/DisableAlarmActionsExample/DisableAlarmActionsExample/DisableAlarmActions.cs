// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DisableAlarmActionsExammple
{
    // snippet-start:[CloudWatch.dotnetv3.DisableAlarmActionsExammple]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    /// <summary>
    /// This example shows how to disable the Amazon CloudWatch actions for
    /// one or more CloudWatch alarms. The example was created using the
    /// AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DisableAlarmActions
    {
        public static async Task Main()
        {
            IAmazonCloudWatch cwClient = new AmazonCloudWatchClient();
            var alarmNames = new List<string>
            {
                "ALARM_NAME",
                "ALARM_NAME_2",
            };

            var success = await DisableAlarmsActionsAsync(cwClient, alarmNames);

            if (success)
            {
                Console.WriteLine("Alarm action(s) successfully disabled.");
            }
            else
            {
                Console.WriteLine("Alarm action(s) were not disabled.")
            }
        }

        /// <summary>
        /// Disable the actions for the list of CloudWatch alarm names passed
        /// in the alarmNames parameter.
        /// </summary>
        /// <param name="client">An initialized CloudWatch client object.</param>
        /// <param name="alarmNames">The list of CloudWatch alarms to disable.</param>
        /// <returns>A Boolean value indicating the success of the call.</returns>
        public static async Task<bool> DisableAlarmsActionsAsync(
            IAmazonCloudWatch client,
            List<string> alarmNames)
        {
            var request = new DisableAlarmActionsRequest
            {
                AlarmNames = alarmNames,
            };

            var response = await client.DisableAlarmActionsAsync(request);

            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }
    }

    // snippet-end:[CloudWatch.dotnetv3.DisableAlarmActionsExammple]
}
