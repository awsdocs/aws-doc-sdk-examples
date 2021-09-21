//snippet-sourcedescription:[GetObjectData.kt demonstrates how to read data from an Amazon Simple Storage Service (Amazon S3) object.]
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

// snippet-start:[s3.kotlin.getobjectdata.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.writeToFile
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.getobjectdata.import]

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
        bucketName - the Amazon S3 bucket name.
        objectKey - the key name.
        objectPath - the path where the file is written to.
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
     }

    val bucketName = args[0]
    val objectKey = args[1]
    val objectPath = args[2]
    val s3Client = S3Client { region = "us-east-1" }
    getObjectBytes(s3Client, bucketName, objectKey, objectPath)
    s3Client.close()
}

// snippet-start:[s3.kotlin.getobjectdata.main]
suspend fun getObjectBytes(s3Client: S3Client, bucketName: String, keyName: String, path: String) {
        try {
            val objectRequest = GetObjectRequest {
                key = keyName
                bucket= bucketName
            }

            s3Client.getObject(objectRequest) { resp ->
                    val myFile = File(path)
                    resp.body?.writeToFile(myFile)
                    println("Successfully read $keyName from $bucketName")
            }

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
 }
// snippet-end:[s3.kotlin.getobjectdata.main]