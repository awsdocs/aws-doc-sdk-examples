// snippet-sourcedescription:[UpdateUser.kt demonstrates how to update the name of an AWS Identity and Access Management (IAM) user.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Identity and Access Management (IAM)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.update_user.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.UpdateUserRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.update_user.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <curName> <newName>
        Where:
            curName - The current user name.
            newName - An updated user name.
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val curName = args[0]
    val newName = args[1]
    updateIAMUser(curName, newName)
}

// snippet-start:[iam.kotlin.update_user.main]
suspend fun updateIAMUser(curName: String?, newName: String?) {

    val request = UpdateUserRequest {
        userName = curName
        newUserName = newName
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.updateUser(request)
        println("Successfully updated user to $newName")
    }
}
// snippet-end:[iam.kotlin.update_user.main]
