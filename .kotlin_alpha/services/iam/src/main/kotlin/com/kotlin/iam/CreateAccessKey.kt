//snippet-sourcedescription:[CreateAccessKey.kt demonstrates how to create an access key for an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.create_access_key.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.CreateAccessKeyRequest
import aws.sdk.kotlin.services.iam.model.IamException
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.create_access_key.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <user> 
        Where:
           user - an AWS IAM user that you can obtain from the AWS Management Console. 

        """

    if (args.size != 1) {
         println(usage)
         exitProcess(0)
    }

    val user = args[0]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    val keyId = createIAMAccessKey(iamClient, user)
    println("The Key Id is $keyId")
    iamClient.close()
}

// snippet-start:[iam.kotlin.create_access_key.main]
suspend  fun createIAMAccessKey(iamClient: IamClient, user: String?): String {
    try {
        val request = CreateAccessKeyRequest {
            userName = user
        }

        val response = iamClient.createAccessKey(request)
        return response.accessKey?.accessKeyId.toString()

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[iam.kotlin.create_access_key.main]