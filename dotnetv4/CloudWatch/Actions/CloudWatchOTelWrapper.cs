// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudWatch;
using Amazon.CloudWatch.Model;
using Microsoft.Extensions.Logging;

namespace CloudWatchActions;

// snippet-start:[CloudWatch.dotnetv4.CloudWatchOTelWrapper]
/// <summary>
/// Wrapper class for the OpenTelemetry features of Amazon CloudWatch: turning on OTel
/// enrichment so that CloudWatch vended metrics are queryable with PromQL, alarming on a
/// PromQL query, inspecting the individual series (contributors) that put a PromQL alarm
/// into ALARM, and muting alarm actions on a schedule.
///
/// Note that OTLP metric ingestion is not an AWS SDK operation. To send OpenTelemetry
/// metrics to CloudWatch, point an OpenTelemetry collector or the AWS Distro for
/// OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
/// https://monitoring.{region}.amazonaws.com/v1/metrics. The operations here cover
/// everything you do after those metrics land in CloudWatch.
/// </summary>
public class CloudWatchOTelWrapper
{
    private readonly IAmazonCloudWatch _amazonCloudWatch;
    private readonly ILogger<CloudWatchOTelWrapper> _logger;

    /// <summary>
    /// Constructor for the CloudWatch OpenTelemetry wrapper.
    /// </summary>
    /// <param name="amazonCloudWatch">The injected CloudWatch client.</param>
    /// <param name="logger">The injected logger for the wrapper.</param>
    public CloudWatchOTelWrapper(IAmazonCloudWatch amazonCloudWatch, ILogger<CloudWatchOTelWrapper> logger)
    {
        _logger = logger;
        _amazonCloudWatch = amazonCloudWatch;
    }

    // snippet-end:[CloudWatch.dotnetv4.CloudWatchOTelWrapper]

