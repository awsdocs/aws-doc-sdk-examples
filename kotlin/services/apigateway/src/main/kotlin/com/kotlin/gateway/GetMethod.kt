//snippet-sourcedescription:[GetMethod.kt demonstrates how to describe an existing method resource.]
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
import aws.sdk.kotlin.services.apigateway.model.GetMethodRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_method.import]


suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <restApiId> <resourceId> <httpMethod> 

    Where:
        restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).
        resourceId - The string identifier of an resource. (for example, xxxx99ewyg).
        httpMethod - The HTTP method. (for example, GET).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val restApiId = args[0]
    val resourceId = args[1]
    val httpMethod = args[2]
    getSpecificMethod(restApiId, resourceId, httpMethod)
}

// snippet-start:[apigateway.kotlin.get_method.main]
suspend fun getSpecificMethod(restApiIdVal: String?, resourceIdVal: String?, httpMethodVal: String? ) {

     val methodRequest = GetMethodRequest {
         httpMethod = httpMethodVal
         restApiId = restApiIdVal
         resourceId = resourceIdVal
     }

    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        val response = apiGateway.getMethod(methodRequest)

        // Retrieve a method response associated with a given HTTP status code.
        val details = response.methodResponses
        if (details != null) {
            for ((key, value) in details)
                println("Key is $key and Value is $value")
        }
    }
}
// snippet-end:[apigateway.kotlin.get_method.main]