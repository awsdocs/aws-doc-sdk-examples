// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: cloudwatch_getting_started_scenario.cpp demonstrates the Amazon CloudWatch
 * OpenTelemetry experience end to end.
 *
 * The scenario:
 * 1. Lists metrics and namespaces to show what CloudWatch is already collecting.
 * 2. Starts OTel enrichment so CloudWatch can attach AWS resource context to incoming
 *    OTLP metrics.
 * 3. Explains how OTLP metrics reach CloudWatch. This is not an AWS SDK operation.
 * 4. Creates an alarm that evaluates a PromQL query.
 * 5. Inspects the alarm's contributors, which is how you find out which series is
 *    unhealthy rather than only that something is.
 * 6. Gets statistics for a metric and charts it on a dashboard.
 * 7. Mutes the alarm for a maintenance window.
 * 8. Deletes everything the scenario created.
 *
 * Prerequisites:
 * An AWS account with credentials that permit the CloudWatch operations above.
 *
 * Inputs:
 * - query: An optional PromQL query, entered as the first argument in the command line.
 *
 * Output:
 * A running commentary on each step. All resources are deleted at the end.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.get_started_scenario.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/json/JsonSerializer.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/AlarmPromQLCriteria.h>
#include <aws/monitoring/model/DeleteAlarmMuteRuleRequest.h>
#include <aws/monitoring/model/DeleteAlarmsRequest.h>
#include <aws/monitoring/model/DeleteDashboardsRequest.h>
#include <aws/monitoring/model/DescribeAlarmContributorsRequest.h>
#include <aws/monitoring/model/EvaluationCriteria.h>
#include <aws/monitoring/model/GetAlarmMuteRuleRequest.h>
#include <aws/monitoring/model/GetDashboardRequest.h>
#include <aws/monitoring/model/GetMetricStatisticsRequest.h>
#include <aws/monitoring/model/GetOTelEnrichmentRequest.h>
#include <aws/monitoring/model/ListAlarmMuteRulesRequest.h>
#include <aws/monitoring/model/ListMetricsRequest.h>
#include <aws/monitoring/model/MuteTargets.h>
#include <aws/monitoring/model/PutAlarmMuteRuleRequest.h>
#include <aws/monitoring/model/PutDashboardRequest.h>
#include <aws/monitoring/model/PutMetricAlarmRequest.h>
#include <aws/monitoring/model/Rule.h>
#include <aws/monitoring/model/Schedule.h>
#include <aws/monitoring/model/StartOTelEnrichmentRequest.h>
#include <aws/monitoring/model/StopOTelEnrichmentRequest.h>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <map>
// snippet-end:[cw.cpp.get_started_scenario.inc]

// snippet-start:[cw.cpp.get_started_scenario.code]
namespace {
    const Aws::String DASHES(80, '-');
    const char DEFAULT_QUERY[] = "avg by (host) (system_cpu_utilization) > 80";

    // Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600
    // seconds.
    const int EVALUATION_INTERVAL = 60;
    const int PENDING_PERIOD = 300;
    const int RECOVERY_PERIOD = 120;

    //! Wait for the reader before moving to the next step.
    void pressEnter() {
        std::cout << "Press Enter to continue..." << std::endl;
        std::cin.get();
    }

