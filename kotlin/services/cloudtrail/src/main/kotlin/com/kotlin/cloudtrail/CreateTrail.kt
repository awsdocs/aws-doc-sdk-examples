// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateTrail.kt demonstrates how to create a trail.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudTrail]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin.create_trail.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.CreateTrailRequest
import kotlin.system.exitProcess
// snippet-end:[cloudtrail.kotlin.create_trail.import]

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <trailName> <s3BucketName> 

    Where:
        trailName - The name of the trail. 
        s3BucketName - The name of the Amazon S3 bucket designated for publishing log files. 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }
    val trailName = args[0]
    val s3BucketName = args[1]
    createNewTrail(trailName, s3BucketName)
}

// snippet-start:[cloudtrail.kotlin.create_trail.main]
suspend fun createNewTrail(trailName: String, s3BucketNameVal: String) {

    val request = CreateTrailRequest {
        name = trailName
        s3BucketName = s3BucketNameVal
        isMultiRegionTrail = true
    }

    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        val trailResponse = cloudTrail.createTrail(request)
        println("The trail ARN is ${trailResponse.trailArn}")
    }
}
// snippet-end:[cloudtrail.kotlin.create_trail.main]
