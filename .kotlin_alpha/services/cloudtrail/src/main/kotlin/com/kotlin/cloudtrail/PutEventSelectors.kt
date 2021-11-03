// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PutEventSelectors.kt demonstrates how to configure an event selector for your trail.]
// snippet-keyword:[AWS SDK for Kotlin]
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

//snippet-start:[cloudtrail.kotlin._selectors.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.EventSelector
import aws.sdk.kotlin.services.cloudtrail.model.PutEventSelectorsRequest
import aws.sdk.kotlin.services.cloudtrail.model.ReadWriteType
import aws.sdk.kotlin.services.cloudtrail.model.CloudTrailException
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin._selectors.import]

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <trailName>  

    Where:
        trailName - the name of the trail to delete. 
        
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
      }

    val trailName = args[0]
    val cloudTrailClient = CloudTrailClient{ region = "us-east-1" }
    setSelector(cloudTrailClient, trailName)
    cloudTrailClient.close()
  }

//snippet-start:[cloudtrail.kotlin._selectors.main]
suspend fun setSelector(cloudTrailClient: CloudTrailClient, trailNameVal: String?) {
        try {

            val selector = EventSelector {
                readWriteType = ReadWriteType.fromValue("All")
            }

            val selectorsRequest = PutEventSelectorsRequest {
                trailName = trailNameVal
                eventSelectors = listOf(selector)
            }

            cloudTrailClient.putEventSelectors(selectorsRequest)

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[cloudtrail.kotlin._selectors.main]