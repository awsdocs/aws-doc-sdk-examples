// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.get_alarm_mute_rule.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.GetAlarmMuteRuleRequest
import aws.sdk.kotlin.services.cloudwatch.model.GetAlarmMuteRuleResponse
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.get_alarm_mute_rule.import]

/**
Gets the full configuration of an alarm mute rule, including its schedule, the alarms it
targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.

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
        muteRuleName - The name of the mute rule to describe.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val muteRuleName = args[0]
    getAlarmMuteRule(muteRuleName)
}

// snippet-start:[cloudwatch.kotlin.get_alarm_mute_rule.main]
suspend fun getAlarmMuteRule(muteRuleName: String): GetAlarmMuteRuleResponse {
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response =
            cwClient.getAlarmMuteRule(
                GetAlarmMuteRuleRequest {
                    alarmMuteRuleName = muteRuleName
                },
            )

        println("Mute rule ${response.name} is ${response.status?.value}")
        println("  ARN: ${response.alarmMuteRuleArn}")
        println("  schedule: ${response.rule?.schedule?.expression} for ${response.rule?.schedule?.duration}")
        response.muteTargets?.alarmNames?.let { println("  muted alarms: ${it.joinToString(", ")}") }
        return response
    }
}
// snippet-end:[cloudwatch.kotlin.get_alarm_mute_rule.main]
