// snippet-sourcedescription:[CopyObject.kt demonstrates how to copy an object from one Amazon Simple Storage Service (Amazon S3) bucket to another.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.copy_object.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CopyObjectRequest
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.copy_object.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <objectKey> <fromBucket> <toBucket> 

    Where:
        objectKey - The name of the object (for example, book.pdf).
        fromBucket - The Amazon S3 bucket name that contains the object (for example, bucket1).
        toBucket - The Amazon S3 bucket to copy the object to (for example, bucket2).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val objectKey = args[0]
    val fromBucket = args[1]
    val toBucket = args[2]
    copyBucketObject(fromBucket, objectKey, toBucket)
}

// snippet-start:[s3.kotlin.copy_object.main]
suspend fun copyBucketObject(
    fromBucket: String,
    objectKey: String,
    toBucket: String
) {

    var encodedUrl = ""
    try {
        encodedUrl = URLEncoder.encode("$fromBucket/$objectKey", StandardCharsets.UTF_8.toString())
    } catch (e: UnsupportedEncodingException) {
        println("URL could not be encoded: " + e.message)
    }

    val request = CopyObjectRequest {
        copySource = encodedUrl
        bucket = toBucket
        key = objectKey
    }
    S3Client { region = "us-east-1" }.use { s3 ->
        s3.copyObject(request)
    }
}
// snippet-end:[s3.kotlin.copy_object.main]
