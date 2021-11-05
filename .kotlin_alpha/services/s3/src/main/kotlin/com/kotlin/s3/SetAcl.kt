//snippet-sourcedescription:[SetAcl.kt demonstrates how to set a new access control list (ACL) for an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.kotlin.set_acl.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Grantee
import aws.sdk.kotlin.services.s3.model.Type
import aws.sdk.kotlin.services.s3.model.Grant
import aws.sdk.kotlin.services.s3.model.Permission
import aws.sdk.kotlin.services.s3.model.Owner
import aws.sdk.kotlin.services.s3.model.AccessControlPolicy
import aws.sdk.kotlin.services.s3.model.PutBucketAclRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.set_acl.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <bucketName> <id>

    Where:
        bucketName - the Amazon S3 bucket to grant permissions on.
        id - the ID of the owner of this bucket (you can get this value from the AWS Management Console under the Amazon S3 Access control list (ACL)).
    """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val bucketName = args[0]
    val id = args[1]
    val s3Client = S3Client { region = "us-east-1" }
    setBucketAcl(s3Client, bucketName, id)
    s3Client.close()
}

// snippet-start:[s3.kotlin.set_acl.main]
suspend fun setBucketAcl(s3Client: S3Client, bucketName: String, idVal: String) {

   try {
        val myGrant = Grantee {
             id = idVal
             type = Type.CanonicalUser
        }

        val ownerGrant = Grant {
            grantee = myGrant
            permission = Permission.FullControl
         }

        val grantList2 = mutableListOf<Grant>()
        grantList2.add(ownerGrant)

        val ownerOb = Owner {
           id = idVal
         }

        val acl = AccessControlPolicy {
            owner = ownerOb
            grants = grantList2
        }

        val putAclReq = PutBucketAclRequest {
            bucket = bucketName
            accessControlPolicy = acl
        }

       s3Client.putBucketAcl(putAclReq)
       println("An ACL was successfully set on $bucketName")

   } catch (e: S3Exception) {
       println(e.message)
       s3Client.close()
       exitProcess(0)
   }
}
// snippet-end:[s3.kotlin.set_acl.main]