    //! List the account's metrics and report how they are spread across namespaces.
    /*!
      \param client: A CloudWatch client.
      \param metric: Receives the first metric found, for later steps to chart.
      \return bool: Function succeeded.
     */
    bool listMetricsAndNamespaces(const Aws::CloudWatch::CloudWatchClient &client,
                                  Aws::CloudWatch::Model::Metric &metric) {
        std::cout << "1. List metrics and namespaces" << std::endl << std::endl;
        std::cout << "Before configuring anything, let's see what CloudWatch is already"
                  << std::endl
                  << "collecting in this account." << std::endl << std::endl;

        std::map<Aws::String, int> counts;
        int metricCount = 0;
        bool haveMetric = false;

        Aws::CloudWatch::Model::ListMetricsRequest request;
        bool done = false;
        while (!done) {
            auto outcome = client.ListMetrics(request);
            if (!outcome.IsSuccess()) {
                std::cerr << "Failed to list metrics: " << outcome.GetError().GetMessage()
                          << std::endl;
                return false;
            }

            for (const auto &found : outcome.GetResult().GetMetrics()) {
                ++counts[found.GetNamespace()];
                ++metricCount;
                if (!haveMetric) {
                    metric = found;
                    haveMetric = true;
                }
            }

            const auto &nextToken = outcome.GetResult().GetNextToken();
            request.SetNextToken(nextToken);
            // This account may have a very large number of metrics, so stop once there
            // are enough to give the reader a sense of what is there.
            done = nextToken.empty() || metricCount >= 500;
        }

        std::cout << "Found " << metricCount << " metrics across " << counts.size()
                  << " namespaces:" << std::endl;
        for (const auto &entry : counts) {
            std::cout << "  " << entry.first << " (" << entry.second << " metrics)"
                      << std::endl;
        }
        if (!haveMetric) {
            std::cout << "No metrics found in this account. The statistics and dashboard"
                      << std::endl
                      << "steps later on need an existing metric, so they will be "
                         "skipped."
                      << std::endl;
        }

        return true;
    }

    //! Start OTel enrichment, but only if it is not already running.
    /*!
      \param client: A CloudWatch client.
      \param startedEnrichment: Set to true if this run started enrichment, so that
             cleanup only stops what this run turned on.
      \return bool: Function succeeded.
     */
    bool startOTelEnrichment(const Aws::CloudWatch::CloudWatchClient &client,
                             bool &startedEnrichment) {
        std::cout << "2. Start OpenTelemetry enrichment" << std::endl << std::endl;
        std::cout << "Enrichment is what lets CloudWatch attach AWS resource context to"
                  << std::endl
                  << "the OTLP metrics you send it. Without it, your metrics arrive as"
                  << std::endl
                  << "opaque series with no connection to the resources that emitted "
                     "them."
                  << std::endl << std::endl;

        Aws::CloudWatch::Model::GetOTelEnrichmentRequest getRequest;
        auto getOutcome = client.GetOTelEnrichment(getRequest);
        if (!getOutcome.IsSuccess()) {
            std::cerr << "Failed to get OTel enrichment status: "
                      << getOutcome.GetError().GetMessage() << std::endl;
            return false;
        }

        auto status = getOutcome.GetResult().GetStatus();
        std::cout << "Enrichment status: "
                  << Aws::CloudWatch::Model::OTelEnrichmentStatusMapper::
                         GetNameForOTelEnrichmentStatus(status)
                  << std::endl;

        if (status == Aws::CloudWatch::Model::OTelEnrichmentStatus::Running) {
            std::cout << std::endl
                      << "Enrichment was already running, so it will be left alone. The"
                      << std::endl
                      << "cleanup step will not stop it, because other workloads in this"
                      << std::endl
                      << "account may depend on it." << std::endl;
            return true;
        }

        Aws::CloudWatch::Model::StartOTelEnrichmentRequest startRequest;
        auto startOutcome = client.StartOTelEnrichment(startRequest);
        if (!startOutcome.IsSuccess()) {
            std::cerr << "Failed to start OTel enrichment: "
                      << startOutcome.GetError().GetMessage() << std::endl;
            return false;
        }
        startedEnrichment = true;

        auto afterOutcome = client.GetOTelEnrichment(getRequest);
        if (afterOutcome.IsSuccess()) {
            std::cout << "Enrichment status: "
                      << Aws::CloudWatch::Model::OTelEnrichmentStatusMapper::
                             GetNameForOTelEnrichmentStatus(
                                 afterOutcome.GetResult().GetStatus())
                      << std::endl;
        }
        std::cout << std::endl
                  << "Note: this run started enrichment, so the cleanup step will stop it"
                  << std::endl
                  << "again." << std::endl;

        return true;
    }

