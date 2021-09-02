//snippet-sourcedescription:[CreateCustomerKey.java demonstrates how to create an AWS Key Management Service (AWS KMS) key.]
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

// snippet-start:[kms.kotlin_create_key.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.CreateKeyRequest
import aws.sdk.kotlin.services.kms.model.CustomerMasterKeySpec
import aws.sdk.kotlin.services.kms.model.KeyUsageType
import aws.sdk.kotlin.services.kms.model.KmsException
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_create_key.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val keyDes = "Created by the AWS KMS Kotlin API"
    val keyClient = KmsClient{region="us-west-2"}
    val keyId = createKey(keyClient, keyDes)
    println("The key id is $keyId")
    keyClient.close()
}

// snippet-start:[kms.kotlin_create_key.main]
suspend fun createKey(kmsClient: KmsClient, keyDesc: String?): String? {
        try {
            val keyRequest = CreateKeyRequest {
                description = keyDesc
                customerMasterKeySpec = CustomerMasterKeySpec.SymmetricDefault
                keyUsage = KeyUsageType.fromValue("ENCRYPT_DECRYPT")
            }

            val result = kmsClient.createKey(keyRequest)
            println("Created a customer key with id "+result.keyMetadata?.arn)
            return result.keyMetadata?.keyId

        } catch (ex: KmsException) {
            println(ex.message)
            kmsClient.close()
            exitProcess(0)
        }
    }
// snippet-end:[kms.kotlin_create_key.main]