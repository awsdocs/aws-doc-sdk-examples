//snippet-sourcedescription:[ListSegments.kt demonstrates how to list segments in an Amazon Pinpoint application.]
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

//snippet-start:[pinpoint.kotlin.listsegments.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.GetSegmentsRequest
import aws.sdk.kotlin.services.pinpoint.model.PinpointException
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.listsegments.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appId> 

    Where:
         appId - the Id value of the application that contains segments.
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    val pinpointClient = PinpointClient { region = "us-east-1" }
    listSegs(pinpointClient, appId)
    pinpointClient.close()
}

//snippet-start:[pinpoint.kotlin.listsegments.main]
suspend fun listSegs(pinpoint: PinpointClient, appId: String?) {
        try {
            val request = GetSegmentsRequest {
                applicationId = appId
            }

            val response = pinpoint.getSegments(request)
            response.segmentsResponse?.item?.forEach { segment ->
                 println("Segement id is ${segment.id.toString()}")
            }

        } catch (ex: PinpointException) {
            println(ex.message)
            pinpoint.close()
            exitProcess(0)
        }
 }
//snippet-end:[pinpoint.kotlin.listsegments.main]