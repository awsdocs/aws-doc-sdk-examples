// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin.logging.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.StartLoggingRequest
import aws.sdk.kotlin.services.cloudtrail.model.StopLoggingRequest
import kotlin.system.exitProcess
// snippet-end:[cloudtrail.kotlin.logging.import]

suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <trailName>  

    Where:
        trailName - The name of the trail. 
        
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val trailName = args[0]
    startLog(trailName)
    stopLog(trailName)
}

// snippet-start:[cloudtrail.kotlin.logging.main]
suspend fun stopLog(trailName: String) {
    val request =
        StopLoggingRequest {
            name = trailName
        }
    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        cloudTrail.stopLogging(request)
        println("$trailName has stopped logging")
    }
}

suspend fun startLog(trailName: String) {
    val request =
        StartLoggingRequest {
            name = trailName
        }
    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        cloudTrail.startLogging(request)
        println("$trailName has started logging")
    }
}
// snippet-end:[cloudtrail.kotlin.logging.main]
