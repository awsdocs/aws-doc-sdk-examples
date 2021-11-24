// snippet-sourcedescription:[ListEventBuses.kt demonstrates how to list your Amazon EventBridge buses.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.eventbridge

// snippet-start:[eventbridge.kotlin._list_buses.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.ListEventBusesResponse
import aws.sdk.kotlin.services.eventbridge.model.ListEventBusesRequest
// snippet-end:[eventbridge.kotlin._list_buses.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
   listBuses()

}

// snippet-start:[eventbridge.kotlin._list_buses.main]
suspend fun listBuses() {

    val request = ListEventBusesRequest {
        limit = 10
    }

    EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
        val response: ListEventBusesResponse = eventBrClient.listEventBuses(request)
        response.eventBuses?.forEach { bus ->
            println("The name of the event bus is ${bus.name}")
            println("The ARN of the event bus is ${bus.arn}")
        }

    }
}
// snippet-end:[eventbridge.kotlin._list_buses.main]