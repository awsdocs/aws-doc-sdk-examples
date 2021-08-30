//snippet-sourcedescription:[DeleteAlarm.kt demonstrates how to delete an Amazon CloudWatch alarm.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/11/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.delete_metrics.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DeleteAlarmsRequest
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
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
    val cwClient = CloudWatchClient{region="us-east-1"}
    deleteCWAlarm(cwClient, alarmName)
    cwClient.close()
}

// snippet-start:[cloudwatch.kotlin.delete_metrics.main]
suspend fun deleteCWAlarm(cwClient: CloudWatchClient, alarmNameVal: String) {

    try {
        val request = DeleteAlarmsRequest {
            alarmNames = listOf(alarmNameVal)
        }

        cwClient.deleteAlarms(request)
        println("Successfully deleted alarm $alarmNameVal")

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cloudwatch.kotlin.delete_metrics.main]