// snippet-sourcedescription:[ListAccountAliases.kt demonstrates how to list all aliases associated with an AWS account.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Identity and Access Management (IAM)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.list_account_aliases.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.ListAccountAliasesRequest
// snippet-end:[iam.kotlin.list_account_aliases.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAliases()
}

// snippet-start:[iam.kotlin.list_account_aliases.main]
suspend fun listAliases() {

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.listAccountAliases(ListAccountAliasesRequest {})
        response.accountAliases?.forEach { alias ->
            println("Retrieved account alias $alias")
        }
    }
}
// snippet-end:[iam.kotlin.list_account_aliases.main]
