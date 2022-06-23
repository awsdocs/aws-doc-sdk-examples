// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetEventSelectors.kt demonstrates how to get event selectors for a given trail.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/03/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

//snippet-start:[cloudtrail.kotlin.get_event_selectors.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.GetEventSelectorsRequest
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin.get_event_selectors.import]

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <trailName>  

    Where:
        trailName - the name of the trail. 
      
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val trailName = args[0]
    getSelectors(trailName)
}

    //snippet-start:[cloudtrail.kotlin.get_event_selectors.main]
    suspend  fun getSelectors(trailNameVal: String) {

        val request = GetEventSelectorsRequest {
            trailName =trailNameVal
        }

        CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->

            val response = cloudTrail.getEventSelectors(request)
            response.eventSelectors?.forEach { selector ->
                println("The type is ${selector.readWriteType.toString()}")
            }
        }
 }
//snippet-end:[cloudtrail.kotlin.get_event_selectors.main]