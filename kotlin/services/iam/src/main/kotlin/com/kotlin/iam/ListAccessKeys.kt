// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.iam

// snippet-start:[iam.kotlin.list_access_keys.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.ListAccessKeysRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.list_access_keys.import]

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
             username - The name of the user for which access keys are retrieved. 
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val userName = args[0]
    listKeys(userName)
}

// snippet-start:[iam.kotlin.list_access_keys.main]
suspend fun listKeys(userNameVal: String?) {
    val request =
        ListAccessKeysRequest {
            userName = userNameVal
        }
    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.listAccessKeys(request)
        response.accessKeyMetadata?.forEach { md ->
            println("Retrieved access key ${md.accessKeyId}")
        }
    }
}
// snippet-end:[iam.kotlin.list_access_keys.main]
