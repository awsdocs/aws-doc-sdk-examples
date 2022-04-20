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

// snippet-start:[appsync.kotlin.get_apis.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.ListGraphqlApisRequest
// snippet-end:[appsync.kotlin.get_apis.import]

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    getApis()
}

// snippet-start:[appsync.kotlin.get_apis.main]
suspend fun getApis() {

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        val response = appClient.listGraphqlApis(ListGraphqlApisRequest {})
        response.graphqlApis?.forEach { graph ->
            println("The name of the graph api is ${graph.name}")
            println("The API Id is ${graph.apiId}")
            println("The API URI is ${graph.uris}")
        }
    }
}
// snippet-end:[appsync.kotlin.get_apis.main]
