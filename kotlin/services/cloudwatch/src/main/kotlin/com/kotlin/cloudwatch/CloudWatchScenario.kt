// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DeleteAlarmsRequest
import aws.sdk.kotlin.services.cloudwatch.model.DeleteDashboardsRequest
import aws.sdk.kotlin.services.cloudwatch.model.Dimension
import aws.sdk.kotlin.services.cloudwatch.model.GetMetricStatisticsRequest
import aws.sdk.kotlin.services.cloudwatch.model.ListMetricsRequest
import aws.sdk.kotlin.services.cloudwatch.model.OTelEnrichmentStatus
import aws.sdk.kotlin.services.cloudwatch.model.PutDashboardRequest
import aws.sdk.kotlin.services.cloudwatch.model.Statistic
import aws.sdk.kotlin.services.cloudwatch.paginators.listDashboardsPaginated
import kotlinx.coroutines.flow.transform
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Random
import java.util.Scanner

// snippet-start:[cloudwatch.kotlin.scenario.main]

/**
 Before running this Kotlin code example, set up your development environment,
 including your credentials.

 For more information, see the following documentation topic:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

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

 This Kotlin code example performs the following tasks:

 1. List metrics and namespaces from Amazon CloudWatch.
 2. Start OpenTelemetry enrichment for the account.
 3. Explain how OTLP metrics reach CloudWatch.
 4. Create an alarm that evaluates a PromQL query.
 5. Inspect the contributors to the PromQL alarm.
 6. Get metric statistics and chart the metric on a dashboard.
 7. Mute the alarm for a maintenance window.
 8. Clean up the Amazon CloudWatch resources.
 */

val DASHES: String? = "-".repeat(80)

private const val DEFAULT_QUERY = "avg by (host) (system_cpu_utilization) > 80"

// Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
private const val EVALUATION_INTERVAL = 60
private const val PENDING_PERIOD = 300
private const val RECOVERY_PERIOD = 120

