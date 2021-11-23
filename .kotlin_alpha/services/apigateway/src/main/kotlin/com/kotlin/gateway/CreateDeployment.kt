//snippet-sourcedescription:[CreateDeployment.kt demonstrates how to create a deployment resource.]
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

// snippet-start:[apigateway.kotlin.create_deployment.import]
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.apigateway.model.CreateDeploymentRequest
import kotlin.system.exitProcess
// snippet-end:[apigateway.kotlin.create_deployment.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <restApiId> <stageName>

        Where:
            restApiId - The string identifier of the associated RestApi. (for example, xxxx99ewyg).
            stageName - The name of the stage. 
        """

   if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val restApiId = args[0]
    val stageName = args[1]
    createNewDeployment(restApiId, stageName)
}

// snippet-start:[apigateway.kotlin.create_deployment.main]
suspend fun createNewDeployment(restApiIdVal: String?, stageNameVal: String?): String? {

        val request = CreateDeploymentRequest {
             restApiId = restApiIdVal
             description = "Created using the AWS API Gateway Kotlin API"
             stageName = stageNameVal
        }

        ApiGatewayClient { region = "us-east-1" }.use { apiGateway ->
          val response = apiGateway.createDeployment(request)
          println("The id of the deployment is " + response.id)
          return response.id
       }
 }
// snippet-end:[apigateway.kotlin.create_deployment.main]