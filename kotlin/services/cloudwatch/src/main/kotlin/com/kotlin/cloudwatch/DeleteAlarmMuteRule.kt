// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.delete_alarm_mute_rule.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DeleteAlarmMuteRuleRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.delete_alarm_mute_rule.import]

/**
Deletes an alarm mute rule. The alarms it targeted resume firing their actions.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <muteRuleName>

    Where:
        muteRuleName - The name of the mute rule to delete.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val muteRuleName = args[0]
    deleteAlarmMuteRule(muteRuleName)
}

// snippet-start:[cloudwatch.kotlin.delete_alarm_mute_rule.main]
suspend fun deleteAlarmMuteRule(muteRuleName: String) {
    val request =
        DeleteAlarmMuteRuleRequest {
            alarmMuteRuleName = muteRuleName
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.deleteAlarmMuteRule(request)
        println("Successfully deleted alarm mute rule $muteRuleName")
    }
}
// snippet-end:[cloudwatch.kotlin.delete_alarm_mute_rule.main]