val scenarioScanner = Scanner(System.`in`)

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            [<dashboardJson>]

        Where:
            dashboardJson - The location of a JSON file describing the dashboard widgets.
                            Defaults to jsonWidgets.json in kotlin/services/cloudwatch.
    """

    if (args.size > 1) {
        println(usage)
        return
    }
    val dashboardJson = if (args.size == 1) args[0] else "jsonWidgets.json"

    // Suffix the resource names so repeated runs do not collide.
    val suffix = (Random().nextInt(9000) + 1000).toString()
    val alarmName = "doc-example-promql-alarm-$suffix"
    val dashboardName = "doc-example-dashboard-$suffix"
    val muteRuleName = "doc-example-mute-rule-$suffix"

    println(DASHES)
    println("Welcome to the Amazon CloudWatch Basics scenario.")
    println(
        """
        CloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through
        that experience: it turns on OTel enrichment so CloudWatch can correlate incoming
        OTLP metrics with the resources that produced them, alarms on those metrics with a
        PromQL query, and shows you which individual series drove the alarm.

        A PromQL alarm works differently from a classic metric alarm. Rather than watching
        one metric and counting breaching periods, it evaluates a query that can match many
        series at once, and tracks each one separately as a contributor.

        Let's get started...
        """.trimIndent(),
    )
    waitForInputToContinue()

    // Tracks whether this run turned enrichment on, so that cleanup only turns off
    // enrichment that this run started.
    var startedEnrichment = false
    var dashboardCreated = false

    println(DASHES)
    println(
        """
        1. List metrics and namespaces

        Before configuring anything, let's see what CloudWatch is already collecting in
        this account by calling ListMetrics.
        """.trimIndent(),
    )
    waitForInputToContinue()

    val namespaces = listNameSpaces()
    println("Found ${namespaces.size} namespaces in this account:")
    namespaces.take(10).forEach { println("  $it") }
    if (namespaces.isEmpty()) {
        println(
            """
            No metrics found in this account. The statistics and dashboard steps later on
            need an existing metric, so they will be skipped.
            """.trimIndent(),
        )
    }
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        2. Start OpenTelemetry enrichment

        Enrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics
        you send it. Without it, your metrics arrive as opaque series with no connection to
        the resources that emitted them.

        We check the current state first, and only start enrichment if it isn't already on.
        """.trimIndent(),
    )
    waitForInputToContinue()

    val status = getOTelEnrichmentStatus()
    if (status !is OTelEnrichmentStatus.Running) {
        startOTelEnrichment()
        startedEnrichment = true
        getOTelEnrichmentStatus()
        println("Note: this run started enrichment, so the cleanup step will stop it again.")
    } else {
        println(
            """
            Enrichment was already running, so we will leave it alone. The cleanup step
            will not stop it, because other workloads in this account may depend on it.
            """.trimIndent(),
        )
    }
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        3. Send OTLP metrics to CloudWatch

        This step is not an AWS SDK operation, and that's worth being explicit about.
        Metrics reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an
        OpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics API to call.

        Point your collector at the CloudWatch metrics endpoint, which follows the pattern
        https://monitoring.<region>.amazonaws.com/v1/metrics

        The endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp
        exporter rather than otlp. The metrics endpoint signs as "monitoring".
        """.trimIndent(),
    )
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        4. Create a PromQL alarm

        Now we alarm on those metrics. The comparison goes inside the query itself: a
        PromQL alarm has no separate threshold, comparison operator, statistic, or period.
        """.trimIndent(),
    )
    println("Enter a PromQL query, or press <ENTER> for the default")
    println("[$DEFAULT_QUERY]:")
    val queryInput = scenarioScanner.nextLine()
    val query = if (queryInput.isBlank()) DEFAULT_QUERY else queryInput.trim()

    putPromQlMetricAlarm(alarmName, query, EVALUATION_INTERVAL, PENDING_PERIOD, RECOVERY_PERIOD)
    println("Created alarm $alarmName:")
    println("  query:              $query")
    println("  evaluationInterval: $EVALUATION_INTERVAL seconds")
    println("  pendingPeriod:      $PENDING_PERIOD seconds")
    println("  recoveryPeriod:     $RECOVERY_PERIOD seconds")
    println(
        """
        A PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which is
        another way it differs from a classic alarm.
        """.trimIndent(),
    )
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        5. Inspect the alarm's contributors

        Each contributor is one series the query matched, identified by its label set. This
        is how you find out which host is unhealthy rather than only that something is.
        Classic alarms have no equivalent.
        """.trimIndent(),
    )
    waitForInputToContinue()

    describeAlarmContributors(alarmName)
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        6. Get statistics and chart the metric on a dashboard

        Statistics and dashboards are how you see what the alarm is evaluating.
        """.trimIndent(),
    )
    waitForInputToContinue()

    if (namespaces.isNotEmpty()) {
        val namespace = namespaces[0]
        val metrics = listMets(namespace)
        if (metrics != null && metrics.isNotEmpty()) {
            val metricName = metrics[0]
            val startDate = Instant.now().minus(24, ChronoUnit.HOURS).toString()
            try {
                val dimension = getSpecificMet(namespace)
                if (dimension != null) {
                    getAndDisplayMetricStatistics(namespace, metricName, "Average", startDate, dimension)
                }
            } catch (e: Exception) {
                println("Could not get statistics for $namespace/$metricName: ${e.message}")
            }
        } else {
            println("No metrics found in namespace $namespace, skipping statistics.")
        }

        try {
            createDashboardWithMetrics(dashboardName, dashboardJson)
            dashboardCreated = true
            listDashboards()
        } catch (e: Exception) {
            println("Could not create the dashboard: ${e.message}")
        }
    } else {
        println("Skipping statistics and dashboard because no metrics exist yet.")
    }
    waitForInputToContinue()

    println(DASHES)
    println(
        """
        7. Mute the alarm for a maintenance window

        While a mute rule is active the targeted alarms keep evaluating and keep changing
        state, but their actions do not fire. This is the supported way to suppress
        notifications during planned maintenance, instead of disabling alarm actions and
        hoping someone remembers to turn them back on.
        """.trimIndent(),
    )
    waitForInputToContinue()

    // The expression is a five-field cron expression,
    // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five fields,
    // not the six that Amazon EventBridge uses. For a one-time window, use
    // at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration from
    // PT1M to P15D, so PT2H rather than 2h.
    val expression = "cron(0 2 * * SUN)"
    val duration = "PT2H"
    val timezone = "America/Los_Angeles"

    putAlarmMuteRule(muteRuleName, expression, duration, listOf(alarmName), timezone)
    println("Created mute rule $muteRuleName:")
    println("  schedule: $expression for $duration")
    println("  timezone: $timezone")
    println("  targets:  $alarmName")
    println(
        """
        Note the two formats here. The expression is a five-field cron expression, five
        rather than the six Amazon EventBridge uses. The duration is an ISO 8601 duration,
        so 'PT2H' and not '2h'.

        Also note that muteTargets is set explicitly. If you leave it out, the rule applies
        to every alarm in the account.
        """.trimIndent(),
    )

    val muteRule = getAlarmMuteRule(muteRuleName)
    println("Read the rule back: status ${muteRule.status?.value}, mute type ${muteRule.muteType}.")

    val summaries = listAlarmMuteRules(alarmName)
    println("Found ${summaries.size} mute rules targeting this alarm.")
    // Mute rule summaries carry no name field, only an ARN, so match on the ARN suffix.
    summaries
        .firstOrNull { summary ->
            summary.alarmMuteRuleArn?.endsWith("/$muteRuleName") == true ||
                summary.alarmMuteRuleArn?.endsWith(":$muteRuleName") == true
        }?.let { summary ->
            println("  matched by ARN: ${summary.alarmMuteRuleArn} (${summary.status?.value})")
        }
    waitForInputToContinue()

    println(DASHES)
    println("8. Clean up")
    println("Delete the resources this scenario created? (y/n)")
    val cleanUp = scenarioScanner.nextLine()
    if (!cleanUp.trim().equals("y", ignoreCase = true)) {
        println(
            """
            Skipping cleanup. Note that the alarm, dashboard, and mute rule are still in
            your account, and enrichment may still be running.
            """.trimIndent(),
        )
        println(DASHES)
        println("This concludes the Amazon CloudWatch Basics scenario.")
        return
    }

    // Each deletion is attempted independently so that one failure does not leave the
    // remaining resources behind.
    try {
        deleteAlarmMuteRule(muteRuleName)
    } catch (e: Exception) {
        println("Could not delete the mute rule: ${e.message}")
    }

    try {
        deleteAlarm(alarmName)
    } catch (e: Exception) {
        println("Could not delete the alarm: ${e.message}")
    }

    if (dashboardCreated) {
        try {
            deleteDashboard(dashboardName)
        } catch (e: Exception) {
            println("Could not delete the dashboard: ${e.message}")
        }
    }

    if (startedEnrichment) {
        try {
            stopOTelEnrichment()
            println("Stopped OTel enrichment, because this run started it.")
        } catch (e: Exception) {
            println("Could not stop OTel enrichment: ${e.message}")
        }
    } else {
        println("Left OTel enrichment running, because it was already on before this run.")
    }

    println(DASHES)
    println("This concludes the Amazon CloudWatch Basics scenario.")
    println(DASHES)
}

