// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteTrail.kt demonstrates how to delete a trail.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin.delete_trail.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.DeleteTrailRequest
import kotlin.system.exitProcess
// snippet-end:[cloudtrail.kotlin.delete_trail.import]

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <trailName>  

    Where:
        trailName - The name of the trail to delete. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val trailName = args[0]
    deleteSpecificTrail(trailName)
}

// snippet-start:[cloudtrail.kotlin.delete_trail.main]
suspend fun deleteSpecificTrail(trailName: String) {

    val request = DeleteTrailRequest {
        name = trailName
    }

    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        cloudTrail.deleteTrail(request)
        println("$trailName was successfully deleted")
    }
}
// snippet-end:[cloudtrail.kotlin.delete_trail.main]
