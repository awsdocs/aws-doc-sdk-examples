//snippet-sourcedescription:[CreateAlias.kt demonstrates how to create an AWS Key Management Service (AWS KMS) alias.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/03/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_create_alias.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.CreateAliasRequest
import aws.sdk.kotlin.services.kms.model.KmsException
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_create_alias.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <targetKeyId> <aliasName>  
        Where:
            targetKeyId - the key ID or the Amazon Resource Name (ARN) of the KMS key.
            aliasName - an alias name to create (for example, alias/myAlias).
        
         """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val targetKeyId = args[0]
    val aliasName = args[1]
    val keyClient = KmsClient{region="us-west-2"}
    createCustomAlias(keyClient, targetKeyId, aliasName)
    keyClient.close()
}

// snippet-start:[kms.kotlin_create_alias.main]
 suspend fun createCustomAlias(kmsClient: KmsClient, targetKeyIdVal: String?, aliasNameVal: String?) {
        try {

            val aliasRequest = CreateAliasRequest {
                aliasName = aliasNameVal
                targetKeyId = targetKeyIdVal
            }

            kmsClient.createAlias(aliasRequest)
            println("$aliasNameVal was successfully created")

        } catch (ex: KmsException) {
            println(ex.message)
            kmsClient.close()
            exitProcess(0)
        }
}
// snippet-end:[kms.kotlin_create_alias.main]