    //! Explain how OTLP metrics reach CloudWatch. There is no SDK call for this step.
    void explainOtlpIngestion() {
        std::cout << "3. Send OTLP metrics to CloudWatch" << std::endl << std::endl;
        std::cout << "This step is not an AWS SDK operation, and that is worth being"
                  << std::endl
                  << "explicit about. Metrics reach CloudWatch over the OTLP protocol,"
                  << std::endl
                  << "through the CloudWatch agent, an OpenTelemetry Collector, or an "
                     "ADOT"
                  << std::endl
                  << "SDK. There is no PutOTelMetrics API to call." << std::endl
                  << std::endl;
        std::cout << "Point your collector at the CloudWatch metrics endpoint, which"
                  << std::endl
                  << "follows the pattern" << std::endl
                  << "  https://monitoring.<region>.amazonaws.com/v1/metrics" << std::endl
                  << std::endl;
        std::cout << "The endpoint is HTTP/1.1 only and does not support gRPC, so use an"
                  << std::endl
                  << "otlphttp exporter rather than otlp. The metrics endpoint signs as"
                  << std::endl
                  << "\"monitoring\"." << std::endl;
    }

    //! Create an alarm that evaluates a PromQL query.
    /*!
      \param client: A CloudWatch client.
      \param alarmName: The name of the alarm to create.
      \param query: The PromQL query to evaluate.
      \return bool: Function succeeded.
     */
    bool createPromQLAlarm(const Aws::CloudWatch::CloudWatchClient &client,
                           const Aws::String &alarmName, const Aws::String &query) {
        std::cout << "4. Create a PromQL alarm" << std::endl << std::endl;
        std::cout << "The comparison goes inside the query itself: a PromQL alarm has no"
                  << std::endl
                  << "separate threshold, comparison operator, statistic, or period."
                  << std::endl << std::endl;

        Aws::CloudWatch::Model::AlarmPromQLCriteria promQLCriteria;
        promQLCriteria.SetQuery(query);
        promQLCriteria.SetPendingPeriod(PENDING_PERIOD);
        promQLCriteria.SetRecoveryPeriod(RECOVERY_PERIOD);

        Aws::CloudWatch::Model::EvaluationCriteria evaluationCriteria;
        evaluationCriteria.SetPromQLCriteria(promQLCriteria);

        // EvaluationCriteria is mutually exclusive with the classic MetricName and
        // Metrics fields. When you use it you must also set EvaluationInterval, and you
        // must not set Period, Statistic, Threshold, ComparisonOperator,
        // EvaluationPeriods, DatapointsToAlarm, or TreatMissingData.
        Aws::CloudWatch::Model::PutMetricAlarmRequest request;
        request.SetAlarmName(alarmName);
        request.SetAlarmDescription(
            "A PromQL alarm created by the AWS SDK for C++ Basics scenario.");
        request.SetEvaluationCriteria(evaluationCriteria);
        request.SetEvaluationInterval(EVALUATION_INTERVAL);
        request.SetActionsEnabled(false);

        auto outcome = client.PutMetricAlarm(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to create PromQL alarm: "
                      << outcome.GetError().GetMessage() << std::endl;
            return false;
        }

        std::cout << "Created alarm " << alarmName << ":" << std::endl;
        std::cout << "  query:              " << query << std::endl;
        std::cout << "  evaluationInterval: " << EVALUATION_INTERVAL << " seconds"
                  << std::endl;
        std::cout << "  pendingPeriod:      " << PENDING_PERIOD << " seconds" << std::endl;
        std::cout << "  recoveryPeriod:     " << RECOVERY_PERIOD << " seconds"
                  << std::endl << std::endl;
        std::cout << "A PromQL alarm starts in the OK state rather than "
                     "INSUFFICIENT_DATA,"
                  << std::endl
                  << "which is another way it differs from a classic alarm." << std::endl;

        return true;
    }

