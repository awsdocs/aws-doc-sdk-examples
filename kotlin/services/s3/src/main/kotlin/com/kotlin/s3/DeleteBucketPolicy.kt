// snippet-sourcedescription:[DeleteBucketPolicy.kt demonstrates how to delete a policy from an Amazon Simple Storage Service (Amazon S3) bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.delete_bucket_policy.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteBucketPolicyRequest
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.delete_bucket_policy.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <bucketName>  

    Where:
        bucketName - The Amazon S3 bucket from which to delete the policy.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    deleteS3BucketPolicy(bucketName)
}

// snippet-start:[s3.kotlin.delete_bucket_policy.main]
suspend fun deleteS3BucketPolicy(bucketName: String?) {

    val request = DeleteBucketPolicyRequest {
        bucket = bucketName
    }

    S3Client { region = "us-east-1" }.use { s3 ->
        s3.deleteBucketPolicy(request)
        println("Done!")
    }
}
// snippet-end:[s3.kotlin.delete_bucket_policy.main]
