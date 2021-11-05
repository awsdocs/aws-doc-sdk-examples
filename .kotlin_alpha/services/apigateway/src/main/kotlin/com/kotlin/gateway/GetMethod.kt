//snippet-sourcedescription:[GetMethod.kt demonstrates how to describe an existing method resource..]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon API Gateway]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.gateway

// snippet-start:[apigateway.kotlin.get_method.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.ApiGatewayException
import aws.sdk.kotlin.services.apigateway.model.GetMethodRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_method.import]


suspend fun main(args:Array<String>) {

    val USAGE = """
    Usage:
        <restApiId> <resourceId> <httpMethod> 

    Where:
        restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).
        resourceId - The string identifier of an resource. (for example, xxxx99ewyg).
        httpMethod - The HTTP method. (for example, GET).
    """

    if (args.size != 3) {
        println(USAGE)
        exitProcess(0)
    }

    val restApiId = args[0]
    val resourceId = args[1]
    val httpMethod = args[2]
    val apiGatewayClient = ApiGatewayClient{region ="us-east-1"}
    getSpecificMethod(apiGatewayClient, restApiId, resourceId, httpMethod)
    apiGatewayClient.close()
}

// snippet-start:[apigateway.kotlin.get_method.main]
suspend fun getSpecificMethod(
    apiGateway: ApiGatewayClient,
    restApiIdVal: String?,
    resourceIdVal: String?,
    httpMethodVal: String?
) {
    try {
        val methodRequest = GetMethodRequest {
            httpMethod = httpMethodVal
            restApiId = restApiIdVal
            resourceId = resourceIdVal
        }

        val response = apiGateway.getMethod(methodRequest)

        // Retrieve a method response associated with a given HTTP status code.
        val details = response.methodResponses
        if (details != null) {
            for ((key, value) in details)
                println("Key is $key and Value is $value")
        }

    } catch (e: ApiGatewayException) {
        println(e.message)
        apiGateway.close()
        exitProcess(0)
    }
}
// snippet-end:[apigateway.kotlin.get_method.main]