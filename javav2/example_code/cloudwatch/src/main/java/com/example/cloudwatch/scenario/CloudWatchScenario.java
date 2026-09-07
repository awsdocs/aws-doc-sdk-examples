// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch.scenario;

// snippet-start:[cloudwatch.java2.scenario.main]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudwatch.model.AlarmContributor;
import software.amazon.awssdk.services.cloudwatch.model.AlarmMuteRuleSummary;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetAlarmMuteRuleResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Before running this Java V2 code example, set up your development environment,
 * including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This scenario demonstrates the Amazon CloudWatch OpenTelemetry (OTel) experience.
 * CloudWatch ingests OpenTelemetry metrics natively, and this example walks through what
 * you do with them: turning on enrichment so CloudWatch can correlate incoming OTLP
 * metrics with the resources that produced them, alarming on those metrics with a PromQL
 * query, and finding out which individual series drove the alarm.
 *
 * A PromQL alarm works differently from a classic metric alarm. Rather than watching one
 * metric and counting breaching periods, it evaluates a query that can match many series
 * at once, and tracks each matching series separately as a contributor.
 *
 * Note that sending OTLP metrics to CloudWatch is not an AWS SDK operation. Metrics
 * arrive over the OTLP protocol through the CloudWatch agent, an OpenTelemetry
 * Collector, or an ADOT SDK. Everything this scenario does is configuration and querying
 * around that ingestion path.
 *
 * This Java code example performs the following tasks:
 *
 * 1. List metrics and namespaces from Amazon CloudWatch.
 * 2. Start OpenTelemetry enrichment for the account.
 * 3. Explain how OTLP metrics reach CloudWatch.
 * 4. Create an alarm that evaluates a PromQL query.
 * 5. Inspect the contributors to the PromQL alarm.
 * 6. Get metric statistics and chart the metric on a dashboard.
 * 7. Mute the alarm for a maintenance window.
 * 8. Clean up the Amazon CloudWatch resources.
 */
