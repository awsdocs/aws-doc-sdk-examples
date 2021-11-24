//snippet-sourcedescription:[DeleteRestApi.kt demonstrates how to delete an existing RestApi resource.]
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

// snippet-start:[apigateway.kotlin.delete_api.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
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
        exitProcess(1)
    }

    val restApiId = args[0]
    deleteAPI(restApiId)
}

// snippet-start:[apigateway.kotlin.delete_api.main]
suspend fun deleteAPI(restApiIdVal: String?) {

    val request = DeleteRestApiRequest {
        restApiId = restApiIdVal
    }

    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        apiGateway.deleteRestApi(request)
        println("The API was successfully deleted")
    }
}
// snippet-end:[apigateway.kotlin.delete_api.main]