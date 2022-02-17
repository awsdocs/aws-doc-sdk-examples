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
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmsRequest
// snippet-end:[cloudwatch.kotlin.describe_alarms.import]

suspend fun main() {
    desCWAlarms()
}

// snippet-start:[cloudwatch.kotlin.describe_alarms.main]
suspend fun desCWAlarms() {

    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
           val response = cwClient.describeAlarms(DescribeAlarmsRequest {})
           response.metricAlarms?.forEach { alarm ->
               println("Retrieved alarm ${alarm.alarmName}")
           }
    }
 }
// snippet-end:[cloudwatch.kotlin.describe_alarms.main]