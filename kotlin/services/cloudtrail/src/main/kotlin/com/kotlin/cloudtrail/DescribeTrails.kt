// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin.describe_trail.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.DescribeTrailsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudtrail.kotlin.describe_trail.import]

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
    describeSpecificTrails(trailName)
}

// snippet-start:[cloudtrail.kotlin.describe_trail.main]
suspend fun describeSpecificTrails(trailName: String) {
    val request =
        DescribeTrailsRequest {
            trailNameList = listOf(trailName)
        }

    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        val response = cloudTrail.describeTrails(request)
        response.trailList?.forEach { trail ->
            println("The ARN of the trail is ${trail.trailArn}")
        }
    }
}
// snippet-end:[cloudtrail.kotlin.describe_trail.main]
