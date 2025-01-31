// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.gateway

// snippet-start:[apigateway.kotlin.get_deployments.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.GetDeploymentsRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_deployments.import]

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <restApiId> 

    Where:
        restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val restApiId = args[0]
    getAllDeployments(restApiId)
}

// snippet-start:[apigateway.kotlin.get_deployments.main]
suspend fun getAllDeployments(restApiIdVal: String?) {
    val request = GetDeploymentsRequest {
        restApiId = restApiIdVal
    }

    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        val response = apiGateway.getDeployments(request)
        response.items?.forEach { deployment ->
            println("The deployment id is ${deployment.id}")
            println("The deployment description is ${deployment.description}")
        }
    }
}
// snippet-end:[apigateway.kotlin.get_deployments.main]
