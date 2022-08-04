// snippet-sourcedescription:[ListObjects.kt demonstrates how to list objects located in a given Amazon Simple Storage Service (Amazon S3) bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.list_objects.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.list_objects.import]

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
        bucketName - The Amazon S3 bucket from which objects are read.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    listBucketObjects(bucketName)
}

// snippet-start:[s3.kotlin.list_objects.main]
suspend fun listBucketObjects(bucketName: String) {

    val request = ListObjectsRequest {
        bucket = bucketName
    }

    S3Client { region = "us-east-1" }.use { s3 ->

        val response = s3.listObjects(request)
        response.contents?.forEach { myObject ->
            println("The name of the key is ${myObject.key}")
            println("The object is ${calKb(myObject.size)} KBs")
            println("The owner is ${myObject.owner}")
        }
    }
}

private fun calKb(intValue: Long): Long {
    return intValue / 1024
}
// snippet-end:[s3.kotlin.list_objects.main]
