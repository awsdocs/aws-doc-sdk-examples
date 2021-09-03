//snippet-sourcedescription:[PutObject.kt demonstrates how to upload an object to an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.kotlin.s3_object_upload.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.ByteStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.s3_object_upload.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <bucketName> <objectKey> <objectPath>

    Where:
        bucketName - the Amazon S3 bucket to upload an object into.
        objectKey - the object to upload (for example, book.pdf).
        objectPath - the path where the file is located (for example, C:/AWS/book2.pdf).
    """

   if (args.size != 1) {
       println(usage)
       exitProcess(0)
   }

    val bucketName = args[0]
    val objectKey = args[1]
    val objectPath = args[2]
    val s3Client = S3Client { region = "us-east-1" }
    putS3Object(s3Client, bucketName, objectKey, objectPath)
    s3Client.close()
}

// snippet-start:[s3.kotlin.s3_object_upload.main]
suspend fun putS3Object(
        s3Client: S3Client,
        bucketName: String,
        objectKey: String,
        objectPath: String
    ){

        try {
            val metadataVal = mutableMapOf<String, String>()
            metadataVal["myVal"] = "test"

            val putOb = PutObjectRequest {
                bucket = bucketName
                key = objectKey
                metadata = metadataVal
                this.body = ByteStream.fromBytes(getObjectFile(objectPath))
            }

            val response =s3Client.putObject(putOb)
            println("Tag information is ${response.eTag}")

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
    }

    fun getObjectFile(filePath: String): ByteArray {
        var fileInputStream: FileInputStream? = null
        lateinit var bytesArray: ByteArray
        try {

            val file = File(filePath)
            bytesArray = ByteArray(file.length().toInt())
            fileInputStream = FileInputStream(file)
            fileInputStream.read(bytesArray)

        } catch (e: IOException) {
            println(e.message)
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    println(e.message)
                }
            }
        }
        return bytesArray
    }
// snippet-end:[s3.kotlin.s3_object_upload.main]