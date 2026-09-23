// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_alarm_history.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmHistoryRequest
import aws.sdk.kotlin.services.cloudwatch.model.HistoryItemType
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_alarm_history.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <alarmName> <startDate>

    Where:
        alarmName - The name of the alarm to get the history for.
        startDate - The start of the history window in ISO 8601 format, for example, 2026-01-01T00:00:00Z.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    val startDate = args[1]
    getAlarmHistory(alarmName, startDate)
}

// snippet-start:[cloudwatch.kotlin.describe_alarm_history.main]
suspend fun getAlarmHistory(
    alarmNameVal: String,
    startDateVal: String,
) {
    val request =
        DescribeAlarmHistoryRequest {
            alarmName = alarmNameVal
            startDate =
                aws.smithy.kotlin.runtime.time.Instant
                    .fromIso8601(startDateVal)
            endDate =
                aws.smithy.kotlin.runtime.time.Instant
                    .now()
            historyItemType = HistoryItemType.Action
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.describeAlarmHistory(request)
        val historyItems = response.alarmHistoryItems
        if (historyItems.isNullOrEmpty()) {
            println("No alarm history data found for $alarmNameVal.")
        } else {
            for (item in historyItems) {
                println("History summary: ${item.historySummary}")
                println("Time stamp: ${item.timestamp}")
            }
        }
    }
}
// snippet-end:[cloudwatch.kotlin.describe_alarm_history.main]
