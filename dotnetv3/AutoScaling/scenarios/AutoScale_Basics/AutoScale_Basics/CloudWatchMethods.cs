// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace AutoScale_Basics
{
    // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.CloudWatchMethods]
    using Amazon.CloudWatch;
    using Amazon.CloudWatch.Model;

    // The single method of this class is used to display the metrics collected
    // for the AWS Auto Scaling group created by the AWS AutoScaling scenario.
    public class CloudWatchMethods
    {
        public static async Task<List<Metric>> GetCloudWatchMetricsAsync(string groupName)
        {
            var client = new AmazonCloudWatchClient();

            var filter = new DimensionFilter
            {
                Name = "AutoScalingGroupName",
                Value = $"{groupName}",
            };

            var request = new ListMetricsRequest
            {
                MetricName = "AutoScalingGroupName",
                Dimensions = new List<DimensionFilter> { filter },
                Namespace = "AWS/AutoScaling",
            };

            var response = await client.ListMetricsAsync(request);

            return response.Metrics;
        }

        public static async Task<List<Datapoint>> GetMetricStatisticsAsync(string groupName)
        {
            var client = new AmazonCloudWatchClient();

            var metricDimensions = new List<Dimension>
            {
                new Dimension
                {
                    Name = "AutoScalingGroupName",
                    Value = $"{groupName}",
                },
            };

            // The start time will be yesterday.
            var startTime = DateTime.UtcNow.AddDays(-1);

            var request = new GetMetricStatisticsRequest
            {
                MetricName = "AutoScalingGroupName",
                Dimensions = metricDimensions,
                Namespace = "AWS/AutoScaling",
                Period = 60, // 60 seconds
                Statistics = new List<string>() { "Minimum" },
                StartTimeUtc = startTime,
                EndTimeUtc = DateTime.UtcNow,
            };

            var response = await client.GetMetricStatisticsAsync(request);

            return response.Datapoints;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.CloudWatchMethods]
    }
}
