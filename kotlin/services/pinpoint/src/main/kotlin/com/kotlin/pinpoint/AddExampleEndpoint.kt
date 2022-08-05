// snippet-sourcedescription:[AddExampleEndpoint.kt demonstrates how to update an existing endpoint.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

// snippet-start:[pinpoint.kotlin.add_endpoint.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.EndpointBatchItem
import aws.sdk.kotlin.services.pinpoint.model.EndpointBatchRequest
import aws.sdk.kotlin.services.pinpoint.model.EndpointUser
import aws.sdk.kotlin.services.pinpoint.model.UpdateEndpointsBatchRequest
import kotlin.system.exitProcess
// snippet-end:[pinpoint.kotlin.add_endpoint.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <appId>
    
        Where:
            appId - The Amazon Pinpoint project/application ID to use. 
       """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    updateEndpointsViaBatch(appId)
}

// snippet-start:[pinpoint.kotlin.add_endpoint.main]
suspend fun updateEndpointsViaBatch(applicationIdVal: String?) {

    val myNames = mutableListOf<String>()
    myNames.add("Richard")
    myNames.add("Roe")

    val myMapRichard = mutableMapOf<String, List<String>>()
    myMapRichard.put("name", myNames)

    val richardRoe = EndpointUser {
        userId = "example_user_1"
        userAttributes = myMapRichard
    }

    // Create an EndpointBatchItem object for Richard Roe.
    val richardRoesEmailEndpoint = EndpointBatchItem {
        channelType = ChannelType.Email
        address = "richard_roe@example.com"
        id = "example_endpoint_1"
        attributes = myMapRichard
        user = richardRoe
    }

    val richardList: MutableList<EndpointBatchItem> = ArrayList()
    richardList.add(richardRoesEmailEndpoint)

    // Adds multiple endpoint definitions to a single request object.
    val endpointList = EndpointBatchRequest {
        item = richardList
    }

    //  Updates the endpoints with Amazon Pinpoint.
    PinpointClient { region = "us-west-2" }.use { pinpoint ->
        val result = pinpoint.updateEndpointsBatch(
            UpdateEndpointsBatchRequest {
                applicationId = applicationIdVal
                endpointBatchRequest = endpointList
            }
        )
        println("Update endpoint result ${result.messageBody?.message}")
    }
}
// snippet-end:[pinpoint.kotlin.add_endpoint.main]
