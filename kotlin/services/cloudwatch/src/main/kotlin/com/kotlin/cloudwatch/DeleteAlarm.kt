//snippet-sourcedescription:[DeleteAlarm.kt demonstrates how to delete an Amazon CloudWatch alarm.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.delete_metrics.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DeleteAlarmsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.delete_metrics.import]

suspend fun main(args:Array<String>) {

    val usage  = """

    Usage:
        <alarmName>  

    Where:
        alarmName - an alarm name to delete.
     """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    deleteCWAlarm(alarmName)
}

// snippet-start:[cloudwatch.kotlin.delete_metrics.main]
suspend fun deleteCWAlarm(alarmNameVal: String) {

    val request = DeleteAlarmsRequest {
        alarmNames = listOf(alarmNameVal)
    }

    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
            cwClient.deleteAlarms(request)
            println("Successfully deleted alarm $alarmNameVal")
    }
}
// snippet-end:[cloudwatch.kotlin.delete_metrics.main]