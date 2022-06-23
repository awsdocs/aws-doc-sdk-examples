// snippet-sourcedescription:[SetAcl.kt demonstrates how to set a new access control list (ACL) for an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.kotlin.set_acl.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.AccessControlPolicy
import aws.sdk.kotlin.services.s3.model.Grant
import aws.sdk.kotlin.services.s3.model.Grantee
import aws.sdk.kotlin.services.s3.model.Owner
import aws.sdk.kotlin.services.s3.model.Permission
import aws.sdk.kotlin.services.s3.model.PutBucketAclRequest
import aws.sdk.kotlin.services.s3.model.Type
import kotlin.system.exitProcess
// snippet-end:[s3.kotlin.set_acl.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <bucketName> <id>

    Where:
        bucketName - The Amazon S3 bucket to grant permissions on.
        id - The ID of the owner of this bucket (you can get this value from the AWS Management Console under the Amazon S3 Access control list (ACL)).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    val id = args[1]
    setBucketAcl(bucketName, id)
}

// snippet-start:[s3.kotlin.set_acl.main]
suspend fun setBucketAcl(bucketName: String, idVal: String) {

    val myGrant = Grantee {
        id = idVal
        type = Type.CanonicalUser
    }

    val ownerGrant = Grant {
        grantee = myGrant
        permission = Permission.FullControl
    }

    val grantList = mutableListOf<Grant>()
    grantList.add(ownerGrant)

    val ownerOb = Owner {
        id = idVal
    }

    val acl = AccessControlPolicy {
        owner = ownerOb
        grants = grantList
    }

    val request = PutBucketAclRequest {
        bucket = bucketName
        accessControlPolicy = acl
    }

    S3Client { region = "us-east-1" }.use { s3 ->
        s3.putBucketAcl(request)
        println("An ACL was successfully set on $bucketName")
    }
}
// snippet-end:[s3.kotlin.set_acl.main]
