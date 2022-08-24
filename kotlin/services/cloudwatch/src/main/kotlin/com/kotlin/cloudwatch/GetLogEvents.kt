// snippet-sourcedescription:[GetLogEvents.kt demonstrates how to get log events from Amazon CloudWatch.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.get_logs.import]
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.GetLogEventsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.get_logs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <logGroup> <logStreamName> 

    Where:
        logGroup - A log group name (testgroup).
        logStreamName - The name of the log stream (for example, mystream).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val logGroup = args[0]
    val logStreamName = args[1]
    getCWLogEvents(logGroup, logStreamName)
}

// snippet-start:[cloudwatch.kotlin.get_logs.main]
suspend fun getCWLogEvents(logGroupNameVal: String, logStreamNameVal: String) {

    val request = GetLogEventsRequest {
        logGroupName = logGroupNameVal
        logStreamName = logStreamNameVal
        startFromHead = true
    }

    CloudWatchLogsClient { region = "us-west-2" }.use { cwlClient ->
        val eventsList = cwlClient.getLogEvents(request)
        eventsList.events?.forEach { list ->
            println("Message is: " + list.message)
        }
    }
}
// snippet-end:[cloudwatch.kotlin.get_logs.main]
