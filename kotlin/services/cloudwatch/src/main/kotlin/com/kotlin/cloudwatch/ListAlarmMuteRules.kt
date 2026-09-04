// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.list_alarm_mute_rules.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.AlarmMuteRuleSummary
import aws.sdk.kotlin.services.cloudwatch.model.ListAlarmMuteRulesRequest
// snippet-end:[cloudwatch.kotlin.list_alarm_mute_rules.import]

/**
Lists the alarm mute rules in the account, optionally filtered to the rules that target
one alarm.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    // The alarm name is optional. With no argument, every mute rule in the account is
    // listed.
    val alarmName = args.firstOrNull()
    listAlarmMuteRules(alarmName)
}

// snippet-start:[cloudwatch.kotlin.list_alarm_mute_rules.main]
suspend fun listAlarmMuteRules(alarmNameVal: String? = null): List<AlarmMuteRuleSummary> {
    val summaries = mutableListOf<AlarmMuteRuleSummary>()

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        var token: String? = null
        do {
            val response =
                cwClient.listAlarmMuteRules(
                    ListAlarmMuteRulesRequest {
                        alarmName = alarmNameVal
                        nextToken = token
                    },
                )

            response.alarmMuteRuleSummaries?.let { summaries.addAll(it) }
            token = response.nextToken
        } while (token != null)

        summaries.forEach { summary ->
            println("${summary.alarmMuteRuleArn} (${summary.status?.value})")
        }
    }
    return summaries
}
// snippet-end:[cloudwatch.kotlin.list_alarm_mute_rules.main]
