// snippet-sourcedescription:[ListSegments.kt demonstrates how to list segments in an Amazon Pinpoint application.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

// snippet-start:[pinpoint.kotlin.listsegments.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.GetSegmentsRequest
import kotlin.system.exitProcess
// snippet-end:[pinpoint.kotlin.listsegments.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appId> 

    Where:
         appId - The Id value of the application that contains segments.
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    listSegs(appId)
}

// snippet-start:[pinpoint.kotlin.listsegments.main]
suspend fun listSegs(appId: String?) {

    PinpointClient { region = "us-west-2" }.use { pinpoint ->

        val response = pinpoint.getSegments(
            GetSegmentsRequest {
                applicationId = appId
            }
        )
        response.segmentsResponse?.item?.forEach { segment ->
            println("Segement id is ${segment.id}")
        }
    }
}
// snippet-end:[pinpoint.kotlin.listsegments.main]
