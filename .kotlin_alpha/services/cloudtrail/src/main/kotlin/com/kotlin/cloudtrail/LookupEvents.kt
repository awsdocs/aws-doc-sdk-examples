// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LookupEvents.kt demonstrates how to look up Cloud Trail events.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/03/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

//snippet-start:[cloudtrail.kotlin.events.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.LookupEventsRequest
import aws.sdk.kotlin.services.cloudtrail.model.CloudTrailException
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin.events.import]

suspend fun main() {

    val cloudTrailClient = CloudTrailClient{ region = "us-east-1" }
    lookupAllEvents(cloudTrailClient)
    cloudTrailClient.close()
}

//snippet-start:[cloudtrail.kotlin.events.main]
suspend fun lookupAllEvents(cloudTrailClient: CloudTrailClient) {
        try {

            val eventsRequest = LookupEventsRequest {
                maxResults =20
            }

            val response = cloudTrailClient.lookupEvents(eventsRequest)
            response.events?.forEach { event ->
                println("Event name is ${event.eventName}")
                println("The event source is ${event.eventSource}")
            }

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
  }
//snippet-end:[cloudtrail.kotlin.events.main]