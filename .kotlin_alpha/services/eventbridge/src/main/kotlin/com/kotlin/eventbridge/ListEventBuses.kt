// snippet-sourcedescription:[ListEventBuses.kt demonstrates how to list your Amazon EventBridge buses.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[03/04/2021]
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
        val buses = response.eventBuses

        if (buses != null) {
            for (bus in buses) {
                println("The name of the event bus is ${bus.name}")
                println("The ARN of the event bus is ${bus.arn}")
            }
        }

    } catch (ex: EventBridgeException) {
        println(ex.message)
        eventBrClient.close()
        exitProcess(0)
    }
}
// snippet-end:[eventbridge.kotlin._list_buses.main]