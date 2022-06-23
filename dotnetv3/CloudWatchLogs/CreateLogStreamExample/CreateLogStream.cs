// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateLogStreamExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.CloudWatchLogs;
    using Amazon.CloudWatchLogs.Model;

    /// <summary>
    /// Shows how to create an Amazon CloudWatch Logs stream for a CloudWatch
    /// log group. The example was created using the AWS SDK for .NET version
    /// 3.7 and .NET Core 5.0.
    /// </summary>
    public class CreateLogStream
    {
        // snippet-start:[CloudWatchLogs.dotnetv3.CreateLogStreamExample]
        public static async Task Main()
        {
            // This client object will be associated with the same AWS Region
            // as the default user on this system. If you need to use a
            // different AWS Region, pass it as a parameter to the client
            // constructor.
            var client = new AmazonCloudWatchLogsClient();
            string logGroupName = "cloudwatchlogs-example-loggroup";
            string logStreamName = "cloudwatchlogs-example-logstream";

            var request = new CreateLogStreamRequest
            {
                LogGroupName = logGroupName,
                LogStreamName = logStreamName,
            };

            var response = await client.CreateLogStreamAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{logStreamName} successfully created for {logGroupName}.");
            }
            else
            {
                Console.WriteLine("Could not create stream.");
            }
        }

        // snippet-end:[CloudWatchLogs.dotnetv3.CreateLogStreamExample]
    }
}
