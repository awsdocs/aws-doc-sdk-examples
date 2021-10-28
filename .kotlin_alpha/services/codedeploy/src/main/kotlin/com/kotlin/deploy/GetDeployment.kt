//snippet-sourcedescription:[GetDeployment.kt demonstrates how to get information about a deployment.]
//snippet-keyword:[AWS SDK for Kotlin
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2021]
//snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.deploy

// snippet-start:[codedeploy.kotlin._get_deployment.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import aws.sdk.kotlin.services.codedeploy.model.GetDeploymentRequest
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin._get_deployment.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <deploymentId> 

    Where:
       deploymentId - the id of the deployment. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val deploymentId = args[0]
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    getSpecificDeployment(codeDeployClient, deploymentId)
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin._get_deployment.main]
suspend fun getSpecificDeployment(deployClient: CodeDeployClient, deploymentIdVal: String?) {
    try {
        val deploymentRequest = GetDeploymentRequest {
            deploymentId= deploymentIdVal
        }

        val response = deployClient.getDeployment(deploymentRequest)
        println("The application associated with this deployment is ${response.deploymentInfo?.applicationName}")

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin._get_deployment.main]