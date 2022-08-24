// snippet-sourcedescription:[DisableAlarmActions.kt demonstrates how to disable actions on an Amazon CloudWatch alarm.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.disable_alarm_actions.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DisableAlarmActionsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.disable_alarm_actions.import]

/**
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
        alarmName - An alarm name to use.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    disableActions(alarmName)
}

// snippet-start:[cloudwatch.kotlin.disable_alarm_actions.main]
suspend fun disableActions(alarmName: String) {

    val request = DisableAlarmActionsRequest {
        alarmNames = listOf(alarmName)
    }
    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
        cwClient.disableAlarmActions(request)
        println("Successfully disabled actions on alarm $alarmName")
    }
}
// snippet-end:[cloudwatch.kotlin.disable_alarm_actions.main]
