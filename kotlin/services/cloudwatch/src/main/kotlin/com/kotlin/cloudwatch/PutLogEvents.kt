// snippet-sourcedescription:[PutEvents.kt demonstrates how to put a sample CloudWatch event.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_log_events.import]
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeLogStreamsRequest
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeLogStreamsResponse
import aws.sdk.kotlin.services.cloudwatchlogs.model.InputLogEvent
import aws.sdk.kotlin.services.cloudwatchlogs.model.PutLogEventsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_log_events.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <streamName> <logGroup>

    Where:
        streamName - A stream name.
        logGroup - A log group name (testgroup).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val streamName = args[0]
    val logGroup = args[1]
    putCWLogEvents(logGroup, streamName)
}

// snippet-start:[cloudwatch.kotlin.put_log_events.main]
suspend fun putCWLogEvents(logGroupNameVal: String, streamNameVal: String) {

    lateinit var describeLogStreamsResponse: DescribeLogStreamsResponse
    val request = DescribeLogStreamsRequest {
        logGroupName = logGroupNameVal
        logStreamNamePrefix = streamNameVal
    }

    CloudWatchLogsClient { region = "us-west-2" }.use { logsClient ->
        describeLogStreamsResponse = logsClient.describeLogStreams(request)
        println("Successfully put the CloudWatch log event")

        val sequenceTokenVal = describeLogStreamsResponse.logStreams?.get(0)?.uploadSequenceToken
        val inputLogEvent = InputLogEvent {
            message = "{ \"key1\": \"value1\", \"key2\": \"value2\" }"
            timestamp = System.currentTimeMillis()
        }

        val request2 = PutLogEventsRequest {
            logEvents = listOf(inputLogEvent)
            logGroupName = logGroupNameVal
            logStreamName = streamNameVal
            sequenceToken = sequenceTokenVal
        }
        logsClient.putLogEvents(request2)
        println("Successfully put the CloudWatch log event")
    }
}
// snippet-end:[cloudwatch.kotlin.put_log_events.main]
