//snippet-sourcedescription:[GetAcl.kt demonstrates how to get the access control list (ACL) of an object located in an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.kotlin.get_acl.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectAclRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.get_acl.import]

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
        bucketName - the Amazon S3 bucket name.
        objectKey - the name of the object from which the ACL is retrieved.
    """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val bucketName = args[0]
    val objectKey = args[1]
    val s3Client = S3Client { region = "us-east-1" }
    getBucketACL(s3Client, objectKey, bucketName)
    s3Client.close()
}
// snippet-start:[s3.kotlin.get_acl.main]
 suspend fun getBucketACL(s3Client: S3Client, objectKey: String, bucketName: String) {

        try {
            val aclReq = GetObjectAclRequest {
                bucket = bucketName
                key = objectKey
            }

            val response = s3Client.getObjectAcl(aclReq)
            response.grants?.forEach { grant ->
               println( "Grant permission is ${grant.permission}")
            }

        } catch (e: S3Exception) {
            println(e.message)
            s3Client.close()
            exitProcess(0)
        }
  }
// snippet-end:[s3.kotlin.get_acl.main]