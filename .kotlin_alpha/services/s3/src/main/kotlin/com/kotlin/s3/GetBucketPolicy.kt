//snippet-sourcedescription:[SetBucketPolicy.kt demonstrates how to add a bucket policy to an existing Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.get_bucket_policy.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetBucketPolicyRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.get_bucket_policy.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
          <bucketName> 

    Where:
        bucketName - the Amazon S3 bucket to get the policy from.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val bucketName =  args[0]
    val s3Client = S3Client { region = "us-east-1" }
    val polText = getPolicy(s3Client, bucketName)
    println("Policy Text: $polText")
    s3Client.close()
}

// snippet-start:[s3.kotlin.get_bucket_policy.main]
suspend fun getPolicy(s3Client: S3Client, bucketName: String): String? {

        println("Getting policy for bucket $bucketName")
        val policyReq = GetBucketPolicyRequest {
            bucket = bucketName
        }

        try {
            val policyRes = s3Client.getBucketPolicy(policyReq)
            return policyRes.policy

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
    }
// snippet-end:[s3.kotlin.get_bucket_policy.main]