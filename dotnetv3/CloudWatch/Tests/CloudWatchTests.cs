// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.CloudWatch;
using Amazon.CloudWatch.Model;
using CloudWatchActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System.Text.Json.Serialization;
using System.Text.Json;
using System.Text;

namespace CloudWatchTests;

/// <summary>
/// CloudWatch tests.
/// </summary>
public class CloudWatchTests
{
    private readonly IConfiguration _configuration;
    private readonly ILoggerFactory _loggerFactory;
    private readonly CloudWatchWrapper _cloudWatchWrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public CloudWatchTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        _cloudWatchWrapper = new CloudWatchWrapper(
            new AmazonCloudWatchClient(),
            new Logger<CloudWatchWrapper>(_loggerFactory)
        );
    }

    /// <summary>
    /// List the metrics. Should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task ListMetrics_ShouldNotBeNull()
    {
        var result = await _cloudWatchWrapper.ListMetrics("AWS/Billing");

        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Get metric statistics. Should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task GetMetricStatistics_ShouldNotBeNull()
    {
        var result = await _cloudWatchWrapper.GetMetricStatistics(
            "AWS/Billing",
            "EstimatedCharges",
            new List<string>() { "Maximum" },
            new List<Dimension>() { new Dimension() { Name = "Currency", Value = "USD" } },
            7,
            86400);

        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Create or update a dashboard. Should execute without any validation messages.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task PutDashboard_ShouldNotBeNull()
    {
        var dashboardName = _configuration["dashboardName"];
        var newDashboard = new DashboardModel();
        _configuration.GetSection("dashboardExampleBody").Bind(newDashboard);
        var newDashboardString = JsonSerializer.Serialize(
            newDashboard,
            new JsonSerializerOptions
            {
                DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull
            });
        var validationMessages =
            await _cloudWatchWrapper.PutDashboard(dashboardName, newDashboardString);

        Assert.Empty(validationMessages);
    }

    /// <summary>
    /// Get a dashboard. Should have exactly three widgets.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task GetDashboard_ShouldNotBeNull()
    {
        var dashboardName = _configuration["dashboardName"];
        var dashboardString =
            await _cloudWatchWrapper.GetDashboard(dashboardName);
        var dashboard = JsonSerializer.Deserialize<DashboardModel>(dashboardString);

        Assert.Equal(3, dashboard!.Widgets.Count);
    }

    /// <summary>
    /// List dashboards. Should contain the new dashboard.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task ListDashboard_ShouldIncludeDashboard()
    {
        var dashboardName = _configuration["dashboardName"];
        var dashboards = await _cloudWatchWrapper.ListDashboards();

        Assert.Contains(dashboardName, dashboards.Select(d => d.DashboardName));
    }

    /// <summary>
    /// Put metric data. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task PutMetricData_ShouldSucceed()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];
        var nowUtc = DateTime.UtcNow;
        List<MetricDatum> customData = new List<MetricDatum>
        {
            new MetricDatum()
            {
                MetricName = customMetricName,
                Value = 1,
                TimestampUtc = nowUtc.AddMinutes(-1)
            }
        };
        var success = await _cloudWatchWrapper.PutMetricData(customMetricNamespace, customData);
        Assert.True(success);
    }

    /// <summary>
    /// Get a metric image. Should return a memory stream that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Integration")]
    public async Task GetMetricImage_ShouldSucceed()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];
        var memoryStream = await _cloudWatchWrapper.GetTimeSeriesMetricImage(customMetricNamespace, customMetricName, "Maximum", 10);

        Assert.NotEmpty(memoryStream.ToArray());
    }

    /// <summary>
    /// Save metric image file. Should save a file.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Unit")]
    public void SaveMetricImage_ShouldSucceed()
    {
        var customMetricName = _configuration["customMetricName"];
        using var test_stream = new MemoryStream(Encoding.UTF8.GetBytes("Test string."));
        var filePath = _cloudWatchWrapper.SaveMetricImage(test_stream, customMetricName);
        Assert.Contains(customMetricName, filePath);
        Assert.True(File.Exists(filePath));
    }

    /// <summary>
    /// Get metric data. Should return a collection that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task GetMetricData_ShouldSucceed()
    {
        var accountId = _configuration["accountId"];

        var query = new List<MetricDataQuery>()
        {
            new MetricDataQuery()
            {
                AccountId = accountId,
                Id = "m1",
                Label = "CloudWatch Usage Data",
                MetricStat = new MetricStat
                {
                    Metric = new Metric()
                    {
                        MetricName = "CallCount",
                        Namespace = "AWS/Usage",
                        Dimensions = new List<Dimension>()
                        {
                            new Dimension()
                            {
                                Name = "Type",
                                Value = "API"
                            },
                            new Dimension()
                            {
                                Name = "Resource",
                                Value = "ListMetrics"
                            },
                            new Dimension()
                            {
                            Name = "Service",
                            Value = "CloudWatch"
                            },
                            new Dimension()
                            {
                                Name = "Class",
                                Value = "None"
                            }
                        }
                    },
                    Period = 86400,
                    Stat = "Sum"
                }
            }
        };

        var metricData = await _cloudWatchWrapper.GetMetricData(
            20,
            true,
            DateTime.UtcNow.AddMinutes(1),
            20,
            query);

        Assert.NotEmpty(metricData);
    }

    /// <summary>
    /// Create a metric alarm. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    [Trait("Category", "Integration")]
    public async Task PutMetricEmailAlarm_ShouldSucceed()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];
        var alarmName = _configuration["exampleAlarmName"];

        var success = await _cloudWatchWrapper.PutMetricEmailAlarm(
            "Example test metric alarm",
            alarmName,
            ComparisonOperator.GreaterThanOrEqualToThreshold,
            customMetricName,
            customMetricNamespace,
            100,
            new List<string>());

        Assert.True(success);
    }

    /// <summary>
    /// Get a metric image. Should return a memory stream that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    [Trait("Category", "Unit")]
    public void AddEmailAlarmAction_ShouldIncludeAction()
    {
        var accountId = _configuration["accountId"];
        var region = _configuration["region"];
        var emailTopic = _configuration["emailTopic"];
        var alarmActions = new List<string>();

        _cloudWatchWrapper.AddEmailAlarmAction(accountId, region, emailTopic, alarmActions);

        Assert.Contains($"arn:aws:sns:{region}:{accountId}:{emailTopic}", alarmActions);
    }

    /// <summary>
    /// Describe all alarms. Should contain the new alarm.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    [Trait("Category", "Integration")]
    public async Task DescribeAlarms_ShouldIncludeAlarm()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var alarms = await _cloudWatchWrapper.DescribeAlarms();

        Assert.Contains(alarmName, alarms.Select(a => a.AlarmName));
    }

    /// <summary>
    /// Describe all alarms for a metric. Should contain the new alarm.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(13)]
    [Trait("Category", "Integration")]
    public async Task DescribeAlarmsForMetric_ShouldIncludeAlarm()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];
        var alarms = await _cloudWatchWrapper.DescribeAlarmsForMetric(customMetricNamespace, customMetricName);

        Assert.Contains(alarmName, alarms.Select(a => a.AlarmName));
    }

    /// <summary>
    /// Describe all alarms for a metric. Should not contain the new alarm.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(14)]
    [Trait("Category", "Integration")]
    public async Task DescribeAlarmsForMetric_ShouldNotIncludeAlarm()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var alarms = await _cloudWatchWrapper.DescribeAlarmsForMetric("AWS/Billing", "EstimatedCharges");

        Assert.DoesNotContain(alarmName, alarms.Select(a => a.AlarmName));
    }

    /// <summary>
    /// Describe alarm history. Should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(15)]
    [Trait("Category", "Integration")]
    public async Task DescribeAlarmHistory_ShouldNotBeNull()
    {
        var customMetricName = _configuration["customMetricName"];
        var history = await _cloudWatchWrapper.DescribeAlarmHistory(customMetricName, 1);

        Assert.NotNull(history);
    }

    /// <summary>
    /// Enable alarm actions. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(16)]
    [Trait("Category", "Integration")]
    public async Task EnableAlarmActions_ShouldSucceed()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var success = await _cloudWatchWrapper.EnableAlarmActions(new List<string> { alarmName });

        Assert.True(success);
    }

    /// <summary>
    /// Disable alarm actions. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(17)]
    [Trait("Category", "Integration")]
    public async Task DisableAlarmActions_ShouldSucceed()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var success = await _cloudWatchWrapper.DisableAlarmActions(new List<string> { alarmName });

        Assert.True(success);
    }

    /// <summary>
    /// Delete an alarm. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(18)]
    [Trait("Category", "Integration")]
    public async Task DeleteAlarms_ShouldSucceed()
    {
        var alarmName = _configuration["exampleAlarmName"];
        var success = await _cloudWatchWrapper.DeleteAlarms(new List<string> { alarmName });

        Assert.True(success);
    }

    /// <summary>
    /// Put an anomaly detector. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(19)]
    [Trait("Category", "Integration")]
    public async Task PutAnomalyDetector_ShouldSucceed()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];

        var detector = new SingleMetricAnomalyDetector
        {
            MetricName = customMetricName,
            Namespace = customMetricNamespace,
            Stat = "Maximum"
        };
        var success = await _cloudWatchWrapper.PutAnomalyDetector(detector);

        Assert.True(success);
    }

    /// <summary>
    /// Describe current anomaly detectors. Should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(20)]
    [Trait("Category", "Integration")]
    public async Task DescribeAnomalyDetectors_ShouldNotBeEmpty()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];
        var detectors = await _cloudWatchWrapper.DescribeAnomalyDetectors(customMetricNamespace, customMetricName);

        Assert.NotEmpty(detectors);
    }

    /// <summary>
    /// Delete an anomaly detector. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(21)]
    [Trait("Category", "Integration")]
    public async Task DeleteAnomalyDetector_ShouldSucceed()
    {
        var customMetricNamespace = _configuration["customMetricNamespace"];
        var customMetricName = _configuration["customMetricName"];

        var detector = new SingleMetricAnomalyDetector
        {
            MetricName = customMetricName,
            Namespace = customMetricNamespace,
            Stat = "Maximum"
        };
        var success = await _cloudWatchWrapper.DeleteAnomalyDetector(detector);

        Assert.True(success);
    }

    /// <summary>
    /// Put an anomaly detector. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(22)]
    [Trait("Category", "Integration")]
    public async Task DeleteDashboard_ShouldSucceed()
    {
        var dashboardName = _configuration["dashboardName"];

        var success = await _cloudWatchWrapper.DeleteDashboards(new List<string> { dashboardName });

        Assert.True(success);
    }
}