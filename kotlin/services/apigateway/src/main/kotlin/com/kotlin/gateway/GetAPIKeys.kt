// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.gateway

// snippet-start:[apigateway.kotlin.get_apikeys.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.GetApiKeysRequest
// snippet-end:[apigateway.kotlin.get_apikeys.import]

suspend fun main() {
    getKeys()
}

// snippet-start:[apigateway.kotlin.get_apikeys.main]
suspend fun getKeys() {
    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        val response = apiGateway.getApiKeys(GetApiKeysRequest { })
        response.items?.forEach { key ->
            println("Key is $key")
        }
    }
}
// snippet-end:[apigateway.kotlin.get_apikeys.main]
