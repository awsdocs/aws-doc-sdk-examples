//snippet-sourcedescription:[DeleteDeploymentGroup.kt demonstrates how to delete a deployment group.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.deploy

// snippet-start:[codedeploy.kotlin.delete_group.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import aws.sdk.kotlin.services.codedeploy.model.DeleteDeploymentGroupRequest
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin.delete_group.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <appName> <deploymentGroupName>

    Where:
        appName - the name of the application. 
        deploymentGroupName - the name of the deployment group. 
    """

    if (args.size != 2) {
        println(usage)
        System.exit(1)
    }

    val appName = args[0]
    val deploymentGroupName = args[1]
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    delDeploymentGroup(codeDeployClient, appName, deploymentGroupName)
    codeDeployClient.close()
}
// snippet-start:[codedeploy.kotlin.delete_group.import]
suspend fun delDeploymentGroup(
    deployClient: CodeDeployClient,
    appName: String?,
    deploymentGroupNameVal: String
) {
    try {
        val deleteDeploymentGroupRequest = DeleteDeploymentGroupRequest {
            deploymentGroupName = deploymentGroupNameVal
            applicationName = appName
        }

        deployClient.deleteDeploymentGroup(deleteDeploymentGroupRequest)
        println("$deploymentGroupNameVal was deleted!")

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin.delete_group.import]