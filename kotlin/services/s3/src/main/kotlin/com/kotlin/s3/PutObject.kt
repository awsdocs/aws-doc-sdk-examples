// snippet-sourcedescription:[PutObject.kt demonstrates how to upload an object to an Amazon Simple Storage Service (Amazon S3) bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.s3_object_upload.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.s3_object_upload.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <bucketName> <objectKey> <objectPath>

    Where:
        bucketName - The Amazon S3 bucket to upload an object into.
        objectKey - The object to upload (for example, book.pdf).
        objectPath - The path where the file is located (for example, C:/AWS/book2.pdf).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    val objectKey = args[1]
    val objectPath = args[2]
    putS3Object(bucketName, objectKey, objectPath)
}

// snippet-start:[s3.kotlin.s3_object_upload.main]
suspend fun putS3Object(bucketName: String, objectKey: String, objectPath: String) {

    val metadataVal = mutableMapOf<String, String>()
    metadataVal["myVal"] = "test"

    val request = PutObjectRequest {
        bucket = bucketName
        key = objectKey
        metadata = metadataVal
        body = File(objectPath).asByteStream()
    }

    S3Client { region = "us-east-1" }.use { s3 ->
        val response = s3.putObject(request)
        println("Tag information is ${response.eTag}")
    }
}
// snippet-end:[s3.kotlin.s3_object_upload.main]
