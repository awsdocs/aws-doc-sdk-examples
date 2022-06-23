//snippet-sourcedescription:[ListEndpointIds.kt demonstrates how to retrieve information about all the endpoints that are associated with a specific user ID.]
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

//snippet-start:[pinpoint.kotlin.list_endpoints.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.GetUserEndpointsRequest
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.list_endpoints.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <applicationId> <userId>

    Where:
        applicationId - The Id value of the Amazon Pinpoint application that has the endpoint.
        userId - The user id applicable to the endpoints.
      """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val applicationId = args[0]
    val userId = args[1]
    listAllEndpoints(applicationId, userId)
    }

suspend fun listAllEndpoints(applicationIdVal: String?, userIdVal: String?) {

    PinpointClient { region = "us-east-1" }.use { pinpoint ->

        val response = pinpoint.getUserEndpoints(GetUserEndpointsRequest {
            userId = userIdVal
            applicationId = applicationIdVal
        })
        response.endpointsResponse?.item?.forEach { endpoint ->
            println("The channel type is ${endpoint.channelType}")
            println("The address is  ${endpoint.address}")
        }
    }
}
