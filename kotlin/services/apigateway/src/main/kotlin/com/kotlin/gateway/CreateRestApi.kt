// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.gateway

// snippet-start:[apigateway.kotlin.create_api.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.CreateRestApiRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.create_api.import]

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <restApiId> 

    Where:
        restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).
       
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val restApiId = args[0]
    createAPI(restApiId)
}

// snippet-start:[apigateway.kotlin.create_api.main]
suspend fun createAPI(restApiName: String?): String? {
    val request = CreateRestApiRequest {
        description = "Created using the Gateway Kotlin API"
        name = restApiName
    }

    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        val response = apiGateway.createRestApi(request)
        println("The id of the new api is ${response.id}")
        return response.id
    }
}
// snippet-end:[apigateway.kotlin.create_api.main]
