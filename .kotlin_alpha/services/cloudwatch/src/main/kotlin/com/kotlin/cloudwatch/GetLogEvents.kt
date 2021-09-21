//snippet-sourcedescription:[GetLogEvents.kt demonstrates how to get log events from Amazon CloudWatch.]
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

// snippet-start:[cloudwatch.kotlin.get_logs.import]
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.GetLogEventsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.get_logs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <logGroup> <logStreamName> 

    Where:
        logGroup - a log group name (testgroup).
        logStreamName - the name of the log stream (for example, mystream).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val logGroup = args[0]
    val logStreamName = args[1]
    val cwlClient = CloudWatchLogsClient{region="us-west-2"}
    getCWLogEvents(cwlClient, logGroup, logStreamName)
}

// snippet-start:[cloudwatch.kotlin.get_logs.main]
suspend fun getCWLogEvents(cloudWatchLogsClient: CloudWatchLogsClient, logGroupNameVal: String?, logStreamNameVal: String?) {
    try {
        val getLogEventsRequest = GetLogEventsRequest {
            logGroupName = logGroupNameVal
            logStreamName = logStreamNameVal
            startFromHead = true
        }

        val eventsList = cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events
        if (eventsList != null) {
            for (list in eventsList) {
                println("Message is: " + list.message)
            }
        }

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cloudWatchLogsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cloudwatch.kotlin.get_logs.main]