public class CloudWatchScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final String DEFAULT_QUERY = "avg by (host) (system_cpu_utilization) > 80";

    // Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
    private static final int EVALUATION_INTERVAL = 60;
    private static final int PENDING_PERIOD = 300;
    private static final int RECOVERY_PERIOD = 120;

    static CloudWatchActions cwActions = new CloudWatchActions();

    private static final Logger logger = LoggerFactory.getLogger(CloudWatchScenario.class);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Throwable {

        final String usage = """

            Usage:
              [<dashboardJson>]\s

            Where:
              dashboardJson - The location of a JSON file describing the dashboard widgets.
                              Defaults to jsonWidgets.json in javav2/example_code/cloudwatch.\s
            """;

        if (args.length > 1) {
            logger.info(usage);
            return;
        }
        String dashboardJson = args.length == 1 ? args[0] : "jsonWidgets.json";

        // Suffix the resource names so repeated runs do not collide.
        String suffix = String.valueOf(new Random().nextInt(9000) + 1000);
        String alarmName = "doc-example-promql-alarm-" + suffix;
        String dashboardName = "doc-example-dashboard-" + suffix;
        String muteRuleName = "doc-example-mute-rule-" + suffix;

        logger.info(DASHES);
        logger.info("Welcome to the Amazon CloudWatch Basics scenario.");
        logger.info("""
            CloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through
            that experience: it turns on OTel enrichment so CloudWatch can correlate incoming
            OTLP metrics with the resources that produced them, alarms on those metrics with a
            PromQL query, and shows you which individual series drove the alarm.

            A PromQL alarm works differently from a classic metric alarm. Rather than watching
            one metric and counting breaching periods, it evaluates a query that can match many
            series at once, and tracks each one separately as a contributor.

            Let's get started...
            """);
        waitForInputToContinue(scanner);

        try {
            runScenario(alarmName, dashboardName, muteRuleName, dashboardJson);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        logger.info(DASHES);
    }

    private static void runScenario(String alarmName, String dashboardName, String muteRuleName,
            String dashboardJson) throws Throwable {

        // Tracks whether this run turned enrichment on, so that cleanup only turns off
        // enrichment that this run started.
        boolean startedEnrichment = false;

        logger.info(DASHES);
        logger.info("""
            1. List metrics and namespaces

            Before configuring anything, let's see what CloudWatch is already collecting in
            this account by calling ListMetrics.
            """);
        waitForInputToContinue(scanner);

        ArrayList<String> namespaces = cwActions.listNameSpacesAsync().join();
        logger.info("Found {} namespaces in this account:", namespaces.size());
        namespaces.stream().limit(10).forEach(namespace -> logger.info("  {}", namespace));
        if (namespaces.isEmpty()) {
            logger.info("""
                No metrics found in this account. The statistics and dashboard steps later on
                need an existing metric, so they will be skipped.
                """);
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            2. Start OpenTelemetry enrichment

            Enrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics
            you send it. Without it, your metrics arrive as opaque series with no connection to
            the resources that emitted them.

            We check the current state first, and only start enrichment if it isn't already on.
            """);
        waitForInputToContinue(scanner);

        String status = cwActions.getOTelEnrichmentStatusAsync().join();
        logger.info("Enrichment status: {}", status);

        if (!"Running".equalsIgnoreCase(status)) {
            cwActions.startOTelEnrichmentAsync().join();
            startedEnrichment = true;
            status = cwActions.getOTelEnrichmentStatusAsync().join();
            logger.info("Enrichment status: {}", status);
            logger.info("""
                Note: this run started enrichment, so the cleanup step will stop it again.
                """);
        } else {
            logger.info("""
                Enrichment was already running, so we will leave it alone. The cleanup step
                will not stop it, because other workloads in this account may depend on it.
                """);
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            3. Send OTLP metrics to CloudWatch

            This step is not an AWS SDK operation, and that's worth being explicit about.
            Metrics reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an
            OpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics API to call.

            Point your collector at the CloudWatch metrics endpoint, which follows the pattern
            https://monitoring.<region>.amazonaws.com/v1/metrics

            The endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp
            exporter rather than otlp. The metrics endpoint signs as "monitoring".
            """);
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            4. Create a PromQL alarm

            Now we alarm on those metrics. The comparison goes inside the query itself: a
            PromQL alarm has no separate threshold, comparison operator, statistic, or period.
            """);
        logger.info("Enter a PromQL query, or press <ENTER> for the default");
        logger.info("[{}]:", DEFAULT_QUERY);
        String queryInput = scanner.nextLine();
        String query = queryInput == null || queryInput.isBlank() ? DEFAULT_QUERY : queryInput.trim();

        cwActions.putPromQLMetricAlarmAsync(alarmName, query, EVALUATION_INTERVAL,
                PENDING_PERIOD, RECOVERY_PERIOD).join();
        logger.info("Created alarm {}:", alarmName);
        logger.info("  query:              {}", query);
        logger.info("  evaluationInterval: {} seconds", EVALUATION_INTERVAL);
        logger.info("  pendingPeriod:      {} seconds", PENDING_PERIOD);
        logger.info("  recoveryPeriod:     {} seconds", RECOVERY_PERIOD);
        logger.info("""

            A PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which is
            another way it differs from a classic alarm.
            """);
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            5. Inspect the alarm's contributors

            Each contributor is one series the query matched, identified by its label set. This
            is how you find out which host is unhealthy rather than only that something is.
            Classic alarms have no equivalent.
            """);
        waitForInputToContinue(scanner);

        List<AlarmContributor> contributors = cwActions.describeAlarmContributorsAsync(alarmName).join();
        if (contributors.isEmpty()) {
            logger.info("""
                No contributors yet. The query matched no series, which usually means no OTel
                metrics with these labels have arrived. Once your collector is sending data,
                each matching series appears here with its labels and the reason it breached.
                """);
        } else {
            logger.info("Found {} contributors:", contributors.size());
            for (AlarmContributor contributor : contributors) {
                StringBuilder labels = new StringBuilder();
                for (Map.Entry<String, String> attribute : contributor.contributorAttributes().entrySet()) {
                    if (labels.length() > 0) {
                        labels.append(", ");
                    }
                    labels.append(attribute.getKey()).append("=").append(attribute.getValue());
                }
                logger.info("  {}: {}", contributor.contributorId(), labels);
                logger.info("    reason: {}", contributor.stateReason());
            }
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            6. Get statistics and chart the metric on a dashboard

            Statistics and dashboards are how you see what the alarm is evaluating.
            """);
        waitForInputToContinue(scanner);

        boolean dashboardCreated = false;
        if (!namespaces.isEmpty()) {
            String namespace = namespaces.get(0);
            ArrayList<String> metrics = cwActions.listMetsAsync(namespace).join();
            if (metrics != null && !metrics.isEmpty()) {
                String metricName = metrics.get(0);
                String startDate = Instant.now().minus(24, ChronoUnit.HOURS).toString();
                try {
                    Dimension dimension = cwActions.getSpecificMetAsync(namespace).join();
                    cwActions.getAndDisplayMetricStatisticsAsync(namespace, metricName,
                            "Average", startDate, dimension).join();
                } catch (RuntimeException e) {
                    logger.info("Could not get statistics for {}/{}: {}", namespace, metricName,
                            e.getMessage());
                }
            } else {
                logger.info("No metrics found in namespace {}, skipping statistics.", namespace);
            }

            try {
                cwActions.createDashboardWithMetricsAsync(dashboardName, dashboardJson).join();
                dashboardCreated = true;
                cwActions.listDashboardsAsync().join();
            } catch (RuntimeException e) {
                logger.info("Could not create the dashboard: {}", e.getMessage());
            }
        } else {
            logger.info("Skipping statistics and dashboard because no metrics exist yet.");
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("""
            7. Mute the alarm for a maintenance window

            While a mute rule is active the targeted alarms keep evaluating and keep changing
            state, but their actions do not fire. This is the supported way to suppress
            notifications during planned maintenance, instead of disabling alarm actions and
            hoping someone remembers to turn them back on.
            """);
        waitForInputToContinue(scanner);

        // The expression is a five-field cron expression,
        // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five fields,
        // not the six that Amazon EventBridge uses. For a one-time window, use
        // at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration from
        // PT1M to P15D, so PT2H rather than 2h.
        String expression = "cron(0 2 * * SUN)";
        String duration = "PT2H";
        String timezone = "America/Los_Angeles";

        cwActions.putAlarmMuteRuleAsync(muteRuleName, expression, duration, timezone,
                List.of(alarmName)).join();
        logger.info("Created mute rule {}:", muteRuleName);
        logger.info("  schedule: {} for {}", expression, duration);
        logger.info("  timezone: {}", timezone);
        logger.info("  targets:  {}", alarmName);
        logger.info("""

            Note the two formats here. The expression is a five-field cron expression, five
            rather than the six Amazon EventBridge uses. The duration is an ISO 8601 duration,
            so 'PT2H' and not '2h'.

            Also note that muteTargets is set explicitly. If you leave it out, the rule applies
            to every alarm in the account.
            """);

        GetAlarmMuteRuleResponse muteRule = cwActions.getAlarmMuteRuleAsync(muteRuleName).join();
        logger.info("Read the rule back: status {}, mute type {}.", muteRule.statusAsString(),
                muteRule.muteType());

        List<AlarmMuteRuleSummary> summaries = cwActions.listAlarmMuteRulesAsync(alarmName).join();
        logger.info("Found {} mute rules targeting this alarm.", summaries.size());
        // Mute rule summaries carry no name field, only an ARN, so match on the ARN suffix.
        summaries.stream()
                .filter(summary -> summary.alarmMuteRuleArn().endsWith("/" + muteRuleName)
                        || summary.alarmMuteRuleArn().endsWith(":" + muteRuleName))
                .findFirst()
                .ifPresent(summary -> logger.info("  matched by ARN: {} ({})",
                        summary.alarmMuteRuleArn(), summary.statusAsString()));
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("8. Clean up");
        logger.info("Delete the resources this scenario created? (y/n)");
        String cleanUp = scanner.nextLine();
        if (cleanUp == null || !cleanUp.trim().equalsIgnoreCase("y")) {
            logger.info("""
                Skipping cleanup. Note that the alarm, dashboard, and mute rule are still in
                your account, and enrichment may still be running.
                """);
            logger.info(DASHES);
            logger.info("This concludes the Amazon CloudWatch Basics scenario.");
            return;
        }

        // Each deletion is attempted independently so that one failure does not leave the
        // remaining resources behind.
        try {
            cwActions.deleteAlarmMuteRuleAsync(muteRuleName).join();
        } catch (RuntimeException e) {
            logger.info("Could not delete the mute rule: {}", e.getMessage());
        }

        try {
            cwActions.deleteCWAlarmAsync(alarmName).join();
            logger.info("Deleted alarm {}.", alarmName);
        } catch (RuntimeException e) {
            logger.info("Could not delete the alarm: {}", e.getMessage());
        }

        if (dashboardCreated) {
            try {
                cwActions.deleteDashboardAsync(dashboardName).join();
                logger.info("Deleted dashboard {}.", dashboardName);
            } catch (RuntimeException e) {
                logger.info("Could not delete the dashboard: {}", e.getMessage());
            }
        }

        if (startedEnrichment) {
            try {
                cwActions.stopOTelEnrichmentAsync().join();
                logger.info("Stopped OTel enrichment, because this run started it.");
            } catch (RuntimeException e) {
                logger.info("Could not stop OTel enrichment: {}", e.getMessage());
            }
        } else {
            logger.info("""
                Left OTel enrichment running, because it was already on before this run.
                """);
        }

        logger.info(DASHES);
        logger.info("This concludes the Amazon CloudWatch Basics scenario.");
        logger.info(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            logger.info("");
            logger.info("Press <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                logger.info("Continuing with the program...");
                logger.info("");
                break;
            } else {
                logger.info("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[cloudwatch.java2.scenario.main]
