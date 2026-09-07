// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudWatch;
using Amazon.CloudWatch.Model;
using CloudWatchActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace CloudWatchScenario;

// snippet-start:[CloudWatch.dotnetv4.GettingStarted]
public class CloudWatchScenario
{
    /*
    Before running this .NET code example, set up your development environment, including your credentials.

    This scenario demonstrates the Amazon CloudWatch OpenTelemetry (OTel) experience.
    CloudWatch ingests OpenTelemetry metrics natively, and this example walks through what
    you do with them: turning on enrichment so CloudWatch can correlate incoming OTLP
    metrics with the resources that produced them, alarming on those metrics with a PromQL
    query, and finding out which individual series drove the alarm.

    A PromQL alarm works differently from a classic metric alarm. Rather than watching one
    metric and counting breaching periods, it evaluates a query that can match many series
    at once, and tracks each matching series separately as a contributor.

    Note that sending OTLP metrics to CloudWatch is not an AWS SDK operation. Metrics
    arrive over the OTLP protocol through the CloudWatch agent, an OpenTelemetry
    Collector, or an ADOT SDK. Everything this scenario does is configuration and querying
    around that ingestion path.

    This .NET example performs the following tasks:
        1. List metrics and namespaces from CloudWatch.
        2. Start OpenTelemetry enrichment for the account.
        3. Explain how OTLP metrics reach CloudWatch.
        4. Create an alarm that evaluates a PromQL query.
        5. Inspect the contributors to the PromQL alarm.
        6. Get metric statistics and chart the metric on a dashboard.
        7. Mute the alarm for a maintenance window.
        8. Clean up resources.
    */

    private static ILogger logger = null!;
    private static CloudWatchWrapper _cloudWatchWrapper = null!;
    private static CloudWatchOTelWrapper _otelWrapper = null!;
    private static IConfiguration _configuration = null!;

    private const string DefaultQuery = "avg by (host) (system_cpu_utilization) > 80";

    // Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
    private const int EvaluationInterval = 60;
    private const int PendingPeriod = 300;
    private const int RecoveryPeriod = 120;

    private static string _alarmName = null!;
    private static string _dashboardName = null!;
    private static string _muteRuleName = null!;

