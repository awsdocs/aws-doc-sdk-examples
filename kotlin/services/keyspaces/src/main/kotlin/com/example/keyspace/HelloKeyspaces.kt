// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[HelloKeyspaces.kt demonstrates how to display all current Amazon Keyspaces names and Amazon Resource Names (ARNs).]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Keyspaces]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.keyspace

import aws.sdk.kotlin.services.keyspaces.KeyspacesClient
import aws.sdk.kotlin.services.keyspaces.model.ListKeyspacesRequest

// snippet-start:[keyspace.kotlin.hello.main]
/**
Before running this Kotlin code example, set up your development environment, including your credentials.

For more information, see the following documentation topic:

https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main() {
    listKeyspaces()
}

suspend fun listKeyspaces() {
    val keyspacesRequest = ListKeyspacesRequest {
        maxResults = 10
    }

    KeyspacesClient { region = "us-east-1" }.use { keyClient ->
        val response = keyClient.listKeyspaces(keyspacesRequest)
        response.keyspaces?.forEach { keyspace ->
            println("The name of the keyspace is ${keyspace.keyspaceName}")
        }
    }
}
// snippet-end:[keyspace.kotlin.hello.main]
