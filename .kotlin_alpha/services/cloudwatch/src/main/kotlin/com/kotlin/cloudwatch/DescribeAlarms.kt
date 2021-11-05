//snippet-sourcedescription:[DescribeAlarms.kt demonstrates how to get information about Amazon CloudWatch alarms.]
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


// snippet-start:[cloudwatch.kotlin.describe_alarms.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_alarms.import]

suspend fun main() {

    val cwClient = CloudWatchClient{region="us-east-1"}
    desCWAlarms(cwClient)
    cwClient.close()
}

// snippet-start:[cloudwatch.kotlin.describe_alarms.main]
suspend fun desCWAlarms(cwClient: CloudWatchClient) {

    try {
           val response = cwClient.describeAlarms(DescribeAlarmsRequest {})
           response.metricAlarms?.forEach { alarm ->
               println("Retrieved alarm ${alarm.alarmName}")
           }

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[cloudwatch.kotlin.describe_alarms.main]