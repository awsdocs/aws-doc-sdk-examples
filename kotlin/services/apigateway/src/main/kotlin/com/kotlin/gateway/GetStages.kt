//snippet-sourcedescription:[GetStages.kt demonstrates how to get information about stages.]
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

// snippet-start:[apigateway.kotlin.get_stages.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.GetStagesRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.get_stages.import]

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
    getAllStages(restApiId)
}

// snippet-start:[apigateway.kotlin.get_stages.main]
suspend fun getAllStages(restApiIdVal: String?) {

    val stagesRequest = GetStagesRequest {
        restApiId = restApiIdVal
    }

    ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
        val response = apiGateway.getStages(stagesRequest)
        response.item?.forEach { stage ->
            println("Stage name is ${stage.stageName}")
        }
    }
}
// snippet-end:[apigateway.kotlin.get_stages.main]