    // Tracks whether this run turned enrichment on, so that cleanup only turns off
    // enrichment that this run started.
    private static bool _startedEnrichment;
    private static bool _dashboardCreated;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
            services.AddAWSService<IAmazonCloudWatch>()
            .AddTransient<CloudWatchWrapper>()
            .AddTransient<CloudWatchOTelWrapper>()
        )
        .Build();

        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally, load local settings.
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<CloudWatchScenario>();

        _cloudWatchWrapper = host.Services.GetRequiredService<CloudWatchWrapper>();
        _otelWrapper = host.Services.GetRequiredService<CloudWatchOTelWrapper>();

        // Suffix the resource names so repeated runs do not collide.
        var suffix = new Random().Next(1000, 9999).ToString();
        _alarmName = $"doc-example-promql-alarm-{suffix}";
        _dashboardName = $"doc-example-dashboard-{suffix}";
        _muteRuleName = $"doc-example-mute-rule-{suffix}";

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the Amazon CloudWatch Basics scenario.");
        Console.WriteLine(new string('-', 80));
        Console.WriteLine(
            "\nCloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through" +
            "\nthat experience: it turns on OTel enrichment so CloudWatch can correlate incoming" +
            "\nOTLP metrics with the resources that produced them, alarms on those metrics with a" +
            "\nPromQL query, and shows you which individual series drove the alarm." +
            "\n" +
            "\nA PromQL alarm works differently from a classic metric alarm. Rather than watching" +
            "\none metric and counting breaching periods, it evaluates a query that can match many" +
            "\nseries at once, and tracks each one separately as a contributor.\n");

        try
        {
            var namespaces = await ListMetricsAndNamespaces();
            await StartOTelEnrichment();
            ExplainOtlpIngestion();
            await CreatePromQlAlarm();
            await InspectAlarmContributors();
            await GetStatisticsAndChartMetric(namespaces);
            await MuteAlarmForMaintenance();
            await CleanUp();

            Console.WriteLine(new string('-', 80));
            Console.WriteLine("CloudWatch Basics scenario is complete.");
            Console.WriteLine(new string('-', 80));
        }
        catch (Exception ex)
        {
            Console.WriteLine(new string('-', 80));
            logger.LogError(ex, "There was a problem executing the scenario.");
            await CleanUp();
            Console.WriteLine(new string('-', 80));
        }
    }

    /// <summary>
    /// List the metrics and namespaces already present in the account, to orient the
    /// reader before any configuration happens.
    /// </summary>
    /// <returns>The distinct namespaces found.</returns>
    private static async Task<List<string>> ListMetricsAndNamespaces()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("1. List metrics and namespaces");
        Console.WriteLine(
            "\nBefore configuring anything, let's see what CloudWatch is already collecting in" +
            "\nthis account by calling ListMetrics.\n");

        var metrics = await _cloudWatchWrapper.ListMetrics();
        var namespaces = metrics.Select(m => m.Namespace).Distinct().OrderBy(n => n).ToList();

        Console.WriteLine($"\tFound {metrics.Count} metrics across {namespaces.Count} namespaces:");
        foreach (var metricNamespace in namespaces.Take(10))
        {
            var count = metrics.Count(m => m.Namespace == metricNamespace);
            Console.WriteLine($"\t  {metricNamespace} ({count} metrics)");
        }

        if (!namespaces.Any())
        {
            Console.WriteLine(
                "\tNo metrics found in this account. The statistics and dashboard steps later on" +
                "\n\tneed an existing metric, so they will be skipped.");
        }

        Console.WriteLine(new string('-', 80));
        return namespaces!;
    }

    /// <summary>
    /// Start OTel enrichment, but only if it is not already running. Enrichment is what
    /// makes CloudWatch attach AWS resource context to incoming OTLP metrics.
    /// </summary>
    private static async Task StartOTelEnrichment()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("2. Start OpenTelemetry enrichment");
        Console.WriteLine(
            "\nEnrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics" +
            "\nyou send it. Without it, your metrics arrive as opaque series with no connection to" +
            "\nthe resources that emitted them." +
            "\n" +
            "\nWe check the current state first, and only start enrichment if it isn't already on.\n");

        var status = await _otelWrapper.GetOTelEnrichmentStatus();
        Console.WriteLine($"\tEnrichment status: {status}");

        if (status != OTelEnrichmentStatus.Running)
        {
            await _otelWrapper.StartOTelEnrichment();
            _startedEnrichment = true;

            status = await _otelWrapper.GetOTelEnrichmentStatus();
            Console.WriteLine($"\tEnrichment status: {status}");
            Console.WriteLine(
                "\n\tNote: this run started enrichment, so the cleanup step will stop it again.");
        }
        else
        {
            Console.WriteLine(
                "\n\tEnrichment was already running, so we will leave it alone. The cleanup step" +
                "\n\twill not stop it, because other workloads in this account may depend on it.");
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Explain that OTLP metric ingestion is not an AWS SDK operation. This step makes no
    /// service call; naming the gap explicitly is the point.
    /// </summary>
    private static void ExplainOtlpIngestion()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("3. Send OTLP metrics to CloudWatch");
        Console.WriteLine(
            "\nThis step is not an AWS SDK operation, and that's worth being explicit about." +
            "\nMetrics reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an" +
            "\nOpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics API to call." +
            "\n" +
            "\nPoint your collector at the CloudWatch metrics endpoint, which follows the pattern" +
            "\n\thttps://monitoring.<region>.amazonaws.com/v1/metrics" +
            "\n" +
            "\nThe endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp" +
            "\nexporter rather than otlp. The metrics endpoint signs as \"monitoring\".\n");
        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Create an alarm whose evaluation is a PromQL query.
    /// </summary>
    private static async Task CreatePromQlAlarm()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("4. Create a PromQL alarm");
        Console.WriteLine(
            "\nNow we alarm on those metrics. The comparison goes inside the query itself: a" +
            "\nPromQL alarm has no separate threshold, comparison operator, statistic, or period.\n");

        Console.WriteLine($"Enter a PromQL query, or press <ENTER> for the default\n[{DefaultQuery}]:");
        var input = Console.ReadLine();
        var query = string.IsNullOrWhiteSpace(input) ? DefaultQuery : input.Trim();

        await _otelWrapper.PutPromQLMetricAlarm(_alarmName, query,
            EvaluationInterval, PendingPeriod, RecoveryPeriod);

        Console.WriteLine($"\tCreated alarm {_alarmName}:");
        Console.WriteLine($"\t  query:              {query}");
        Console.WriteLine($"\t  evaluationInterval: {EvaluationInterval} seconds");
        Console.WriteLine($"\t  pendingPeriod:      {PendingPeriod} seconds");
        Console.WriteLine($"\t  recoveryPeriod:     {RecoveryPeriod} seconds");
        Console.WriteLine(
            "\n\tA PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which is" +
            "\n\tanother way it differs from a classic alarm.");

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Show which individual series the alarm's query matched. This is the step with no
    /// classic-alarm equivalent.
    /// </summary>
    private static async Task InspectAlarmContributors()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("5. Inspect the alarm's contributors");
        Console.WriteLine(
            "\nEach contributor is one series the query matched, identified by its label set." +
            "\nThis is how you find out which host is unhealthy rather than only that something" +
            "\nis. Classic alarms have no equivalent.\n");

        var contributors = await _otelWrapper.DescribeAlarmContributors(_alarmName);

        if (!contributors.Any())
        {
            Console.WriteLine(
                "\tNo contributors yet. The query matched no series, which usually means no OTel" +
                "\n\tmetrics with these labels have arrived. Once your collector is sending data," +
                "\n\teach matching series appears here with its labels and why it breached.");
        }
        else
        {
            Console.WriteLine($"\tFound {contributors.Count} contributors:");
            foreach (var contributor in contributors)
            {
                var labels = string.Join(", ",
                    contributor.ContributorAttributes.Select(a => $"{a.Key}={a.Value}"));
                Console.WriteLine($"\t  {contributor.ContributorId}: {labels}");
                Console.WriteLine($"\t    reason: {contributor.StateReason}");
            }
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Get statistics for an existing metric and chart it on a dashboard, so the reader can
    /// see what the alarm is evaluating.
    /// </summary>
    /// <param name="namespaces">The namespaces discovered in step 1.</param>
    private static async Task GetStatisticsAndChartMetric(List<string> namespaces)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("6. Get statistics and chart the metric on a dashboard");
        Console.WriteLine("\nStatistics and dashboards are how you see what the alarm is evaluating.\n");

        if (!namespaces.Any())
        {
            Console.WriteLine("\tSkipping statistics and dashboard because no metrics exist yet.");
            Console.WriteLine(new string('-', 80));
            return;
        }

        var metricNamespace = namespaces.First();
        var metrics = await _cloudWatchWrapper.ListMetrics(metricNamespace);
        var metric = metrics.FirstOrDefault();

        if (metric != null)
        {
            var datapoints = await _cloudWatchWrapper.GetMetricStatistics(
                metricNamespace, metric.MetricName, new List<string> { "Average", "Maximum" },
                metric.Dimensions, 1, 3600);

            Console.WriteLine(
                $"\tStatistics for {metricNamespace} {metric.MetricName} over the last day:");
            Console.WriteLine($"\t  Datapoints: {datapoints.Count}");
            foreach (var datapoint in datapoints.Take(3))
            {
                Console.WriteLine(
                    $"\t  {datapoint.Timestamp:u} average {datapoint.Average}, maximum {datapoint.Maximum}");
            }

            var dashboardBody = BuildDashboardBody(metricNamespace, metric);
            var validationMessages = await _cloudWatchWrapper.PutDashboard(_dashboardName, dashboardBody);
            _dashboardCreated = true;

            if (validationMessages.Any())
            {
                foreach (var message in validationMessages)
                {
                    Console.WriteLine($"\tDashboard validation message: {message.Message}");
                }
            }

            Console.WriteLine($"\tCreated dashboard {_dashboardName}.");

            var dashboard = await _cloudWatchWrapper.GetDashboard(_dashboardName);
            Console.WriteLine($"\tRead the dashboard back, {dashboard.Length} characters of widget JSON.");
        }
        else
        {
            Console.WriteLine($"\tNo metrics found in namespace {metricNamespace}, skipping.");
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Build a single-widget dashboard body that charts the given metric.
    /// </summary>
    private static string BuildDashboardBody(string metricNamespace, Metric metric)
    {
        var dimensionParts = string.Concat(
            metric.Dimensions.Select(d => $", \"{d.Name}\", \"{d.Value}\""));

        return $@"{{
    ""widgets"": [
        {{
            ""type"": ""text"",
            ""x"": 0, ""y"": 0, ""width"": 24, ""height"": 2,
            ""properties"": {{
                ""markdown"": ""This dashboard was created programmatically by an AWS SDK code example.""
            }}
        }},
        {{
            ""type"": ""metric"",
            ""x"": 0, ""y"": 2, ""width"": 12, ""height"": 6,
            ""properties"": {{
                ""metrics"": [[ ""{metricNamespace}"", ""{metric.MetricName}""{dimensionParts} ]],
                ""view"": ""timeSeries"",
                ""stat"": ""Average"",
                ""period"": 300,
                ""title"": ""{metric.MetricName}""
            }}
        }}
    ]
}}";
    }

    /// <summary>
    /// Create a mute rule so the alarm's actions are suppressed during a maintenance
    /// window, then read it back and find it in the account's rules.
    /// </summary>
    private static async Task MuteAlarmForMaintenance()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("7. Mute the alarm for a maintenance window");
        Console.WriteLine(
            "\nWhile a mute rule is active the targeted alarms keep evaluating and keep changing" +
            "\nstate, but their actions do not fire. This is the supported way to suppress" +
            "\nnotifications during planned maintenance, instead of disabling alarm actions and" +
            "\nhoping someone remembers to turn them back on.\n");

        // The expression is a five-field cron expression,
        // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five fields,
        // not the six that Amazon EventBridge uses. For a one-time window, use
        // at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration from
        // PT1M to P15D, so PT2H rather than 2h.
        const string expression = "cron(0 2 * * SUN)";
        const string duration = "PT2H";
        const string timezone = "America/Los_Angeles";

        await _otelWrapper.PutAlarmMuteRule(_muteRuleName, expression, duration, timezone,
            new List<string> { _alarmName });

        Console.WriteLine($"\tCreated mute rule {_muteRuleName}:");
        Console.WriteLine($"\t  schedule: {expression} for {duration}");
        Console.WriteLine($"\t  timezone: {timezone}");
        Console.WriteLine($"\t  targets:  {_alarmName}");
        Console.WriteLine(
            "\n\tNote the two formats here. The expression is a five-field cron expression, five" +
            "\n\trather than the six Amazon EventBridge uses. The duration is an ISO 8601" +
            "\n\tduration, so 'PT2H' and not '2h'." +
            "\n" +
            "\n\tAlso note that MuteTargets is set explicitly. If you leave it out, the rule" +
            "\n\tapplies to every alarm in the account.");

        var muteRule = await _otelWrapper.GetAlarmMuteRule(_muteRuleName);
        Console.WriteLine(
            $"\tRead the rule back: status {muteRule.Status}, mute type {muteRule.MuteType}.");

        var summaries = await _otelWrapper.ListAlarmMuteRules(_alarmName);
        Console.WriteLine($"\tFound {summaries.Count} mute rules targeting this alarm.");

        // Mute rule summaries carry no name field, only an ARN, so match on the ARN suffix.
        var match = summaries.FirstOrDefault(s =>
            s.AlarmMuteRuleArn.EndsWith($"/{_muteRuleName}") ||
            s.AlarmMuteRuleArn.EndsWith($":{_muteRuleName}"));

        if (match != null)
        {
            Console.WriteLine($"\t  matched by ARN: {match.AlarmMuteRuleArn} ({match.Status})");
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Delete the resources the scenario created. Each deletion is attempted independently
    /// so that one failure does not leave the remaining resources behind.
    /// </summary>
    private static async Task CleanUp()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("8. Clean up");
        Console.WriteLine("\nDelete the resources this scenario created? (y/n)");

        var response = Console.ReadLine();
        if (!string.Equals(response?.Trim(), "y", StringComparison.OrdinalIgnoreCase))
        {
            Console.WriteLine(
                "\tSkipping cleanup. Note that the alarm, dashboard, and mute rule are still in" +
                "\n\tyour account, and enrichment may still be running.");
            Console.WriteLine(new string('-', 80));
            return;
        }

        try
        {
            await _otelWrapper.DeleteAlarmMuteRule(_muteRuleName);
            Console.WriteLine($"\tDeleted mute rule {_muteRuleName}.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"\tCould not delete the mute rule: {ex.Message}");
        }

        try
        {
            await _cloudWatchWrapper.DeleteAlarms(new List<string> { _alarmName });
            Console.WriteLine($"\tDeleted alarm {_alarmName}.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"\tCould not delete the alarm: {ex.Message}");
        }

        if (_dashboardCreated)
        {
            try
            {
                await _cloudWatchWrapper.DeleteDashboards(new List<string> { _dashboardName });
                Console.WriteLine($"\tDeleted dashboard {_dashboardName}.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\tCould not delete the dashboard: {ex.Message}");
            }
        }

        if (_startedEnrichment)
        {
            try
            {
                await _otelWrapper.StopOTelEnrichment();
                Console.WriteLine("\tStopped OTel enrichment, because this run started it.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\tCould not stop OTel enrichment: {ex.Message}");
            }
        }
        else
        {
            Console.WriteLine(
                "\tLeft OTel enrichment running, because it was already on before this run.");
        }

        Console.WriteLine(new string('-', 80));
    }
}
// snippet-end:[CloudWatch.dotnetv4.GettingStarted]
