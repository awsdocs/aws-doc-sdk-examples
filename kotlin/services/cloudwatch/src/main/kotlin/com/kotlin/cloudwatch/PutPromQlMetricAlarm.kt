// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_promql_metric_alarm.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.AlarmPromQlCriteria
import aws.sdk.kotlin.services.cloudwatch.model.EvaluationCriteria
import aws.sdk.kotlin.services.cloudwatch.model.PutMetricAlarmRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_promql_metric_alarm.import]

/**
Creates an alarm that evaluates a PromQL query against OpenTelemetry metrics.

A PromQL alarm differs from a classic metric alarm in a few ways. The query can match
many series at once, and each matching series is tracked separately as a contributor.
Instead of counting breaching periods, you specify durations: a contributor moves to
ALARM after it breaches continuously for the pending period, and back to OK after it
stops breaching for the recovery period. A PromQL alarm starts in the OK state rather
than INSUFFICIENT_DATA.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <alarmName> <query>

    Where:
        alarmName - An alarm name to use.
        query - The PromQL query to evaluate, such as avg(cpu_utilization_percent) > 80.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    val query = args[1]
    putPromQlMetricAlarm(alarmName, query)
}

// snippet-start:[cloudwatch.kotlin.put_promql_metric_alarm.main]
suspend fun putPromQlMetricAlarm(
    alarmNameVal: String,
    queryVal: String,
    evaluationIntervalVal: Int = 60,
    pendingPeriodVal: Int = 300,
    recoveryPeriodVal: Int = 120,
) {
    // The comparison belongs in the query itself. A PromQL alarm has no separate
    // threshold, comparison operator, statistic, period, or evaluation periods.
    //
    // Note that the Kotlin SDK spells this AlarmPromQlCriteria, with a lowercase l in
    // "Ql". Every other AWS SDK spells it PromQL, so don't be thrown by the difference
    // when comparing this example against the other language versions.
    val promQlCriteria =
        AlarmPromQlCriteria {
            query = queryVal
            pendingPeriod = pendingPeriodVal
            recoveryPeriod = recoveryPeriodVal
        }

    // EvaluationCriteria is a union and is mutually exclusive with the classic
    // metricName and metrics parameters. When you use it, you must also set
    // evaluationInterval.
    val request =
        PutMetricAlarmRequest {
            alarmName = alarmNameVal
            alarmDescription = "A PromQL alarm created by the Kotlin SDK"
            evaluationCriteria = EvaluationCriteria.PromQlCriteria(promQlCriteria)
            evaluationInterval = evaluationIntervalVal
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.putMetricAlarm(request)
        println("Successfully created PromQL alarm $alarmNameVal for query $queryVal")
    }
}
// snippet-end:[cloudwatch.kotlin.put_promql_metric_alarm.main]
