//snippet-sourcedescription:[DeleteUser.kt demonstrates how to delete an AWS Identity and Access Management (IAM) user. This is only possible for users with no associated resources.]
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

// snippet-start:[iam.kotlin.delete_user.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DeleteUserRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.delete_user.import]

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
            username - the name of the user to delete. 
        """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val username = args[0]
    deleteIAMUser(username)
    }

// snippet-start:[iam.kotlin.delete_user.main]
suspend fun deleteIAMUser(userNameVal: String) {

    val request = DeleteUserRequest {
        userName= userNameVal
    }

    // To delete a user, ensure that the user's access keys are deleted first.
    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.deleteUser(request)
        println("Successfully deleted user $userNameVal")
    }
}
// snippet-end:[iam.kotlin.delete_user.main]