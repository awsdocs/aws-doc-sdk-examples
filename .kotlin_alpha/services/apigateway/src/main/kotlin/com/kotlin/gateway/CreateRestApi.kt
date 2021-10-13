//snippet-sourcedescription:[CreateRestApi.kt demonstrates how to create a new RestApi resource.]
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

// snippet-start:[apigateway.kotlin.create_api.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.ApiGatewayException
import aws.sdk.kotlin.services.apigateway.model.CreateRestApiRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.create_api.import]

suspend fun main(args:Array<String>) {

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
    val apiGatewayClient = ApiGatewayClient{region ="us-east-1"}
    createAPI(apiGatewayClient, restApiId)
    apiGatewayClient.close()
}

// snippet-start:[apigateway.kotlin.create_api.main]
suspend fun createAPI(apiGateway: ApiGatewayClient, restApiName: String?): String? {
    try {
        val request = CreateRestApiRequest {
            description = "Created using the Gateway Kotlin API"
            name = restApiName
        }
        val response = apiGateway.createRestApi(request)
        println("The id of the new api is " + response.id)
        return response.id

    } catch (e: ApiGatewayException) {
        println(e.message)
        apiGateway.close()
        exitProcess(0)
    }
}
// snippet-end:[apigateway.kotlin.create_api.main]