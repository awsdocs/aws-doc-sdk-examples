//snippet-sourcedescription:[PutEvents.kt demonstrates how to put a sample CloudWatch event.]
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

// snippet-start:[cloudwatch.kotlin.put_log_events.import]
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeLogStreamsRequest
import aws.sdk.kotlin.services.cloudwatchlogs.model.InputLogEvent
import aws.sdk.kotlin.services.cloudwatchlogs.model.PutLogEventsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_log_events.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <streamName> <logGroup>

    Where:
        streamName - a stream name.
        logGroup - a log group name (testgroup).
    """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val streamName = args[0]
    val logGroup = args[1]
    val cwlClient = CloudWatchLogsClient{region="us-west-2"}
    putCWLogEvents(cwlClient, logGroup, streamName)
    cwlClient.close()
}

// snippet-start:[cloudwatch.kotlin.put_log_events.main]
suspend fun putCWLogEvents(logsClient: CloudWatchLogsClient, logGroupNameVal: String?, streamNameVal: String?) {
    try {
        val logStreamRequest = DescribeLogStreamsRequest {
            logGroupName = logGroupNameVal
            logStreamNamePrefix = streamNameVal
        }
        val describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest)
        val sequenceTokenVal = describeLogStreamsResponse.logStreams?.get(0)?.uploadSequenceToken

        // Build an input log message to put to CloudWatch.
        val inputLogEvent = InputLogEvent {
            message  = "{ \"key1\": \"value1\", \"key2\": \"value2\" }"
            timestamp =  System.currentTimeMillis()
        }

        // Specify the request parameters.
        val putLogEventsRequest = PutLogEventsRequest {
            logEvents = listOf(inputLogEvent)
            logGroupName = logGroupNameVal
            logStreamName = streamNameVal
            sequenceToken = sequenceTokenVal
        }

        logsClient.putLogEvents(putLogEventsRequest)
        println("Successfully put the CloudWatch log event")

    } catch (e: CloudWatchException) {
        println(e.message)
        logsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cloudwatch.kotlin.put_log_events.main]