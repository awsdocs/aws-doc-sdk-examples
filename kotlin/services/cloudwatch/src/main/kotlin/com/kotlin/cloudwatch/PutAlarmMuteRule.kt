// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_alarm_mute_rule.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.MuteTargets
import aws.sdk.kotlin.services.cloudwatch.model.PutAlarmMuteRuleRequest
import aws.sdk.kotlin.services.cloudwatch.model.Rule
import aws.sdk.kotlin.services.cloudwatch.model.Schedule
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_alarm_mute_rule.import]

/**
Creates or updates an alarm mute rule. While a mute rule is active, the targeted alarms
keep evaluating and keep transitioning between states, but their configured actions do
not fire. This is the supported way to suppress notifications during a known maintenance
window, instead of disabling alarm actions and relying on someone to turn them back on.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <muteRuleName> <alarmName>

    Where:
        muteRuleName - The name of the mute rule to create.
        alarmName - The name of the alarm to mute.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val muteRuleName = args[0]
    val alarmName = args[1]
    putAlarmMuteRule(muteRuleName, "cron(0 2 ? * SUN *)", "2h", listOf(alarmName))
}

// snippet-start:[cloudwatch.kotlin.put_alarm_mute_rule.main]
suspend fun putAlarmMuteRule(
    muteRuleName: String,
    expressionVal: String,
    durationVal: String,
    alarmNamesVal: List<String>,
    timezoneVal: String = "America/Los_Angeles",
) {
    // Use a cron expression for a recurring window, such as cron(0 2 ? * SUN *), or an
    // at expression for a one-time window, such as at(2026-09-05T02:00:00).
    val scheduleOb =
        Schedule {
            expression = expressionVal
            duration = durationVal
            timezone = timezoneVal
        }

    val request =
        PutAlarmMuteRuleRequest {
            name = muteRuleName
            description = "A mute rule created by the Kotlin SDK"
            rule =
                Rule {
                    schedule = scheduleOb
                }
            // Target up to 100 alarms. If muteTargets is omitted, the rule applies to
            // every alarm in the account.
            if (alarmNamesVal.isNotEmpty()) {
                muteTargets =
                    MuteTargets {
                        alarmNames = alarmNamesVal
                    }
            }
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.putAlarmMuteRule(request)
        println("Successfully put alarm mute rule $muteRuleName")
    }
}
// snippet-end:[cloudwatch.kotlin.put_alarm_mute_rule.main]
