// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DescribeLogGroupsExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.CloudWatchLogs;
    using Amazon.CloudWatchLogs.Model;

    /// <summary>
    /// Retrieves information about existing Amazon CloudWatch Logs log groups
    /// and displays the information on the console. The example was created
    /// using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DescribeLogGroups
    {
        // snippet-start:[CloudWatchLogs.dotnetv3.DescribeLogGroupsExample]
        public static async Task Main()
        {
            // Creates a CloudWatch Logs client using the default
            // user. If you need to work with resources in another
            // AWS Region than the one defined for the default user,
            // pass the AWS Region as a parameter to the client constructor.
            var client = new AmazonCloudWatchLogsClient();

            var request = new DescribeLogGroupsRequest
            {
                Limit = 5,
            };

            var response = await client.DescribeLogGroupsAsync(request);

            if (response.LogGroups.Count > 0)
            {
                do
                {
                    response.LogGroups.ForEach(lg =>
                    {
                        Console.WriteLine($"{lg.LogGroupName} is associated with the key: {lg.KmsKeyId}.");
                        Console.WriteLine($"Created on: {lg.CreationTime.Date.Date}");
                        Console.WriteLine($"Date for this group will be stored for: {lg.RetentionInDays} days.\n");
                    });
                }
                while (response.NextToken is not null);
            }
        }

        // snippet-end:[CloudWatchLogs.dotnetv3.DescribeLogGroupsExample]
    }
}
