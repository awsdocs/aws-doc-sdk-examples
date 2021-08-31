// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteTrail.kt demonstrates how to delete a trail.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/02/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

//snippet-start:[cloudtrail.kotlin.delete_trail.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.DeleteTrailRequest
import aws.sdk.kotlin.services.cloudtrail.model.CloudTrailException
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin.delete_trail.import]

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

    val trailName = args.get(0)
    val cloudTrailClient = CloudTrailClient{ region = "us-east-1" }
    deleteSpecificTrail(cloudTrailClient, trailName)
    cloudTrailClient.close()
    }

    //snippet-start:[cloudtrail.kotlin.delete_trail.main]
   suspend fun deleteSpecificTrail(cloudTrailClient: CloudTrailClient, trailName: String) {

       try {

            val trailRequest = DeleteTrailRequest {
                name = trailName
            }

           cloudTrailClient.deleteTrail(trailRequest)
           println("$trailName was successfully deleted")

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[cloudtrail.kotlin.delete_trail.main]