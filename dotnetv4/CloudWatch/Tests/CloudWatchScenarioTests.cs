// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Text.Json;
using Amazon.CloudWatch.Model;

namespace CloudWatchTests;

/// <summary>
/// Unit tests for the CloudWatch Basics scenario. These make no service calls, so they
/// run without credentials.
/// </summary>
public class CloudWatchScenarioTests
{
    /// <summary>
    /// Parse a dashboard body and return its metric widget's properties.
    /// </summary>
    private static JsonElement MetricWidgetProperties(string body)
    {
        using var document = JsonDocument.Parse(body);
        var widget = document.RootElement
            .GetProperty("widgets")
            .EnumerateArray()
            .Single(w => w.GetProperty("type").GetString() == "metric");
        return widget.GetProperty("properties").Clone();
    }

    /// <summary>
    /// The dashboard body must be valid JSON. It is built by string interpolation, so a
    /// stray quote or a missing brace would only show up as a service-side error.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_ProducesValidJson()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>()
        };

        var body = CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
            "AWS/EC2", metric, "us-west-2");

        using var document = JsonDocument.Parse(body);
        Assert.Equal(2, document.RootElement.GetProperty("widgets").GetArrayLength());
    }

    /// <summary>
    /// A metric widget must name its region, because a dashboard can chart metrics from
    /// several regions and so cannot supply a default.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_NamesItsRegion()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>()
        };

        var body = CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
            "AWS/EC2", metric, "eu-central-1");

        var properties = MetricWidgetProperties(body);
        Assert.Equal("eu-central-1", properties.GetProperty("region").GetString());
        Assert.Equal("CPUUtilization", properties.GetProperty("title").GetString());
    }

    /// <summary>
    /// A metric with no dimensions charts as just its namespace and name.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_WithNoDimensions()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>()
        };

        var body = CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
            "AWS/EC2", metric, "us-west-2");

        var spec = MetricWidgetProperties(body)
            .GetProperty("metrics")[0]
            .EnumerateArray()
            .Select(element => element.GetString())
            .ToList();
        Assert.Equal(new[] { "AWS/EC2", "CPUUtilization" }, spec);
    }

    /// <summary>
    /// Dimensions are flattened into the metric spec as alternating name and value
    /// entries, which is the format a metric widget expects.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_FlattensDimensions()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>
            {
                new() { Name = "InstanceId", Value = "i-abc123" },
                new() { Name = "InstanceType", Value = "t3.micro" }
            }
        };

        var body = CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
            "AWS/EC2", metric, "us-west-2");

        var spec = MetricWidgetProperties(body)
            .GetProperty("metrics")[0]
            .EnumerateArray()
            .Select(element => element.GetString())
            .ToList();
        Assert.Equal(
            new[]
            {
                "AWS/EC2", "CPUUtilization",
                "InstanceId", "i-abc123",
                "InstanceType", "t3.micro"
            },
            spec);
    }

    /// <summary>
    /// The namespace argument is what ends up in the widget, not the metric's own
    /// namespace property. Step 6 picks the namespace before it picks the metric.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_UsesTheNamespaceArgument()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>()
        };

        var body = CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
            "AWS/Lambda", metric, "us-west-2");

        var spec = MetricWidgetProperties(body).GetProperty("metrics")[0];
        Assert.Equal("AWS/Lambda", spec[0].GetString());
    }

    /// <summary>
    /// The scenario charts with the Average statistic over five-minute periods.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void BuildDashboardBody_SetsViewStatAndPeriod()
    {
        var metric = new Metric
        {
            Namespace = "AWS/EC2",
            MetricName = "CPUUtilization",
            Dimensions = new List<Dimension>()
        };

        var properties = MetricWidgetProperties(
            CloudWatchScenario.CloudWatchScenario.BuildDashboardBody(
                "AWS/EC2", metric, "us-west-2"));

        Assert.Equal("timeSeries", properties.GetProperty("view").GetString());
        Assert.Equal("Average", properties.GetProperty("stat").GetString());
        Assert.Equal(300, properties.GetProperty("period").GetInt32());
    }
}