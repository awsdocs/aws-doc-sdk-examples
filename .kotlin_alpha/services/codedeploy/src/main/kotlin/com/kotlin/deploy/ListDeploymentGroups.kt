//snippet-sourcedescription:[ListDeploymentGroups.kt demonstrates how to list your deployment groups.]
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

// snippet-start:[codedeploy.kotlin._list_groups.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import aws.sdk.kotlin.services.codedeploy.model.ListDeploymentGroupsRequest
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin._list_groups.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <appName> 

    Where:
        appName - the name of the application. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val appName = args[0]
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    listDeployGroups(codeDeployClient, appName)
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin._list_groups.main]
suspend fun listDeployGroups(deployClient: CodeDeployClient, appName: String) {
    try {
        val groupsRequest = ListDeploymentGroupsRequest {
            applicationName = appName
        }

        val response = deployClient.listDeploymentGroups(groupsRequest)
        response.deploymentGroups?.forEach { group ->
            println("The deployment group is: $group")
        }

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin._list_groups.main]