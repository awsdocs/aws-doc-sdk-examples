// snippet-sourcedescription:[SetBucketPolicy.kt demonstrates how to add a bucket policy to an existing Amazon Simple Storage Service (Amazon S3) bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon S3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/30/2021]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.set_bucket_policy.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutBucketPolicyRequest
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.set_bucket_policy.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <bucketName> <polFile>

    Where:
        bucketName - The Amazon S3 bucket to set the policy on.
        polFile - A JSON file containing the policy (see the Amazon S3 User Guide for an example).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    val polFile = args[1]
    setPolicy(bucketName, polFile)
}

// snippet-start:[s3.kotlin.set_bucket_policy.main]
suspend fun setPolicy(bucketName: String, polText: String) {

    val policyText = getBucketPolicyFromFile(polText)
    println("Setting policy:")
    println("----")
    println(policyText)
    println("----")
    println("On Amazon S3 bucket $bucketName")

    val request = PutBucketPolicyRequest {
        bucket = bucketName
        policy = policyText
    }

    S3Client { region = "us-east-1" }.use { s3 ->
        s3.putBucketPolicy(request)
        println("Done!")
    }
}

// Loads a JSON-formatted policy from a file.
fun getBucketPolicyFromFile(policyFile: String): String {
    val fileText = StringBuilder()
    try {
        val lines = Files.readAllLines(
            Paths.get(policyFile), Charset.forName("UTF-8")
        )
        for (line in lines) {
            fileText.append(line)
        }
    } catch (e: IOException) {
        println("Problem reading file $policyFile")
        println(e.message)
    }
    return fileText.toString()
}
// snippet-end:[s3.kotlin.set_bucket_policy.main]
