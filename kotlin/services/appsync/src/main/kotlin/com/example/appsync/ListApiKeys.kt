// snippet-sourcedescription:[ListApiKeys.kt demonstrates how to get API keys.]
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

// snippet-start:[appsync.kotlin.get_keys.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.ListApiKeysRequest
import kotlin.system.exitProcess
// snippet-end:[appsync.kotlin.get_keys.import]

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
            <apiId> 

        Where:
             apiId - The id of the API (You can obtain the value from the AWS Management console). 
    """

    if (args.size != 1) {
        System.out.println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    getKeys(apiId)
}

// snippet-start:[appsync.kotlin.get_keys.main]
suspend fun getKeys(apiIdVal: String?) {

    val request = ListApiKeysRequest {
        apiId = apiIdVal
    }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        val response = appClient.listApiKeys(request)
        response.apiKeys?.forEach { key ->
            println("The key Id is ${key.id}")
        }
    }
}
// snippet-end:[appsync.kotlin.get_keys.main]
