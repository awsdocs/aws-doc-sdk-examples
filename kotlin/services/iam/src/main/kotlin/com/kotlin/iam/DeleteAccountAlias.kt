// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.iam

// snippet-start:[iam.kotlin.delete_account_alias.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DeleteAccountAliasRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.delete_account_alias.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <alias> 
        Where:
            alias - The account alias to delete (for example, myawsaccount).  
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val alias = args[0]
    deleteIAMAccountAlias(alias)
}

// snippet-start:[iam.kotlin.delete_account_alias.main]
suspend fun deleteIAMAccountAlias(alias: String) {
    val request =
        DeleteAccountAliasRequest {
            accountAlias = alias
        }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.deleteAccountAlias(request)
        println("Successfully deleted account alias $alias")
    }
}
// snippet-end:[iam.kotlin.delete_account_alias.main]
