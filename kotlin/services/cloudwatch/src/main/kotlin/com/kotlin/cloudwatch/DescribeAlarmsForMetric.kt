// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_alarms_for_metric.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmsForMetricRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_alarms_for_metric.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <namespace> <metricName>

    Where:
        namespace - The namespace that contains the metric, for example, AWS/EC2.
        metricName - The name of the metric, for example, CPUUtilization.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val namespace = args[0]
    val metricName = args[1]
    describeAlarmsForMetric(namespace, metricName)
}

// snippet-start:[cloudwatch.kotlin.describe_alarms_for_metric.main]
suspend fun describeAlarmsForMetric(
    namespaceVal: String,
    metricNameVal: String,
) {
    val request =
        DescribeAlarmsForMetricRequest {
            namespace = namespaceVal
            metricName = metricNameVal
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.describeAlarmsForMetric(request)
        val alarms = response.metricAlarms
        if (alarms.isNullOrEmpty()) {
            println("No alarms found for $metricNameVal in $namespaceVal.")
        } else {
            for (alarm in alarms) {
                println("Alarm name: ${alarm.alarmName}")
                println("Alarm state: ${alarm.stateValue}")
            }
        }
    }
}
// snippet-end:[cloudwatch.kotlin.describe_alarms_for_metric.main]
