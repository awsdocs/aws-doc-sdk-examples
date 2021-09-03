//snippet-sourcedescription:[CopyObject.kt demonstrates how to copy an object from one Amazon Simple Storage Service (Amazon S3) bucket to another.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/06/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.s3

// snippet-start:[s3.kotlin.copy_object.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CopyObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.copy_object.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <objectKey> <fromBucket> <toBucket> 

    Where:
        objectKey - the name of the object (for example, book.pdf).
        fromBucket - the Amazon S3 bucket name that contains the object (for example, bucket1).
        toBucket - the Amazon S3 bucket to copy the object to (for example, bucket2).
    """

    if (args.size != 3) {
       println(usage)
       exitProcess(0)
    }

    val objectKey = args[0]
    val fromBucket = args[1]
    val toBucket = args[2]
    val s3Client = S3Client { region = "us-east-1" }
    val response = copyBucketObject(s3Client, fromBucket, objectKey, toBucket)
    println(response)
    s3Client.close()
}

// snippet-start:[s3.kotlin.copy_object.main]
 suspend fun copyBucketObject(s3Client: S3Client,
                         fromBucket: String,
                         objectKey: String,
                         toBucket: String): String {

        var encodedUrl = ""
        try {
            encodedUrl = URLEncoder.encode("$fromBucket/$objectKey", StandardCharsets.UTF_8.toString())

        } catch (e: UnsupportedEncodingException) {
            println("URL could not be encoded: " + e.message)
        }

        val copyReq = CopyObjectRequest {
            copySource = encodedUrl
            this.bucket = toBucket
            this.key= objectKey
        }

        try {
            val copyRes = s3Client.copyObject(copyReq)
            return copyRes.copyObjectResult.toString()

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
  }
// snippet-end:[s3.kotlin.copy_object.main]