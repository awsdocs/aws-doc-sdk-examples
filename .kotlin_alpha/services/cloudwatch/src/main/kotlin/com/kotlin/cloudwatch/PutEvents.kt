//snippet-sourcedescription:[PutEvents.kt demonstrates how to put a sample CloudWatch event.]
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

// snippet-start:[cloudwatch.kotlin.put_events.import]
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatchevents.CloudWatchEventsClient
import aws.sdk.kotlin.services.cloudwatchevents.model.PutEventsRequest
import aws.sdk.kotlin.services.cloudwatchevents.model.PutEventsRequestEntry
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_events.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <resourceArn>

    Where:
        resourceArn - an Amazon Resource Name (ARN) related to the events.
    """

   if (args.size != 1) {
       println(usage)
       exitProcess(0)
   }

    val resourceArn = args[0]
    val cwEventsClient = CloudWatchEventsClient{region="us-east-1"}
    putCWEvents(cwEventsClient, resourceArn)
}

// snippet-start:[cloudwatch.kotlin.put_events.main]
suspend fun putCWEvents(cwe: CloudWatchEventsClient, resourceArn: String) {
    try {
        val eventDetails = "{ \"key1\": \"value1\", \"key2\": \"value2\" }"

        val requestEntry = PutEventsRequestEntry {
            detail = eventDetails
            detailType = "sampleSubmitted"
            resources = listOf(resourceArn)
            source = "aws-sdk-java-cloudwatch-example"
        }

        val request = PutEventsRequest {
            entries = listOf(requestEntry)
        }

        cwe.putEvents(request)
        println("Successfully put CloudWatch event")

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwe.close()
        exitProcess(0)
    }
}
// snippet-end:[cloudwatch.kotlin.put_events.main]