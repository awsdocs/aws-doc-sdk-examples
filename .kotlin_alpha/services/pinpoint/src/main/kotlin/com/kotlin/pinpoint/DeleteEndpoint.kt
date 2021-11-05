//snippet-sourcedescription:[DeleteEndpoint.kt demonstrates how to delete an endpoint.]
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

//snippet-start:[pinpoint.kotlin.deleteendpoint.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.DeleteEndpointRequest
import aws.sdk.kotlin.services.pinpoint.model.PinpointException
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.deleteendpoint.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */


suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appId> <endpointId>

    Where:
         appId - the id of the application.
         endpointId - the id of the endpoint to delete.
      """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    val endpointId = args[1]
    val pinpointClient = PinpointClient { region = "us-east-1" }
    deletePinEncpoint(pinpointClient, appId, endpointId)
    pinpointClient.close()
}

//snippet-start:[pinpoint.kotlin.deleteendpoint.main]
suspend fun deletePinEncpoint(pinpoint: PinpointClient, appIdVal: String?, endpointIdVal: String?) {
        try {
            val deleteEndpointRequest = DeleteEndpointRequest {
                applicationId =  appIdVal
                endpointId = endpointIdVal
            }

            val result = pinpoint.deleteEndpoint(deleteEndpointRequest)
            val id = result.endpointResponse?.id
            println("The deleted endpoint is  $id")

        } catch (ex: PinpointException) {
            println(ex.message)
            pinpoint.close()
            exitProcess(0)
        }
 }
//snippet-end:[pinpoint.kotlin.deleteendpoint.main]