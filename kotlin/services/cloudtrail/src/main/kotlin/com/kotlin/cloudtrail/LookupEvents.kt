// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LookupEvents.kt demonstrates how to look up Cloud Trail events.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin.events.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.LookupEventsRequest
// snippet-end:[cloudtrail.kotlin.events.import]

suspend fun main() {
    lookupAllEvents()
}

// snippet-start:[cloudtrail.kotlin.events.main]
suspend fun lookupAllEvents() {

    val request = LookupEventsRequest {
        maxResults = 20
    }

    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        val response = cloudTrail.lookupEvents(request)
        response.events?.forEach { event ->
            println("Event name is ${event.eventName}")
            println("The event source is ${event.eventSource}")
        }
    }
}
// snippet-end:[cloudtrail.kotlin.events.main]