private fun waitForInputToContinue() {
    while (true) {
        println("")
        println("Press <ENTER> to continue:")
        val input = scenarioScanner.nextLine()
        if (input.trim().isEmpty()) {
            println("Continuing with the program...")
            println("")
            break
        }
        println("Invalid input. Please try again.")
    }
}

// snippet-start:[cloudwatch.kotlin.scenario.del.alarm.main]
suspend fun deleteAlarm(alarmNameVal: String) {
    val request =
        DeleteAlarmsRequest {
            alarmNames = listOf(alarmNameVal)
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.deleteAlarms(request)
        println("Successfully deleted alarm $alarmNameVal")
    }
}
// snippet-end:[cloudwatch.kotlin.scenario.del.alarm.main]

// snippet-start:[cloudwatch.kotlin.scenario.del.dashboard.main]
suspend fun deleteDashboard(dashboardName: String) {
    val dashboardsRequest =
        DeleteDashboardsRequest {
            dashboardNames = listOf(dashboardName)
        }
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.deleteDashboards(dashboardsRequest)
        println("$dashboardName was successfully deleted.")
    }
}
// snippet-end:[cloudwatch.kotlin.scenario.del.dashboard.main]

// snippet-start:[cloudwatch.kotlin.scenario.list.dashboard.main]
suspend fun listDashboards() {
    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
        cwClient
            .listDashboardsPaginated({})
            .transform { it.dashboardEntries?.forEach { obj -> emit(obj) } }
            .collect { obj ->
                println("Name is ${obj.dashboardName}")
                println("Dashboard ARN is ${obj.dashboardArn}")
            }
    }
}
// snippet-end:[cloudwatch.kotlin.scenario.list.dashboard.main]

