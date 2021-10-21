//snippet-sourcedescription:[GetDeployments.kt demonstrates how to get information about a deployment collection.]
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

// snippet-start:[apigateway.kotlin.get_deployments.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.ApiGatewayException
import aws.sdk.kotlin.services.apigateway.model.GetDeploymentsRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_deployments.import]

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
    getAllDeployments(apiGatewayClient, restApiId);
    apiGatewayClient.close()
}

// snippet-start:[apigateway.kotlin.get_deployments.main]
suspend fun getAllDeployments(apiGateway: ApiGatewayClient, restApiIdVal: String?) {
    try {
        val request = GetDeploymentsRequest {
            restApiId = restApiIdVal
        }

        val response = apiGateway.getDeployments(request)
        val deployments = response.items
        if (deployments != null) {
            for (deployment in deployments) {
                println("The deployment id is ${deployment.id}")
                println("The deployment description is ${deployment.description}")
            }
        }

    } catch (e: ApiGatewayException) {
        println(e.message)
        apiGateway.close()
        exitProcess(0)
    }
}
// snippet-end:[apigateway.kotlin.get_deployments.main]