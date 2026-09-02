// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_alarm_contributors.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.AlarmContributor
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmContributorsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_alarm_contributors.import]

/**
Gets the contributors for a PromQL alarm. Each contributor is one series that the alarm's
query matched, identified by its label set. This is how you find out which hosts,
services, or pods are breaching, rather than only that something is.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <alarmName>

    Where:
        alarmName - The name of the PromQL alarm.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    describeAlarmContributors(alarmName)
}

// snippet-start:[cloudwatch.kotlin.describe_alarm_contributors.main]
suspend fun describeAlarmContributors(alarmNameVal: String): List<AlarmContributor> {
    val contributors = mutableListOf<AlarmContributor>()

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        var token: String? = null
        do {
            val response =
                cwClient.describeAlarmContributors(
                    DescribeAlarmContributorsRequest {
                        alarmName = alarmNameVal
                        nextToken = token
                    },
                )

            response.alarmContributors?.let { contributors.addAll(it) }
            token = response.nextToken
        } while (token != null)

        if (contributors.isEmpty()) {
            println(
                "No contributors yet. The query matched no series, which usually means no " +
                    "OTel metrics with these labels have arrived",
            )
        }

        contributors.forEach { contributor ->
            val labels =
                contributor.contributorAttributes
                    ?.entries
                    ?.sortedBy { it.key }
                    ?.joinToString(", ") { "${it.key}=${it.value}" }
            println("${contributor.contributorId}: $labels")
            println("  reason: ${contributor.stateReason}")
        }
    }
    return contributors
}
// snippet-end:[cloudwatch.kotlin.describe_alarm_contributors.main]