// snippet-start:[cloudwatch.kotlin.scenario.create.dashboard.main]
suspend fun createDashboardWithMetrics(
    dashboardNameVal: String,
    fileNameVal: String,
) {
    val dashboardRequest =
        PutDashboardRequest {
            dashboardName = dashboardNameVal
            dashboardBody = readFileAsString(fileNameVal)
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.putDashboard(dashboardRequest)
        println("$dashboardNameVal was successfully created.")
        val messages = response.dashboardValidationMessages
        if (messages != null) {
            if (messages.isEmpty()) {
                println("There are no messages in the new Dashboard")
            } else {
                for (message in messages) {
                    println("Message is: ${message.message}")
                }
            }
        }
    }
}
// snippet-end:[cloudwatch.kotlin.scenario.create.dashboard.main]

fun readFileAsString(file: String): String = String(Files.readAllBytes(Paths.get(file)))

// snippet-start:[cloudwatch.kotlin.scenario.display.metrics.main]
suspend fun getAndDisplayMetricStatistics(
    nameSpaceVal: String,
    metVal: String,
    metricOption: String,
    date: String,
    myDimension: Dimension,
) {
    val start = Instant.parse(date)
    val endDate = Instant.now()
    val statisticsRequest =
        GetMetricStatisticsRequest {
            endTime =
                aws.smithy.kotlin.runtime.time
                    .Instant(endDate)
            startTime =
                aws.smithy.kotlin.runtime.time
                    .Instant(start)
            dimensions = listOf(myDimension)
            metricName = metVal
            namespace = nameSpaceVal
            period = 86400
            statistics = listOf(Statistic.fromValue(metricOption))
        }

    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.getMetricStatistics(statisticsRequest)
        val data = response.datapoints
        if (data != null) {
            if (data.isNotEmpty()) {
                for (datapoint in data) {
                    println("Timestamp: ${datapoint.timestamp} Maximum value: ${datapoint.maximum}")
                }
            } else {
                println("The returned data list is empty")
            }
        }
    }
}
// snippet-end:[cloudwatch.kotlin.scenario.display.metrics.main]

// snippet-start:[cloudwatch.kotlin.scenario.list.metrics.main]
suspend fun listMets(namespaceVal: String?): ArrayList<String>? {
    val metList = ArrayList<String>()
    val request =
        ListMetricsRequest {
            namespace = namespaceVal
        }
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val reponse = cwClient.listMetrics(request)
        reponse.metrics?.forEach { metrics ->
            val data = metrics.metricName
            if (!metList.contains(data)) {
                metList.add(data!!)
            }
        }
    }
    return metList
}
// snippet-end:[cloudwatch.kotlin.scenario.list.metrics.main]

suspend fun getSpecificMet(namespaceVal: String?): Dimension? {
    val request =
        ListMetricsRequest {
            namespace = namespaceVal
        }
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.listMetrics(request)
        val myList = response.metrics
        if (myList != null) {
            return myList[0].dimensions?.get(0)
        }
    }
    return null
}

// snippet-start:[cloudwatch.kotlin.scenario.list.namespaces.main]
suspend fun listNameSpaces(): ArrayList<String> {
    val nameSpaceList = ArrayList<String>()
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.listMetrics(ListMetricsRequest {})
        response.metrics?.forEach { metrics ->
            val data = metrics.namespace
            if (!nameSpaceList.contains(data)) {
                nameSpaceList.add(data!!)
            }
        }
    }
    return nameSpaceList
}
// snippet-end:[cloudwatch.kotlin.scenario.list.namespaces.main]
// snippet-end:[cloudwatch.kotlin.scenario.main]
