//snippet-sourcedescription:[AddExampleEndpoint.kt demonstrates how to update an existing endpoint.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.add_endpoint.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.EndpointBatchItem
import aws.sdk.kotlin.services.pinpoint.model.EndpointUser
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.EndpointBatchRequest
import aws.sdk.kotlin.services.pinpoint.model.UpdateEndpointsBatchRequest
import aws.sdk.kotlin.services.pinpoint.model.PinpointException
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.add_endpoint.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <appId>
    
        Where:
            appId - the Amazon Pinpoint project/application ID to use. 
       """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
    }

    val appId = args[0]
    val pinpointClient = PinpointClient {region = "us-east-1"}
    updateEndpointsViaBatch(pinpointClient, appId)
    pinpointClient.close()
}

//snippet-start:[pinpoint.kotlin.add_endpoint.main]
 suspend fun updateEndpointsViaBatch(pinpoint: PinpointClient, applicationIdVal: String?) {

        try {

            val myNames = mutableListOf<String>()
            myNames.add("Richard")
            myNames.add("Roe")

            val myMapRichard = mutableMapOf<String, List<String>>()
            myMapRichard.put("name", myNames)

            val richardRoe = EndpointUser {
                userId = "example_user_1"
                userAttributes= myMapRichard
            }

            // Create an EndpointBatchItem object for Richard Roe.
            val richardRoesEmailEndpoint = EndpointBatchItem {
                channelType = ChannelType.Email
                address = "richard_roe@example.com"
                id = "example_endpoint_1"
                attributes = myMapRichard
                user=richardRoe
            }

            val richardList : MutableList<EndpointBatchItem> = ArrayList()
            richardList.add(richardRoesEmailEndpoint)

            // Adds multiple endpoint definitions to a single request object.
            val endpointList = EndpointBatchRequest {
                item = richardList
            }

            // Create the UpdateEndpointsBatchRequest.
            val batchRequest = UpdateEndpointsBatchRequest {
                applicationId= applicationIdVal
                endpointBatchRequest = endpointList
            }

            //  Updates the endpoints with Amazon Pinpoint.
            val result = pinpoint.updateEndpointsBatch(batchRequest)
            println("Update endpoint result ${result.messageBody?.message}")

        } catch (ex: PinpointException) {
            println(ex.message)
            pinpoint.close()
            exitProcess(0)
        }
 }
//snippet-end:[pinpoint.kotlin.add_endpoint.main]