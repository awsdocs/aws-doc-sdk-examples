// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[StartLogging.java demonstrates how to start and stop logging.]
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

//snippet-start:[cloudtrail.kotlin.logging.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.StartLoggingRequest
import aws.sdk.kotlin.services.cloudtrail.model.StopLoggingRequest
import aws.sdk.kotlin.services.cloudtrail.model.CloudTrailException
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin.logging.import]

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
    val cloudTrailClient = CloudTrailClient{ region = "us-east-1" }
    startLog(cloudTrailClient, trailName)
    stopLog(cloudTrailClient, trailName)
    cloudTrailClient.close()
}

//snippet-start:[cloudtrail.kotlin.logging.main]
 suspend fun stopLog(cloudTrailClient: CloudTrailClient, trailName: String) {

     try {

            val loggingRequest = StopLoggingRequest {
                name = trailName
            }

            cloudTrailClient.stopLogging(loggingRequest)
            println("$trailName has stopped logging")

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
    }

   suspend fun startLog(cloudTrailClient: CloudTrailClient, trailName: String) {

       try {
            val loggingRequest = StartLoggingRequest {
                name = trailName
            }

            cloudTrailClient.startLogging(loggingRequest)
            println("$trailName has started logging")

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[cloudtrail.kotlin.logging.main]