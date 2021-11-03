//snippet-sourcedescription:[DeleteApplication.kt demonstrates how to delete an application.]
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

// snippet-start:[codedeploy.kotlin.delete_app.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import aws.sdk.kotlin.services.codedeploy.model.DeleteApplicationRequest
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin.delete_app.import]

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
    delApplication(codeDeployClient, appName)
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin.delete_app.main]
suspend fun delApplication(deployClient: CodeDeployClient, appName: String) {
    try {
        val deleteApplicationRequest = DeleteApplicationRequest {
            applicationName = appName
        }

        deployClient.deleteApplication(deleteApplicationRequest)
        println("$appName was deleted!")

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin.delete_app.main]