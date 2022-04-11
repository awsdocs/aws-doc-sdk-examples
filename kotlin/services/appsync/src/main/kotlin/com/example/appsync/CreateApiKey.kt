// snippet-sourcedescription:[CreateApiKey.kt demonstrates how to create a unique key.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS AppSync]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[04-01-2022]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync

//snippet-start:[appsync.kotlin.create_key.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.CreateApiKeyRequest
import kotlin.system.exitProcess
//snippet-end:[appsync.kotlin.create_key.import]

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>){

    val usage = """
        Usage:
            <apiId> 
        Where:
            apiId - the id of the API (You can obtain the value from the AWS Management console). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    val key = createKey(apiId)
    println("The Id of the new Key is $key")
}

//snippet-start:[appsync.kotlin.create_key.main]
suspend fun createKey(apiIdVal: String): String? {

    val apiKeyRequest = CreateApiKeyRequest {
        apiId = apiIdVal
        description = "Created using the AWS SDK for Kotlin"
    }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        val response = appClient.createApiKey(apiKeyRequest)
        return response.apiKey?.id
    }
}
//snippet-end:[appsync.kotlin.create_key.main]