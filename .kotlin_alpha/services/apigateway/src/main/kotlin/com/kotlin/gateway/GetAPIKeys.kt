//snippet-sourcedescription:[GetAPIKeys.kt demonstrates how to obtain information about the current ApiKeys resource.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon API Gateway]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[08/09/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.gateway

// snippet-start:[apigateway.kotlin.get_apikeys.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.ApiGatewayException
import aws.sdk.kotlin.services.apigateway.model.GetApiKeysRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_apikeys.import]

suspend fun main() {

    val apiGatewayClient = ApiGatewayClient{region ="us-east-1"}
    getKeys(apiGatewayClient)
    apiGatewayClient.close()
}

// snippet-start:[apigateway.kotlin.get_apikeys.main]
suspend fun getKeys(apiGateway: ApiGatewayClient) {
    try {
        val response = apiGateway.getApiKeys(GetApiKeysRequest { })
        val keys = response.items
        if (keys != null) {
            for (key in keys) {
                println("key id is ${key.id}")
            }
        }
    } catch (e: ApiGatewayException) {
        println(e.message)
        apiGateway.close()
        exitProcess(0)
    }
}
// snippet-end:[apigateway.kotlin.get_apikeys.main]