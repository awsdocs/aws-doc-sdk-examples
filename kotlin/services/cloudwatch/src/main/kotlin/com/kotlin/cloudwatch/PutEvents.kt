// snippet-sourcedescription:[PutEvents.kt demonstrates how to put a sample CloudWatch event.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_events.import]
import aws.sdk.kotlin.services.cloudwatchevents.CloudWatchEventsClient
import aws.sdk.kotlin.services.cloudwatchevents.model.PutEventsRequest
import aws.sdk.kotlin.services.cloudwatchevents.model.PutEventsRequestEntry
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_events.import]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <resourceArn>

    Where:
        resourceArn - An Amazon Resource Name (ARN) related to the events.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val resourceArn = args[0]
    putCWEvents(resourceArn)
}

// snippet-start:[cloudwatch.kotlin.put_events.main]
suspend fun putCWEvents(resourceArn: String) {

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

    CloudWatchEventsClient { region = "us-west-2" }.use { cwe ->
        cwe.putEvents(request)
        println("Successfully put CloudWatch event")
    }
}
// snippet-end:[cloudwatch.kotlin.put_events.main]
