// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch.otel;

// snippet-start:[cloudwatch.java2.otel.actions.import]
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.AlarmContributor;
import software.amazon.awssdk.services.cloudwatch.model.AlarmMuteRuleSummary;
import software.amazon.awssdk.services.cloudwatch.model.AlarmPromQLCriteria;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAlarmMuteRuleRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmContributorsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmContributorsResponse;
import software.amazon.awssdk.services.cloudwatch.model.EvaluationCriteria;
import software.amazon.awssdk.services.cloudwatch.model.GetAlarmMuteRuleRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetAlarmMuteRuleResponse;
import software.amazon.awssdk.services.cloudwatch.model.GetOTelEnrichmentRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListAlarmMuteRulesRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListAlarmMuteRulesResponse;
import software.amazon.awssdk.services.cloudwatch.model.MuteTargets;
import software.amazon.awssdk.services.cloudwatch.model.PutAlarmMuteRuleRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.cloudwatch.model.Rule;
import software.amazon.awssdk.services.cloudwatch.model.Schedule;
import software.amazon.awssdk.services.cloudwatch.model.StartOTelEnrichmentRequest;
import software.amazon.awssdk.services.cloudwatch.model.StopOTelEnrichmentRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// snippet-end:[cloudwatch.java2.otel.actions.import]

