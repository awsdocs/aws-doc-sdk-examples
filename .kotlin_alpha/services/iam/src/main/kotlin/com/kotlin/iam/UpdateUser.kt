//snippet-sourcedescription:[UpdateUser.kt demonstrates how to update the name of an AWS Identity and Access Management (IAM) user.]
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

// snippet-start:[iam.kotlin.update_user.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.UpdateUserRequest
import aws.sdk.kotlin.services.iam.model.IamException
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.update_user.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <curName> <newName>
        Where:
            curName - the current user name.
            newName - an updated user name.
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val curName = args[0]
    val newName = args[1]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    updateIAMUser(iamClient, curName, newName)
    iamClient.close()
}

// snippet-start:[iam.kotlin.update_user.main]
suspend fun updateIAMUser(iamClient: IamClient, curName: String?, newName: String?) {
    try {
        val request = UpdateUserRequest {
            userName = curName
            newUserName = newName
        }

        iamClient.updateUser(request)
        println("Successfully updated user to $newName")

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
}
// snippet-end:[iam.kotlin.update_user.main]