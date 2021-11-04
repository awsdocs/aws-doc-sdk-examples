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
import aws.sdk.kotlin.services.eventbridge.model.EventBridgeException
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._list_buses.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val eventbridgeClient = EventBridgeClient{region="us-east-1"}
    listBuses(eventbridgeClient)
    eventbridgeClient.close()
}

// snippet-start:[eventbridge.kotlin._list_buses.main]
suspend fun listBuses(eventBrClient: EventBridgeClient) {
    try {

        val busesRequest = ListEventBusesRequest {
            limit = 10
        }

        val response: ListEventBusesResponse = eventBrClient.listEventBuses(busesRequest)
        response.eventBuses?.forEach { bus ->
            println("The name of the event bus is ${bus.name}")
            println("The ARN of the event bus is ${bus.arn}")
        }

    } catch (ex: EventBridgeException) {
        println(ex.message)
        eventBrClient.close()
        exitProcess(0)
    }
}
// snippet-end:[eventbridge.kotlin._list_buses.main]