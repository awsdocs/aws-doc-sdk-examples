//snippet-sourcedescription:[LookUpEndpoint.kt demonstrates how to display information about an existing endpoint in Amazon Pinpoint.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.lookup.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.GetEndpointRequest
import aws.sdk.kotlin.services.pinpoint.model.PinpointException
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.lookup.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: <appId> <endpoint>

        Where:
            appId - the Id value of the application.
            endpoint - the Id value of the endpoint.
      """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    val endpoint =  args[1]
    val pinpointClient = PinpointClient { region = "us-east-1" }
    lookupPinpointEndpoint(pinpointClient, appId, endpoint)
    pinpointClient.close()
}

//snippet-start:[pinpoint.kotlin.lookup.main]
suspend fun lookupPinpointEndpoint(pinpoint: PinpointClient, appId: String?, endpoint: String?) {
        try {
            val  getEndpointRequest = GetEndpointRequest {
                applicationId = appId
                endpointId = endpoint
            }

            val result = pinpoint.getEndpoint(getEndpointRequest)
            val endResponse = result.endpointResponse

            // Uses the Google Gson library to pretty print the endpoint JSON.
            val gson: com.google.gson.Gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create()

            val endpointJson: String = gson.toJson(endResponse)
            println(endpointJson)

        } catch (ex: PinpointException) {
            println(ex.message)
            pinpoint.close()
            exitProcess(0)
        }
        println("Done")
   }
//snippet-end:[pinpoint.kotlin.lookup.main]