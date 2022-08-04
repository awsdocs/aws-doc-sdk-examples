// snippet-sourcedescription:[ListAliases.kt demonstrates how to get a list of AWS Key Management Service (AWS KMS) aliases.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_list_aliases.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.ListAliasesRequest
// snippet-end:[kms.kotlin_list_aliases.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllAliases()
}

// snippet-start:[kms.kotlin_list_aliases.main]
suspend fun listAllAliases() {

    val request = ListAliasesRequest {
        limit = 15
    }

    KmsClient { region = "us-west-2" }.use { kmsClient ->
        val response = kmsClient.listAliases(request)
        response.aliases?.forEach { alias ->
            println("The alias name is ${alias.aliasName}")
        }
    }
}
// snippet-end:[kms.kotlin_list_aliases.main]
