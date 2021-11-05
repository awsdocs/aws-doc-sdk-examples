//snippet-sourcedescription:[ListAliases.kt demonstrates how to get a list of AWS Key Management Service (AWS KMS) aliases.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_list_aliases.import
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.ListAliasesRequest
import aws.sdk.kotlin.services.kms.model.KmsException
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_list_aliases.import

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val keyClient = KmsClient{region="us-west-2"}
    listAllAliases(keyClient)
    keyClient.close()
}

// snippet-start:[kms.kotlin_list_aliases.main
suspend fun listAllAliases(kmsClient: KmsClient) {
        try {
            val aliasesRequest = ListAliasesRequest {
                limit = 15
            }

            val response = kmsClient.listAliases(aliasesRequest)
            response.aliases?.forEach { alias ->
                println("The alias name is ${alias.aliasName}")
            }

        } catch (ex: KmsException) {
            println(ex.message)
            kmsClient.close()
            exitProcess(0)
        }
 }
// snippet-end:[kms.kotlin_list_aliases.main
