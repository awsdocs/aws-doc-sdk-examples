// snippet-sourcedescription:[CreateAccessKey.kt demonstrates how to create an access key for an AWS Identity and Access Management (IAM) user.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Identity and Access Management (IAM)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.create_access_key.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.CreateAccessKeyRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.create_access_key.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <user> 
        Where:
           user - An AWS IAM user that you can obtain from the AWS Management Console. 

        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val user = args[0]
    val keyId = createIAMAccessKey(user)
    println("The Key Id is $keyId")
}

// snippet-start:[iam.kotlin.create_access_key.main]
suspend fun createIAMAccessKey(user: String?): String {

    val request = CreateAccessKeyRequest {
        userName = user
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.createAccessKey(request)
        return response.accessKey?.accessKeyId.toString()
    }
}
// snippet-end:[iam.kotlin.create_access_key.main]
