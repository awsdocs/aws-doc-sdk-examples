//snippet-sourcedescription:[CreateUser.kt demonstrates how to create an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/27/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.create_user.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.CreateUserRequest
import aws.sdk.kotlin.services.iam.model.IamException
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.create_user.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <username> 
        Where:
            username - the name of the user to create. 

        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val username = args[0]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    val result = createIAMUser(iamClient, username)
    println("Successfully created user: $result")
    iamClient.close()
}

// snippet-start:[iam.kotlin.create_user.main]
suspend fun createIAMUser(iamClient: IamClient, usernameVal: String?): String? {
    try {

      val request = CreateUserRequest {
          userName = usernameVal
      }

        val response = iamClient.createUser(request)
        return response.user?.userName

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[iam.kotlin.create_user.main]