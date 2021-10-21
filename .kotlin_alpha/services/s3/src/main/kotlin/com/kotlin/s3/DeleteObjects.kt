//snippet-sourcedescription:[DeleteObjects.kt demonstrates how to delete an object from an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.kotlin.delete_objects.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ObjectIdentifier
import aws.sdk.kotlin.services.s3.model.Delete
import aws.sdk.kotlin.services.s3.model.DeleteObjectsRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.delete_objects.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <bucketName> <objectKey> 

    Where:
        bucketName - the Amazon S3 bucket name that contains the object (for example, bucket1).
        objectKey - the name of the object to delete (for example, book.pdf).
      """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    val objectKey = args[1]
    val s3Client = S3Client { region = "us-east-1" }
    deleteBucketObjects(s3Client, bucketName, objectKey)
    s3Client.close()
}

// snippet-start:[s3.kotlin.delete_objects.main]
  suspend fun deleteBucketObjects(s3Client: S3Client, bucketName: String, objectName: String) {

        val objectId = ObjectIdentifier{
            key = objectName
        }

        val delOb = Delete{
            objects = listOf(objectId)
        }

        try {
            val deleteObjectsRequest = DeleteObjectsRequest {
                bucket = bucketName
                delete= delOb
            }

            s3Client.deleteObjects(deleteObjectsRequest)
            println("$objectName was deleted from $bucketName")

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
   }
// snippet-end:[s3.kotlin.delete_objects.main]