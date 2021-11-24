//snippet-sourcedescription:[DisableCustomerKey.kt demonstrates how to disable an AWS Key Management Service (AWS KMS) key.]
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

// snippet-start:[kms.kotlin_disable_key.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.DisableKeyRequest
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_disable_key.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <keyId> 
        Where:
            keyId -  a key id value to disable (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val keyId = args[0]
    disableKey(keyId)
    }

// snippet-start:[kms.kotlin_disable_key.main]
suspend fun disableKey(keyIdVal: String?) {

        val request = DisableKeyRequest {
            keyId = keyIdVal
        }

        KmsClient { region = "us-west-2" }.use { kmsClient ->
            kmsClient.disableKey(request)
            println("$keyIdVal was successfully disabled")
        }
}
// snippet-end:[kms.kotlin_disable_key.main]