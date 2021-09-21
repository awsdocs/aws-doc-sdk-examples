// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateTrail.kt demonstrates how to create a trail.]
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

//snippet-start:[cloudtrail.kotlin.create_trail.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.CreateTrailRequest
import aws.sdk.kotlin.services.cloudtrail.model.CloudTrailException
import kotlin.system.exitProcess
//snippet-end:[cloudtrail.kotlin.create_trail.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <trailName> <s3BucketName> 

    Where:
        trailName - the name of the trail. 
        s3BucketName - the name of the Amazon S3 bucket designated for publishing log files. 
    """

   if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val trailName = args.get(0)
    val s3BucketName = args.get(1)
    val cloudTrailClient = CloudTrailClient{ region = "us-east-1" }
    createNewTrail(cloudTrailClient, trailName, s3BucketName)
    cloudTrailClient.close()
}

    //snippet-start:[cloudtrail.kotlin.create_trail.main]
    suspend fun createNewTrail(cloudTrailClient: CloudTrailClient, trailName: String, s3BucketNameVal: String) {

        try {

            val trailRequest = CreateTrailRequest {
                name =trailName
                s3BucketName =s3BucketNameVal
                isMultiRegionTrail = true
            }

            val trailResponse = cloudTrailClient.createTrail(trailRequest)
            println("The trail ARN is ${trailResponse.trailArn}")

        } catch (ex: CloudTrailException) {
            println(ex.message)
            cloudTrailClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[cloudtrail.kotlin.create_trail.main]
