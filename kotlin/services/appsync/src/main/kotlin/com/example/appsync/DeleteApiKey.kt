// snippet-sourcedescription:[DeleteApiKey.kt demonstrates how to delete a unique key.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS AppSync]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/25/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync

// snippet-start:[appsync.kotlin.del_key.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.DeleteApiKeyRequest
import kotlin.system.exitProcess
// snippet-end:[appsync.kotlin.del_key.import]

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <apiId> <keyId>
        Where:
            apiId - The Id of the API. (You can get this value from the AWS Management Console.)
            keyId - The Id of the key to delete.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    val keyId = args[1]
    deleteKey(keyId, apiId)
}

// snippet-start:[appsync.kotlin.del_key.main]
suspend fun deleteKey(keyIdVal: String?, apiIdVal: String?) {

    val apiKeyRequest = DeleteApiKeyRequest {
        apiId = apiIdVal
        id = keyIdVal
    }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        appClient.deleteApiKey(apiKeyRequest)
        println("$keyIdVal key was deleted.")
    }
}
// snippet-end:[appsync.kotlin.del_key.main]
