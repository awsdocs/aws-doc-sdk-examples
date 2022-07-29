// snippet-sourcedescription:[ListEvents.kt demonstrates how to list events for a given cluster.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Redshift]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.redshift

// snippet-start:[redshift.kotlin._events.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.DescribeEventsRequest
import aws.sdk.kotlin.services.redshift.model.SourceType
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin._events.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <clusterId> <eventSourceType> 

        Where:
            clusterId - The id of the cluster. 
            eventSourceType - The event type (ie, cluster).
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val clusterId = args[0]
    val eventSourceType = args[1]
    listRedShiftEvents(clusterId, eventSourceType)
}

// snippet-start:[redshift.kotlin._events.main]
suspend fun listRedShiftEvents(clusterId: String?, eventSourceType: String) {

    val request = DescribeEventsRequest {
        sourceIdentifier = clusterId
        sourceType = SourceType.fromValue(eventSourceType)
        startTime = aws.smithy.kotlin.runtime.time.Instant.fromEpochSeconds("1634058260")
        maxRecords = 20
    }

    RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
        val eventsResponse = redshiftClient.describeEvents(request)
        eventsResponse.events?.forEach { event ->
            println("Source type is ${event.sourceType}")
            println("Event message is ${event.message}")
        }
    }
}
// snippet-end:[redshift.kotlin._events.main]
