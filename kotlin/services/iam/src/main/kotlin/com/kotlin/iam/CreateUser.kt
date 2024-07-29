// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.iam

// snippet-start:[iam.kotlin.create_user.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.CreateUserRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.create_user.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <username> 
        Where:
            username - The name of the user to create. 
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val username = args[0]
    val result = createIAMUser(username)
    println("Successfully created user: $result")
}

// snippet-start:[iam.kotlin.create_user.main]
suspend fun createIAMUser(usernameVal: String?): String? {
    val request =
        CreateUserRequest {
            userName = usernameVal
        }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.createUser(request)
        return response.user?.userName
    }
}
// snippet-end:[iam.kotlin.create_user.main]
