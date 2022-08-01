// snippet-sourcedescription:[ListKeys.kt demonstrates how to get a list of AWS Key Management Service (AWS KMS) keys.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.kms

// snippet-start:[kms.kotlin_list_keys.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.ListKeysRequest
// snippet-end:[kms.kotlin_list_keys.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllKeys()
}

// snippet-start:[kms.kotlin_list_keys.main]
suspend fun listAllKeys() {

    val request = ListKeysRequest {
        limit = 15
    }

    KmsClient { region = "us-west-2" }.use { kmsClient ->
        val response = kmsClient.listKeys(request)
        response.keys?.forEach { key ->
            println("The key ARN is ${key.keyArn}")
            println("The key Id is ${key.keyId}")
        }
    }
}
// snippet-end:[kms.kotlin_list_keys.main]