/**
 * Operations for working with OpenTelemetry metrics in Amazon CloudWatch: turning on
 * OTel enrichment so that CloudWatch vended metrics are queryable with PromQL,
 * alarming on a PromQL query, inspecting the individual series (contributors) that put
 * a PromQL alarm into ALARM, and muting alarm actions on a schedule.
 *
 * <p>Note that OTLP metric ingestion is not an AWS SDK operation. To send
 * OpenTelemetry metrics to CloudWatch you point an OpenTelemetry collector or the AWS
 * Distro for OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
 * {@code https://monitoring.<region>.amazonaws.com/v1/metrics}. The operations here
 * cover everything you do after those metrics land in CloudWatch.
 *
 * <p>Before running this Java V2 code example, set up your development environment,
 * including your credentials. For more information, see the following documentation
 * topic:
 *
 * <p>https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
// snippet-start:[cloudwatch.java2.otel.actions.main]
public class CloudWatchOTelActions {

    // snippet-start:[cloudwatch.java2.StartOTelEnrichment.main]

    /**
     * Turns on OTel enrichment for the account. Once enrichment is running, CloudWatch
     * vended metrics that carry a resource identifier dimension, such as the EC2
     * CPUUtilization metric with its InstanceId dimension, are decorated with resource
     * ARN and resource tag labels and become queryable with PromQL.
     *
     * <p>Resource tags on telemetry must already be enabled for the account before you
     * call this operation.
     *
     * @param cw the CloudWatch client
     */
    public static void startOTelEnrichment(CloudWatchClient cw) {
        try {
            cw.startOTelEnrichment(StartOTelEnrichmentRequest.builder().build());
            System.out.println("Started OTel enrichment for this account.");

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.StartOTelEnrichment.main]

    // snippet-start:[cloudwatch.java2.GetOTelEnrichment.main]

    /**
     * Gets the current OTel enrichment status for the account.
     *
     * @param cw the CloudWatch client
     * @return the status, either {@code Running} or {@code Stopped}
     */
    public static String getOTelEnrichmentStatus(CloudWatchClient cw) {
        try {
            String status = cw.getOTelEnrichment(GetOTelEnrichmentRequest.builder().build())
                    .statusAsString();
            System.out.printf("OTel enrichment status is %s.%n", status);
            return status;

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }
    }
    // snippet-end:[cloudwatch.java2.GetOTelEnrichment.main]

    // snippet-start:[cloudwatch.java2.StopOTelEnrichment.main]

    /**
     * Turns off OTel enrichment for the account. Existing PromQL alarms are not
     * deleted, but vended metrics stop being enriched with resource ARN and tag labels,
     * so queries that select on those labels stop matching.
     *
     * @param cw the CloudWatch client
     */
    public static void stopOTelEnrichment(CloudWatchClient cw) {
        try {
            cw.stopOTelEnrichment(StopOTelEnrichmentRequest.builder().build());
            System.out.println("Stopped OTel enrichment for this account.");

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.StopOTelEnrichment.main]

    // snippet-start:[cloudwatch.java2.PutMetricAlarm.promql.main]

    /**
     * Creates an alarm that evaluates a PromQL query.
     *
     * <p>A PromQL alarm differs from a classic metric alarm in a few ways. The query
     * can match many series at once, and each matching series is tracked separately as
     * a contributor. Instead of counting breaching periods, you specify durations: a
     * contributor moves to ALARM after it breaches continuously for the pending period,
     * and back to OK after it stops breaching for the recovery period. A PromQL alarm
     * starts in the OK state rather than INSUFFICIENT_DATA.
     *
     * <p>{@link EvaluationCriteria} is a union and is mutually exclusive with the
     * classic {@code metricName} and {@code metrics} parameters. When you use it you
     * must also set {@code evaluationInterval}, and you must not set {@code period},
     * {@code statistic}, {@code threshold}, {@code comparisonOperator},
     * {@code evaluationPeriods}, {@code datapointsToAlarm}, or
     * {@code treatMissingData}.
     *
     * @param cw                 the CloudWatch client
     * @param alarmName          the name of the alarm, unique within the Region
     * @param query              the PromQL query to evaluate, such as
     *                           {@code avg(cpu_utilization_percent) > 80}. The
     *                           comparison belongs in the query itself; there is no
     *                           separate threshold parameter.
     * @param evaluationInterval how often, in seconds, to run the query. Valid values
     *                           are 10, 20, 30, and any multiple of 60, up to 3600.
     * @param pendingPeriod      how long, in seconds, a contributor must breach
     *                           continuously before it moves to ALARM
     * @param recoveryPeriod     how long, in seconds, a contributor must stop breaching
     *                           before it moves back to OK
     */
    public static void putPromQLMetricAlarm(CloudWatchClient cw, String alarmName, String query,
            int evaluationInterval, int pendingPeriod, int recoveryPeriod) {
        try {
            AlarmPromQLCriteria promQLCriteria = AlarmPromQLCriteria.builder()
                    .query(query)
                    .pendingPeriod(pendingPeriod)
                    .recoveryPeriod(recoveryPeriod)
                    .build();

            PutMetricAlarmRequest request = PutMetricAlarmRequest.builder()
                    .alarmName(alarmName)
                    .alarmDescription("PromQL alarm created by the AWS SDK for Java 2.x example.")
                    .evaluationCriteria(EvaluationCriteria.builder()
                            .promQLCriteria(promQLCriteria)
                            .build())
                    .evaluationInterval(evaluationInterval)
                    .build();

            cw.putMetricAlarm(request);
            System.out.printf("Created PromQL alarm %s for query %s.%n", alarmName, query);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.PutMetricAlarm.promql.main]

    // snippet-start:[cloudwatch.java2.DescribeAlarmContributors.main]

    /**
     * Gets the contributors for a PromQL alarm. Each contributor is one series that the
     * alarm's query matched, identified by its label set. This is how you find out which
     * hosts, services, or pods are breaching, rather than only that something is.
     *
     * @param cw        the CloudWatch client
     * @param alarmName the name of the PromQL alarm
     * @return the list of contributors
     */
    public static List<AlarmContributor> describeAlarmContributors(CloudWatchClient cw, String alarmName) {
        List<AlarmContributor> contributors = new ArrayList<>();
        try {
            String nextToken = null;
            do {
                DescribeAlarmContributorsRequest request = DescribeAlarmContributorsRequest.builder()
                        .alarmName(alarmName)
                        .nextToken(nextToken)
                        .build();

                DescribeAlarmContributorsResponse response = cw.describeAlarmContributors(request);
                contributors.addAll(response.alarmContributors());
                nextToken = response.nextToken();
            } while (nextToken != null && !nextToken.isEmpty());

            for (AlarmContributor contributor : contributors) {
                StringBuilder labels = new StringBuilder();
                for (Map.Entry<String, String> attribute : contributor.contributorAttributes().entrySet()) {
                    if (labels.length() > 0) {
                        labels.append(", ");
                    }
                    labels.append(attribute.getKey()).append("=").append(attribute.getValue());
                }
                System.out.printf("%s: %s%n", contributor.contributorId(), labels);
                System.out.printf("  reason: %s%n", contributor.stateReason());
            }
            return contributors;

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return contributors;
        }
    }
    // snippet-end:[cloudwatch.java2.DescribeAlarmContributors.main]

    // snippet-start:[cloudwatch.java2.PutAlarmMuteRule.main]

    /**
     * Creates or updates an alarm mute rule. While a mute rule is active the targeted
     * alarms keep evaluating and keep transitioning between states, but their configured
     * actions do not fire. This is the supported way to suppress notifications during a
     * known maintenance window instead of disabling alarm actions and hoping someone
     * remembers to turn them back on.
     *
     * @param cw         the CloudWatch client
     * @param name       the name of the mute rule
     * @param expression when the rule activates. Use a cron expression for a recurring
     *                   window, such as {@code cron(0 2 ? * SUN *)}, or an at
     *                   expression for a one-time window, such as
     *                   {@code at(2026-09-05T02:00:00)}.
     * @param duration   how long the mute window lasts once it activates, such as
     *                   {@code 2h} or {@code 30m}
     * @param timezone   the time zone the expression is evaluated in, such as
     *                   {@code America/Los_Angeles}
     * @param alarmNames the names of up to 100 alarms to mute. If empty, the rule
     *                   applies to all alarms in the account.
     */
    public static void putAlarmMuteRule(CloudWatchClient cw, String name, String expression, String duration,
            String timezone, List<String> alarmNames) {
        try {
            Schedule schedule = Schedule.builder()
                    .expression(expression)
                    .duration(duration)
                    .timezone(timezone)
                    .build();

            PutAlarmMuteRuleRequest.Builder request = PutAlarmMuteRuleRequest.builder()
                    .name(name)
                    .description("Mute rule created by the AWS SDK for Java 2.x example.")
                    .rule(Rule.builder().schedule(schedule).build());

            if (alarmNames != null && !alarmNames.isEmpty()) {
                request.muteTargets(MuteTargets.builder().alarmNames(alarmNames).build());
            }

            cw.putAlarmMuteRule(request.build());
            System.out.printf("Put alarm mute rule %s.%n", name);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.PutAlarmMuteRule.main]

    // snippet-start:[cloudwatch.java2.GetAlarmMuteRule.main]

    /**
     * Gets the full configuration of an alarm mute rule, including its schedule, the
     * alarms it targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.
     *
     * @param cw   the CloudWatch client
     * @param name the name of the mute rule
     * @return the mute rule
     */
    public static GetAlarmMuteRuleResponse getAlarmMuteRule(CloudWatchClient cw, String name) {
        try {
            GetAlarmMuteRuleResponse response = cw.getAlarmMuteRule(GetAlarmMuteRuleRequest.builder()
                    .alarmMuteRuleName(name)
                    .build());

            System.out.printf("Mute rule %s is %s.%n", response.name(), response.statusAsString());
            return response;

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }
    }
    // snippet-end:[cloudwatch.java2.GetAlarmMuteRule.main]

    // snippet-start:[cloudwatch.java2.ListAlarmMuteRules.main]

    /**
     * Lists the alarm mute rules in the account, optionally filtered to the rules that
     * target one alarm.
     *
     * @param cw        the CloudWatch client
     * @param alarmName when non-null, only rules that target this alarm are returned
     * @return the list of mute rule summaries
     */
    public static List<AlarmMuteRuleSummary> listAlarmMuteRules(CloudWatchClient cw, String alarmName) {
        List<AlarmMuteRuleSummary> summaries = new ArrayList<>();
        try {
            String nextToken = null;
            do {
                ListAlarmMuteRulesRequest request = ListAlarmMuteRulesRequest.builder()
                        .alarmName(alarmName)
                        .nextToken(nextToken)
                        .build();

                ListAlarmMuteRulesResponse response = cw.listAlarmMuteRules(request);
                summaries.addAll(response.alarmMuteRuleSummaries());
                nextToken = response.nextToken();
            } while (nextToken != null && !nextToken.isEmpty());

            for (AlarmMuteRuleSummary summary : summaries) {
                System.out.printf("%s (%s)%n", summary.alarmMuteRuleArn(), summary.statusAsString());
            }
            return summaries;

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return summaries;
        }
    }
    // snippet-end:[cloudwatch.java2.ListAlarmMuteRules.main]

    // snippet-start:[cloudwatch.java2.DeleteAlarmMuteRule.main]

    /**
     * Deletes an alarm mute rule. The alarms it targeted resume firing their actions.
     *
     * @param cw   the CloudWatch client
     * @param name the name of the mute rule
     */
    public static void deleteAlarmMuteRule(CloudWatchClient cw, String name) {
        try {
            cw.deleteAlarmMuteRule(DeleteAlarmMuteRuleRequest.builder()
                    .alarmMuteRuleName(name)
                    .build());

            System.out.printf("Deleted alarm mute rule %s.%n", name);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.DeleteAlarmMuteRule.main]
}
// snippet-end:[cloudwatch.java2.otel.actions.main]
