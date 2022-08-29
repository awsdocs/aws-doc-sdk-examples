// snippet-sourcedescription:[ListGrants.kt demonstrates how to get information about AWS Key Management Service (AWS KMS) grants related to a key.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_list_grant.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.ListGrantsRequest
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_list_grant.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <keyId> 
        Where:
            keyId - The unique identifier for the KMS key that the grant applies to (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab).
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val keyId = args[0]
    displayGrantIds(keyId)
}

// snippet-start:[kms.kotlin_list_grant.main]
suspend fun displayGrantIds(keyIdVal: String?) {

    val request = ListGrantsRequest {
        keyId = keyIdVal
        limit = 15
    }

    KmsClient { region = "us-west-2" }.use { kmsClient ->
        val response = kmsClient.listGrants(request)
        response.grants?.forEach { grant ->
            println("The grant Id is ${grant.grantId}")
        }
    }
}
// snippet-end:[kms.kotlin_list_grant.main]
