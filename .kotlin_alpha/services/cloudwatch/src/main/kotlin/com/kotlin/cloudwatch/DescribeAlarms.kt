//snippet-sourcedescription:[DescribeAlarms.kt demonstrates how to get information about Amazon CloudWatch alarms.]
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


// snippet-start:[cloudwatch.kotlin.describe_alarms.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmsRequest
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAlarmsResponse
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_alarms.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val cwClient = CloudWatchClient{region="us-east-1"}
    desCWAlarms(cwClient)
    cwClient.close()
}

// snippet-start:[cloudwatch.kotlin.describe_alarms.main]
suspend fun desCWAlarms(cwClient: CloudWatchClient) {

    try {
        var done = false
        var newToken: String? = null
        while (!done) {
            var response: DescribeAlarmsResponse

            if (newToken == null) {
                val request = DescribeAlarmsRequest{}
                response = cwClient.describeAlarms(request)
            } else {

                val request = DescribeAlarmsRequest {
                    nextToken = newToken
                }
                response = cwClient.describeAlarms(request)
            }
            for (alarm in response.metricAlarms!!)
                println("Retrieved alarm ${alarm.alarmName}")

            if (response.nextToken == null)
                done = true
            else
                newToken = response.nextToken
        }

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[cloudwatch.kotlin.describe_alarms.main]