    //! Report the alarm's contributors, one per series the query matched.
    /*!
      \param client: A CloudWatch client.
      \param alarmName: The name of the alarm.
      \return bool: Function succeeded.
     */
    bool inspectAlarmContributors(const Aws::CloudWatch::CloudWatchClient &client,
                                 const Aws::String &alarmName) {
        std::cout << "5. Inspect the alarm's contributors" << std::endl << std::endl;
        std::cout << "Each contributor is one series the query matched, identified by its"
                  << std::endl
                  << "label set. This is how you find out which host is unhealthy rather"
                  << std::endl
                  << "than only that something is. Classic alarms have no equivalent."
                  << std::endl << std::endl;

        Aws::Vector<Aws::CloudWatch::Model::AlarmContributor> contributors;
        Aws::CloudWatch::Model::DescribeAlarmContributorsRequest request;
        request.SetAlarmName(alarmName);

        bool done = false;
        while (!done) {
            auto outcome = client.DescribeAlarmContributors(request);
            if (!outcome.IsSuccess()) {
                std::cerr << "Failed to describe alarm contributors: "
                          << outcome.GetError().GetMessage() << std::endl;
                return false;
            }

            const auto &page = outcome.GetResult().GetAlarmContributors();
            contributors.insert(contributors.end(), page.begin(), page.end());

            const auto &nextToken = outcome.GetResult().GetNextToken();
            request.SetNextToken(nextToken);
            // A page can come back empty while still carrying a token, so keep going
            // until the token itself is gone rather than stopping at the first empty
            // page.
            done = nextToken.empty();
        }

        if (contributors.empty()) {
            std::cout << "No contributors yet. The query matched no series, which usually"
                      << std::endl
                      << "means no OTel metrics with these labels have arrived. Once your"
                      << std::endl
                      << "collector is sending data, each matching series appears here"
                      << std::endl
                      << "with its labels and the reason it breached." << std::endl;
            return true;
        }

        std::cout << "Found " << contributors.size() << " contributors:" << std::endl;
        for (const auto &contributor : contributors) {
            std::cout << "  " << contributor.GetContributorId() << ": ";
            bool first = true;
            for (const auto &label : contributor.GetContributorAttributes()) {
                if (!first) {
                    std::cout << ", ";
                }
                std::cout << label.first << "=" << label.second;
                first = false;
            }
            std::cout << std::endl;
            std::cout << "    reason: " << contributor.GetStateReason() << std::endl;
        }

        return true;
    }

    //! Build a single-widget dashboard body that charts the given metric.
    /*!
      \param metric: The metric to chart.
      \param region: The region the metric is in. A metric widget must name its
       region, because a dashboard can chart metrics from several.
      \return Aws::String: The dashboard body, as JSON.
     */
    Aws::String buildDashboardBody(const Aws::CloudWatch::Model::Metric &metric,
                                   const Aws::String &region) {
        const auto &dimensions = metric.GetDimensions();

        // A metric is specified in a widget as a flat array,
        // [namespace, metricName, dimensionName, dimensionValue, ...].
        Aws::Utils::Array<Aws::Utils::Json::JsonValue> metricSpec(
            2 + 2 * dimensions.size());
        metricSpec[0].AsString(metric.GetNamespace());
        metricSpec[1].AsString(metric.GetMetricName());
        size_t index = 2;
        for (const auto &dimension : dimensions) {
            metricSpec[index++].AsString(dimension.GetName());
            metricSpec[index++].AsString(dimension.GetValue());
        }

        Aws::Utils::Array<Aws::Utils::Json::JsonValue> metricsArray(1);
        metricsArray[0].AsArray(metricSpec);

        Aws::Utils::Json::JsonValue textProperties;
        textProperties.WithString(
            "markdown",
            "This dashboard was created programmatically by an AWS SDK code example.");

        Aws::Utils::Json::JsonValue textWidget;
        textWidget.WithString("type", "text")
            .WithInteger("x", 0)
            .WithInteger("y", 0)
            .WithInteger("width", 24)
            .WithInteger("height", 2)
            .WithObject("properties", textProperties);

        Aws::Utils::Json::JsonValue metricProperties;
        metricProperties.WithArray("metrics", metricsArray)
            .WithString("view", "timeSeries")
            .WithString("stat", "Average")
            .WithInteger("period", 300)
            .WithString("region", region)
            .WithString("title", metric.GetMetricName());

        Aws::Utils::Json::JsonValue metricWidget;
        metricWidget.WithString("type", "metric")
            .WithInteger("x", 0)
            .WithInteger("y", 2)
            .WithInteger("width", 12)
            .WithInteger("height", 6)
            .WithObject("properties", metricProperties);

        Aws::Utils::Array<Aws::Utils::Json::JsonValue> widgets(2);
        widgets[0] = textWidget;
        widgets[1] = metricWidget;

        Aws::Utils::Json::JsonValue body;
        body.WithArray("widgets", widgets);

        return body.View().WriteCompact();
    }

