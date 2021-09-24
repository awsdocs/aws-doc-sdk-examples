//snippet-sourcedescription:[DeleteRestApi.kt demonstrates how to delete an existing RestApi resource.]
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

// snippet-start:[apigateway.kotlin.delete_api.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.ApiGatewayException
import aws.sdk.kotlin.services.apigateway.model.DeleteRestApiRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.delete_api.import]


suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <restApiId> 

    Where:
        restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).
        
    """

    if (args.size != 1) {
        println(usage)
        System.exit(1)
    }

    val restApiId = args[0]
    val apiGatewayClient = ApiGatewayClient{region ="us-east-1"}
    deleteAPI(apiGatewayClient, restApiId)
    apiGatewayClient.close()
}

// snippet-start:[apigateway.kotlin.delete_api.main]
suspend fun deleteAPI(apiGateway: ApiGatewayClient, restApiIdVal: String?) {
    try {
        val request = DeleteRestApiRequest {
            restApiId = restApiIdVal
        }

        apiGateway.deleteRestApi(request)
        println("The API was successfully deleted")

    } catch (e: ApiGatewayException) {
        println(e.message)
        apiGateway.close()
        exitProcess(0)
    }
}
// snippet-end:[apigateway.kotlin.delete_api.main]