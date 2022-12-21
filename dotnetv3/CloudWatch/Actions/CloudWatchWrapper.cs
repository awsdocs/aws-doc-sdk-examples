// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Text;
using Amazon.CloudWatch;
using Amazon.CloudWatch.Model;
using static System.Net.Mime.MediaTypeNames;

namespace ServiceActions;

// snippet-start:[CloudWatch.dotnetv3.CloudWatchWrapper]

/// <summary>
/// Wrapper class for CloudWatch methods.
/// </summary>
public class CloudWatchWrapper
{
    private readonly IAmazonCloudWatch _amazonCloudWatch;
    public CloudWatchWrapper(IAmazonCloudWatch amazonCloudWatch)
    {
        _amazonCloudWatch = amazonCloudWatch;
    }

    // snippet-start:[CloudWatch.dotnetv3.ListMetrics]

    /// <summary>
    /// List metrics available within a namespace.
    /// </summary>
    /// <param name="metricNamespace">Metrics namespace to use when listing metrics.</param>
    /// <returns>The list of metrics.</returns>
    public async Task<List<Metric>> ListMetrics(string metricNamespace)
    {
        var results = new List<Metric>();
        var paginateMetrics = _amazonCloudWatch.Paginators.ListMetrics(
            new ListMetricsRequest
            {
                Namespace = metricNamespace
            });
        // Get the entire list using the paginator.
        await foreach (var metric in paginateMetrics.Metrics)
        {
            results.Add(metric);
        }

        return results;
    }
    // snippet-end:[CloudWatch.dotnetv3.ListMetrics]

    // snippet-start:[CloudWatch.dotnetv3.GetMetricStatistics]

    /// <summary>
    /// Get statistics for a specific metric.
    /// </summary>
    /// <param name="metricNamespace"></param>
    /// <param name="metricName"></param>
    /// <param name="dimensions"></param>
    /// <param name="days"></param>
    /// <param name="period"></param>
    /// <returns></returns>
    public async Task<List<Datapoint>> GetMetricStatistics(string metricNamespace, string metricName, List<Dimension> dimensions, int days, int period)
    {
        var metricStatistics = await _amazonCloudWatch.GetMetricStatisticsAsync(
            new GetMetricStatisticsRequest()
            {
                Namespace = metricNamespace,
                MetricName = metricName,
                Dimensions = dimensions,
                StartTimeUtc = DateTime.UtcNow.AddDays(-days),
                EndTimeUtc = DateTime.UtcNow,
                Period = period

            });

        return metricStatistics.Datapoints;
    }
    // snippet-end:[CloudWatch.dotnetv3.GetMetricStatistics]

    // snippet-start:[CloudWatch.dotnetv3.PutDashboard]

    /// <summary>
    /// Create or add to a dashboard with metrics.
    /// </summary>
    /// <param name="dashboardName"></param>
    /// <param name="dashboardBody"></param>
    /// <returns></returns>
    public async Task<List<DashboardValidationMessage>> PutDashboard(string dashboardName, string dashboardBody)
    {
        // Updating a dashboard replaces all contents.
        // Best practice is to include a text widget indicating this dashboard was created programmatically.
        var dashboardResponse = await _amazonCloudWatch.PutDashboardAsync(
            new PutDashboardRequest()
            {
                DashboardName = dashboardName,
                DashboardBody = dashboardBody

            });

        return dashboardResponse.DashboardValidationMessages;
    }
    // snippet-end:[CloudWatch.dotnetv3.PutDashboard]

    // snippet-start:[CloudWatch.dotnetv3.GetMetricImage]

    /// <summary>
    /// Get an image for a metric.
    /// </summary>
    /// <param name="dashboardName"></param>
    /// <param name="dashboardBody"></param>
    /// <returns></returns>
    public async Task<MemoryStream> GetMetricImage(string metricWidget)
    {
        // Get an image for a metric.
        var imageResponse = await _amazonCloudWatch.GetMetricWidgetImageAsync(
            new GetMetricWidgetImageRequest()
            {
                MetricWidget = metricWidget

            });

        return imageResponse.MetricWidgetImage;
    }

    /// <summary>
    /// Save a metric image to a file.
    /// </summary>
    /// <param name="memoryStream"></param>
    /// <param name="metricName"></param>
    /// <returns>The name of the file.</returns>
    public string SaveMericImage(MemoryStream memoryStream, string metricName)
    {
        var metricFileName = $"{metricName}_{DateTime.Now.Ticks}.png";
        using var sr = new StreamReader(memoryStream);
        string data = sr.ReadToEnd();
        //File.WriteAllBytes(metricFileName, Convert.FromBase64String(data));
        File.WriteAllBytes(metricFileName, memoryStream.ToArray());
        var filePath = Path.Join(System.AppDomain.CurrentDomain.BaseDirectory,
            metricFileName);
        return filePath;
    }
    // snippet-end:[CloudWatch.dotnetv3.GetMetricImage]
}
// snippet-start:[CloudWatch.dotnetv3.CloudWatchWrapper]