    //! Get statistics for a metric and chart it on a dashboard.
    /*!
      \param client: A CloudWatch client.
      \param metric: The metric to chart. If its name is empty, this step is skipped.
      \param dashboardName: The name of the dashboard to create.
      \param dashboardCreated: Set to true if a dashboard was created, so that cleanup
             only deletes a dashboard that exists.
      \return bool: Function succeeded.
     */
    bool getStatisticsAndChartMetric(const Aws::CloudWatch::CloudWatchClient &client,
                                     const Aws::CloudWatch::Model::Metric &metric,
                                     const Aws::String &dashboardName,
                                     const Aws::String &region,
                                     bool &dashboardCreated) {
        std::cout << "6. Get statistics and chart the metric on a dashboard" << std::endl
                  << std::endl;
        std::cout << "Statistics and dashboards are how you see what the alarm is"
                  << std::endl << "evaluating." << std::endl << std::endl;

        if (metric.GetMetricName().empty()) {
            std::cout << "Skipping statistics and dashboard because no metrics exist yet."
                      << std::endl;
            return true;
        }

        const auto now = std::chrono::system_clock::now();
        Aws::CloudWatch::Model::GetMetricStatisticsRequest statsRequest;
        statsRequest.SetNamespace(metric.GetNamespace());
        statsRequest.SetMetricName(metric.GetMetricName());
        statsRequest.SetDimensions(metric.GetDimensions());
        statsRequest.SetStartTime(Aws::Utils::DateTime(now - std::chrono::hours(24)));
        statsRequest.SetEndTime(Aws::Utils::DateTime(now));
        statsRequest.SetPeriod(3600);
        statsRequest.AddStatistics(Aws::CloudWatch::Model::Statistic::Average);
        statsRequest.AddStatistics(Aws::CloudWatch::Model::Statistic::Maximum);

        auto statsOutcome = client.GetMetricStatistics(statsRequest);
        if (!statsOutcome.IsSuccess()) {
            std::cerr << "Failed to get metric statistics: "
                      << statsOutcome.GetError().GetMessage() << std::endl;
            return false;
        }

        const auto &datapoints = statsOutcome.GetResult().GetDatapoints();
        std::cout << "Statistics for " << metric.GetNamespace() << " "
                  << metric.GetMetricName() << " over the last day:" << std::endl;
        std::cout << "  Datapoints: " << datapoints.size() << std::endl;
        size_t shown = 0;
        for (const auto &datapoint : datapoints) {
            if (shown++ >= 3) {
                break;
            }
            std::cout << "  " << datapoint.GetTimestamp().ToGmtString(
                                     Aws::Utils::DateFormat::ISO_8601)
                      << " average " << datapoint.GetAverage() << ", maximum "
                      << datapoint.GetMaximum() << std::endl;
        }

        Aws::CloudWatch::Model::PutDashboardRequest putRequest;
        putRequest.SetDashboardName(dashboardName);
        putRequest.SetDashboardBody(buildDashboardBody(metric, region));

        auto putOutcome = client.PutDashboard(putRequest);
        if (!putOutcome.IsSuccess()) {
            std::cerr << "Failed to put dashboard: " << putOutcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        dashboardCreated = true;

        for (const auto &message :
             putOutcome.GetResult().GetDashboardValidationMessages()) {
            std::cout << "Dashboard validation message: " << message.GetMessage()
                      << std::endl;
        }
        std::cout << "Created dashboard " << dashboardName << "." << std::endl;

        Aws::CloudWatch::Model::GetDashboardRequest getRequest;
        getRequest.SetDashboardName(dashboardName);
        auto getOutcome = client.GetDashboard(getRequest);
        if (getOutcome.IsSuccess()) {
            std::cout << "Read the dashboard back, "
                      << getOutcome.GetResult().GetDashboardBody().size()
                      << " characters of widget JSON." << std::endl;
        }

        return true;
    }

    //! Mute the alarm for a recurring maintenance window.
    /*!
      \param client: A CloudWatch client.
      \param muteRuleName: The name of the mute rule to create.
      \param alarmName: The alarm to target.
      \return bool: Function succeeded.
     */
    bool muteAlarmForMaintenance(const Aws::CloudWatch::CloudWatchClient &client,
                                 const Aws::String &muteRuleName,
                                 const Aws::String &alarmName) {
        std::cout << "7. Mute the alarm for a maintenance window" << std::endl
                  << std::endl;
        std::cout << "While a mute rule is active the targeted alarms keep evaluating and"
                  << std::endl
                  << "keep changing state, but their actions do not fire. This is the"
                  << std::endl
                  << "supported way to suppress notifications during planned maintenance,"
                  << std::endl
                  << "instead of disabling alarm actions and hoping someone remembers to"
                  << std::endl
                  << "turn them back on." << std::endl << std::endl;

        // The expression is a five-field cron expression,
        // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five
        // fields, not the six that Amazon EventBridge uses. For a one-time window, use
        // at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration
        // from PT1M to P15D, so PT2H rather than 2h.
        const Aws::String expression("cron(0 2 * * SUN)");
        const Aws::String duration("PT2H");
        const Aws::String timezone("America/Los_Angeles");

        Aws::CloudWatch::Model::Schedule schedule;
        schedule.SetExpression(expression);
        schedule.SetDuration(duration);
        schedule.SetTimezone(timezone);

        Aws::CloudWatch::Model::Rule rule;
        rule.SetSchedule(schedule);

        // Target up to 100 alarms. If MuteTargets is not set, the rule applies to every
        // alarm in the account.
        Aws::CloudWatch::Model::MuteTargets muteTargets;
        muteTargets.AddAlarmNames(alarmName);

        Aws::CloudWatch::Model::PutAlarmMuteRuleRequest putRequest;
        putRequest.SetName(muteRuleName);
        putRequest.SetDescription(
            "A mute rule created by the AWS SDK for C++ Basics scenario.");
        putRequest.SetRule(rule);
        putRequest.SetMuteTargets(muteTargets);

        auto putOutcome = client.PutAlarmMuteRule(putRequest);
        if (!putOutcome.IsSuccess()) {
            std::cerr << "Failed to put alarm mute rule: "
                      << putOutcome.GetError().GetMessage() << std::endl;
            return false;
        }

        std::cout << "Created mute rule " << muteRuleName << ":" << std::endl;
        std::cout << "  schedule: " << expression << " for " << duration << std::endl;
        std::cout << "  timezone: " << timezone << std::endl;
        std::cout << "  targets:  " << alarmName << std::endl << std::endl;
        std::cout << "Note the two formats here. The expression is a five-field cron"
                  << std::endl
                  << "expression, five rather than the six Amazon EventBridge uses. The"
                  << std::endl
                  << "duration is an ISO 8601 duration, so \"PT2H\" and not \"2h\"."
                  << std::endl << std::endl;
        std::cout << "Also note that MuteTargets is set explicitly. If you leave it out,"
                  << std::endl
                  << "the rule applies to every alarm in the account." << std::endl;

        Aws::CloudWatch::Model::GetAlarmMuteRuleRequest getRequest;
        getRequest.SetAlarmMuteRuleName(muteRuleName);
        auto getOutcome = client.GetAlarmMuteRule(getRequest);
        if (getOutcome.IsSuccess()) {
            const auto &result = getOutcome.GetResult();
            std::cout << "Read the rule back: status "
                      << Aws::CloudWatch::Model::AlarmMuteRuleStatusMapper::
                             GetNameForAlarmMuteRuleStatus(result.GetStatus())
                      << ", mute type " << result.GetMuteType() << "." << std::endl;
        }

        Aws::Vector<Aws::CloudWatch::Model::AlarmMuteRuleSummary> summaries;
        Aws::CloudWatch::Model::ListAlarmMuteRulesRequest listRequest;
        listRequest.SetAlarmName(alarmName);
        bool done = false;
        while (!done) {
            auto listOutcome = client.ListAlarmMuteRules(listRequest);
            if (!listOutcome.IsSuccess()) {
                std::cerr << "Failed to list alarm mute rules: "
                          << listOutcome.GetError().GetMessage() << std::endl;
                return false;
            }

            const auto &page = listOutcome.GetResult().GetAlarmMuteRuleSummaries();
            summaries.insert(summaries.end(), page.begin(), page.end());

            const auto &nextToken = listOutcome.GetResult().GetNextToken();
            listRequest.SetNextToken(nextToken);
            done = nextToken.empty();
        }

        std::cout << "Found " << summaries.size()
                  << " mute rules targeting this alarm." << std::endl;
        // Mute rule summaries carry no name field, only an ARN, so match on the ARN
        // suffix.
        for (const auto &summary : summaries) {
            const auto &arn = summary.GetAlarmMuteRuleArn();
            const Aws::String slashSuffix = "/" + muteRuleName;
            const Aws::String colonSuffix = ":" + muteRuleName;
            if ((arn.size() >= slashSuffix.size() &&
                 arn.compare(arn.size() - slashSuffix.size(), slashSuffix.size(),
                             slashSuffix) == 0) ||
                (arn.size() >= colonSuffix.size() &&
                 arn.compare(arn.size() - colonSuffix.size(), colonSuffix.size(),
                             colonSuffix) == 0)) {
                std::cout << "  matched by ARN: " << arn << " ("
                          << Aws::CloudWatch::Model::AlarmMuteRuleStatusMapper::
                                 GetNameForAlarmMuteRuleStatus(summary.GetStatus())
                          << ")" << std::endl;
            }
        }

        return true;
    }

    //! Delete everything the scenario created.
    /*!
      \param client: A CloudWatch client.
      \param alarmName: The alarm to delete.
      \param dashboardName: The dashboard to delete.
      \param muteRuleName: The mute rule to delete.
      \param dashboardCreated: Whether a dashboard was created.
      \param startedEnrichment: Whether this run started OTel enrichment.
      \return bool: Every deletion succeeded.
     */
    bool cleanUp(const Aws::CloudWatch::CloudWatchClient &client,
                 const Aws::String &alarmName, const Aws::String &dashboardName,
                 const Aws::String &muteRuleName, bool dashboardCreated,
                 bool startedEnrichment) {
        std::cout << "8. Clean up" << std::endl << std::endl;

        // Each deletion is attempted independently so that one failure does not leave
        // the remaining resources behind.
        bool result = true;

        Aws::CloudWatch::Model::DeleteAlarmMuteRuleRequest muteRuleRequest;
        muteRuleRequest.SetAlarmMuteRuleName(muteRuleName);
        auto muteRuleOutcome = client.DeleteAlarmMuteRule(muteRuleRequest);
        if (muteRuleOutcome.IsSuccess()) {
            std::cout << "Deleted mute rule " << muteRuleName << "." << std::endl;
        } else {
            std::cerr << "Could not delete the mute rule: "
                      << muteRuleOutcome.GetError().GetMessage() << std::endl;
            result = false;
        }

        Aws::CloudWatch::Model::DeleteAlarmsRequest alarmRequest;
        alarmRequest.AddAlarmNames(alarmName);
        auto alarmOutcome = client.DeleteAlarms(alarmRequest);
        if (alarmOutcome.IsSuccess()) {
            std::cout << "Deleted alarm " << alarmName << "." << std::endl;
        } else {
            std::cerr << "Could not delete the alarm: "
                      << alarmOutcome.GetError().GetMessage() << std::endl;
            result = false;
        }

        if (dashboardCreated) {
            Aws::CloudWatch::Model::DeleteDashboardsRequest dashboardRequest;
            dashboardRequest.AddDashboardNames(dashboardName);
            auto dashboardOutcome = client.DeleteDashboards(dashboardRequest);
            if (dashboardOutcome.IsSuccess()) {
                std::cout << "Deleted dashboard " << dashboardName << "." << std::endl;
            } else {
                std::cerr << "Could not delete the dashboard: "
                          << dashboardOutcome.GetError().GetMessage() << std::endl;
                result = false;
            }
        }

        if (!startedEnrichment) {
            std::cout << "Left OTel enrichment running, because it was already on before"
                      << std::endl << "this run." << std::endl;
            return result;
        }

        Aws::CloudWatch::Model::StopOTelEnrichmentRequest stopRequest;
        auto stopOutcome = client.StopOTelEnrichment(stopRequest);
        if (stopOutcome.IsSuccess()) {
            std::cout << "Stopped OTel enrichment, because this run started it."
                      << std::endl;
        } else {
            std::cerr << "Could not stop OTel enrichment: "
                      << stopOutcome.GetError().GetMessage() << std::endl;
            result = false;
        }

        return result;
    }
} // namespace

//! Run the Amazon CloudWatch Basics scenario.
/*!
  \param query: The PromQL query to alarm on.
  \param clientConfig: AWS client configuration.
  \return bool: Function succeeded.
 */
bool runCloudWatchScenario(const Aws::String &query,
                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudWatch::CloudWatchClient client(clientConfig);

    // Suffix the resource names so repeated runs do not collide.
    std::srand(static_cast<unsigned int>(std::time(nullptr)));
    const int suffix = 1000 + (std::rand() % 9000);
    const Aws::String alarmName =
        "doc-example-promql-alarm-" + std::to_string(suffix);
    const Aws::String dashboardName = "doc-example-dashboard-" + std::to_string(suffix);
    const Aws::String muteRuleName = "doc-example-mute-rule-" + std::to_string(suffix);

    Aws::CloudWatch::Model::Metric metric;
    bool startedEnrichment = false;
    bool dashboardCreated = false;
    bool result = true;

    std::cout << DASHES << std::endl;
    std::cout << "Welcome to the Amazon CloudWatch Basics scenario." << std::endl
              << std::endl;
    std::cout << "CloudWatch now ingests OpenTelemetry metrics natively. This scenario"
              << std::endl
              << "walks through that experience: it turns on OTel enrichment so"
              << std::endl
              << "CloudWatch can correlate incoming OTLP metrics with the resources that"
              << std::endl
              << "produced them, alarms on those metrics with a PromQL query, and shows"
              << std::endl
              << "you which individual series drove the alarm." << std::endl << std::endl;
    std::cout << "A PromQL alarm works differently from a classic metric alarm. Rather"
              << std::endl
              << "than watching one metric and counting breaching periods, it evaluates"
              << std::endl
              << "a query that can match many series at once, and tracks each one"
              << std::endl << "separately as a contributor." << std::endl;
    std::cout << DASHES << std::endl;
    pressEnter();

    if (!listMetricsAndNamespaces(client, metric)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    if (result && !startOTelEnrichment(client, startedEnrichment)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    explainOtlpIngestion();
    std::cout << DASHES << std::endl;
    pressEnter();

    if (result && !createPromQLAlarm(client, alarmName, query)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    if (result && !inspectAlarmContributors(client, alarmName)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    if (result && !getStatisticsAndChartMetric(client, metric, dashboardName,
                                               clientConfig.region,
                                               dashboardCreated)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    if (result && !muteAlarmForMaintenance(client, muteRuleName, alarmName)) {
        result = false;
    }
    std::cout << DASHES << std::endl;
    pressEnter();

    // Clean up regardless of whether an earlier step failed, so a partial run does not
    // leave resources behind.
    if (!cleanUp(client, alarmName, dashboardName, muteRuleName, dashboardCreated,
                 startedEnrichment)) {
        result = false;
    }
    std::cout << DASHES << std::endl;

    std::cout << "This concludes the Amazon CloudWatch Basics scenario." << std::endl;
    std::cout << DASHES << std::endl;

    return result;
}
// snippet-end:[cw.cpp.get_started_scenario.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_cloudwatch_getting_started_scenario [query]'
 *
 */

#ifndef EXCLUDE_MAIN_FUNCTION

int main(int argc, char **argv) {
    Aws::String query(DEFAULT_QUERY);
    if (argc > 1) {
        query = argv[1];
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        runCloudWatchScenario(query, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // EXCLUDE_MAIN_FUNCTION