    // snippet-start:[CloudWatch.dotnetv4.StartOTelEnrichment]
    /// <summary>
    /// Turn on OTel enrichment for the account. Once enrichment is running, CloudWatch
    /// vended metrics that carry a resource identifier dimension, such as the Amazon EC2
    /// CPUUtilization metric with its InstanceId dimension, are decorated with resource
    /// ARN and resource tag labels and become queryable with PromQL.
    ///
    /// Resource tags on telemetry must already be enabled for the account before you
    /// call this operation.
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> StartOTelEnrichment()
    {
        var response = await _amazonCloudWatch.StartOTelEnrichmentAsync(
            new StartOTelEnrichmentRequest());

        _logger.LogInformation("Started OTel enrichment for this account.");
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
    // snippet-end:[CloudWatch.dotnetv4.StartOTelEnrichment]

    // snippet-start:[CloudWatch.dotnetv4.GetOTelEnrichment]
    /// <summary>
    /// Get the current OTel enrichment status for the account.
    /// </summary>
    /// <returns>The status, either Running or Stopped.</returns>
    public async Task<OTelEnrichmentStatus> GetOTelEnrichmentStatus()
    {
        var response = await _amazonCloudWatch.GetOTelEnrichmentAsync(
            new GetOTelEnrichmentRequest());

        _logger.LogInformation($"OTel enrichment status is {response.Status}.");
        return response.Status;
    }
    // snippet-end:[CloudWatch.dotnetv4.GetOTelEnrichment]

    // snippet-start:[CloudWatch.dotnetv4.StopOTelEnrichment]
    /// <summary>
    /// Turn off OTel enrichment for the account. Existing PromQL alarms are not deleted,
    /// but vended metrics stop being enriched with resource ARN and tag labels, so
    /// queries that select on those labels stop matching.
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> StopOTelEnrichment()
    {
        var response = await _amazonCloudWatch.StopOTelEnrichmentAsync(
            new StopOTelEnrichmentRequest());

        _logger.LogInformation("Stopped OTel enrichment for this account.");
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
    // snippet-end:[CloudWatch.dotnetv4.StopOTelEnrichment]

    // snippet-start:[CloudWatch.dotnetv4.PutMetricAlarm.PromQL]
    /// <summary>
    /// Create an alarm that evaluates a PromQL query.
    ///
    /// A PromQL alarm differs from a classic metric alarm in a few ways. The query can
    /// match many series at once, and each matching series is tracked separately as a
    /// contributor. Instead of counting breaching periods, you specify durations: a
    /// contributor moves to ALARM after it breaches continuously for the pending period,
    /// and back to OK after it stops breaching for the recovery period. A PromQL alarm
    /// starts in the OK state rather than INSUFFICIENT_DATA.
    ///
    /// EvaluationCriteria is a union and is mutually exclusive with the classic
    /// MetricName and Metrics properties. When you use it you must also set
    /// EvaluationInterval, and you must not set Period, Statistic, Threshold,
    /// ComparisonOperator, EvaluationPeriods, DatapointsToAlarm, or TreatMissingData.
    /// </summary>
    /// <param name="alarmName">The name of the alarm, unique within the Region.</param>
    /// <param name="query">The PromQL query to evaluate, such as
    /// avg(cpu_utilization_percent) &gt; 80. The comparison belongs in the query itself;
    /// there is no separate threshold property.</param>
    /// <param name="evaluationInterval">How often, in seconds, to run the query. Valid
    /// values are 10, 20, 30, and any multiple of 60, up to 3600.</param>
    /// <param name="pendingPeriod">How long, in seconds, a contributor must breach
    /// continuously before it moves to ALARM.</param>
    /// <param name="recoveryPeriod">How long, in seconds, a contributor must stop
    /// breaching before it moves back to OK.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> PutPromQLMetricAlarm(string alarmName, string query,
        int evaluationInterval = 60, int pendingPeriod = 300, int recoveryPeriod = 120)
    {
        var response = await _amazonCloudWatch.PutMetricAlarmAsync(
            new PutMetricAlarmRequest
            {
                AlarmName = alarmName,
                AlarmDescription = "A PromQL alarm created by the AWS SDK for .NET example.",
                EvaluationCriteria = new EvaluationCriteria
                {
                    PromQLCriteria = new AlarmPromQLCriteria
                    {
                        Query = query,
                        PendingPeriod = pendingPeriod,
                        RecoveryPeriod = recoveryPeriod
                    }
                },
                EvaluationInterval = evaluationInterval
            });

        _logger.LogInformation($"Created PromQL alarm {alarmName} for query {query}.");
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
    // snippet-end:[CloudWatch.dotnetv4.PutMetricAlarm.PromQL]

    // snippet-start:[CloudWatch.dotnetv4.DescribeAlarmContributors]
    /// <summary>
    /// Get the contributors for a PromQL alarm. Each contributor is one series that the
    /// alarm's query matched, identified by its label set. This is how you find out which
    /// hosts, services, or pods are breaching, rather than only that something is.
    /// </summary>
    /// <param name="alarmName">The name of the PromQL alarm.</param>
    /// <returns>The list of contributors.</returns>
    public async Task<List<AlarmContributor>> DescribeAlarmContributors(string alarmName)
    {
        var results = new List<AlarmContributor>();
        string? nextToken = null;

        do
        {
            var response = await _amazonCloudWatch.DescribeAlarmContributorsAsync(
                new DescribeAlarmContributorsRequest
                {
                    AlarmName = alarmName,
                    NextToken = nextToken
                });

            if (response.AlarmContributors != null)
            {
                results.AddRange(response.AlarmContributors);
            }

            nextToken = response.NextToken;
        } while (!string.IsNullOrEmpty(nextToken));

        _logger.LogInformation($"Got {results.Count} contributors for alarm {alarmName}.");
        return results;
    }
    // snippet-end:[CloudWatch.dotnetv4.DescribeAlarmContributors]

    // snippet-start:[CloudWatch.dotnetv4.PutAlarmMuteRule]
    /// <summary>
    /// Create or update an alarm mute rule. While a mute rule is active the targeted
    /// alarms keep evaluating and keep transitioning between states, but their configured
    /// actions do not fire. This is the supported way to suppress notifications during a
    /// known maintenance window, instead of disabling alarm actions and relying on
    /// someone to turn them back on.
    /// </summary>
    /// <param name="name">The name of the mute rule.</param>
    /// <param name="expression">When the rule activates. Use a cron expression for a
    /// recurring window, such as cron(0 2 ? * SUN *), or an at expression for a one-time
    /// window, such as at(2026-09-05T02:00:00).</param>
    /// <param name="duration">How long the mute window lasts once it activates, such as
    /// 2h or 30m.</param>
    /// <param name="timezone">The time zone the expression is evaluated in, such as
    /// America/Los_Angeles.</param>
    /// <param name="alarmNames">The names of up to 100 alarms to mute. If null or empty,
    /// the rule applies to all alarms in the account.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> PutAlarmMuteRule(string name, string expression, string duration,
        string timezone, List<string>? alarmNames = null)
    {
        var request = new PutAlarmMuteRuleRequest
        {
            Name = name,
            Description = "A mute rule created by the AWS SDK for .NET example.",
            Rule = new Rule
            {
                Schedule = new Schedule
                {
                    Expression = expression,
                    Duration = duration,
                    Timezone = timezone
                }
            }
        };

        if (alarmNames != null && alarmNames.Any())
        {
            request.MuteTargets = new MuteTargets { AlarmNames = alarmNames };
        }

        var response = await _amazonCloudWatch.PutAlarmMuteRuleAsync(request);

        _logger.LogInformation($"Put alarm mute rule {name}.");
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
    // snippet-end:[CloudWatch.dotnetv4.PutAlarmMuteRule]

    // snippet-start:[CloudWatch.dotnetv4.GetAlarmMuteRule]
    /// <summary>
    /// Get the full configuration of an alarm mute rule, including its schedule, the
    /// alarms it targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.
    /// </summary>
    /// <param name="name">The name of the mute rule.</param>
    /// <returns>The mute rule.</returns>
    public async Task<GetAlarmMuteRuleResponse> GetAlarmMuteRule(string name)
    {
        var response = await _amazonCloudWatch.GetAlarmMuteRuleAsync(
            new GetAlarmMuteRuleRequest
            {
                AlarmMuteRuleName = name
            });

        _logger.LogInformation($"Mute rule {response.Name} is {response.Status}.");
        return response;
    }
    // snippet-end:[CloudWatch.dotnetv4.GetAlarmMuteRule]

    // snippet-start:[CloudWatch.dotnetv4.ListAlarmMuteRules]
    /// <summary>
    /// List the alarm mute rules in the account, optionally filtered to the rules that
    /// target one alarm.
    /// </summary>
    /// <param name="alarmName">When specified, only rules that target this alarm are
    /// returned.</param>
    /// <returns>The list of mute rule summaries.</returns>
    public async Task<List<AlarmMuteRuleSummary>> ListAlarmMuteRules(string? alarmName = null)
    {
        var results = new List<AlarmMuteRuleSummary>();
        string? nextToken = null;

        do
        {
            var response = await _amazonCloudWatch.ListAlarmMuteRulesAsync(
                new ListAlarmMuteRulesRequest
                {
                    AlarmName = alarmName,
                    NextToken = nextToken
                });

            if (response.AlarmMuteRuleSummaries != null)
            {
                results.AddRange(response.AlarmMuteRuleSummaries);
            }

            nextToken = response.NextToken;
        } while (!string.IsNullOrEmpty(nextToken));

        _logger.LogInformation($"Got {results.Count} alarm mute rules.");
        return results;
    }
    // snippet-end:[CloudWatch.dotnetv4.ListAlarmMuteRules]

    // snippet-start:[CloudWatch.dotnetv4.DeleteAlarmMuteRule]
    /// <summary>
    /// Delete an alarm mute rule. The alarms it targeted resume firing their actions.
    /// </summary>
    /// <param name="name">The name of the mute rule.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteAlarmMuteRule(string name)
    {
        var response = await _amazonCloudWatch.DeleteAlarmMuteRuleAsync(
            new DeleteAlarmMuteRuleRequest
            {
                AlarmMuteRuleName = name
            });

        _logger.LogInformation($"Deleted alarm mute rule {name}.");
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
    // snippet-end:[CloudWatch.dotnetv4.DeleteAlarmMuteRule]
}