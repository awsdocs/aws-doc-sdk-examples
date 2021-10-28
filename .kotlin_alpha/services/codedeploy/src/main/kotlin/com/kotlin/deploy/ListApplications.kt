//snippet-sourcedescription:[ListApplications.kt demonstrates how to information about your applications.]
//snippet-keyword:[AWS SDK for Kotlin]
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

// snippet-start:[codedeploy.kotlin._list_apps.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import aws.sdk.kotlin.services.codedeploy.model.ListApplicationsRequest
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin._list_apps.import]

suspend fun main() {
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    listApps(codeDeployClient)
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin._list_apps.main]
suspend fun listApps(deployClient: CodeDeployClient) {
    try {
        val applicationsResponse  = deployClient.listApplications(ListApplicationsRequest { })
        val apps = applicationsResponse.applications
        if (apps != null) {
            for (app in apps) {
                println("The application name is: $app")
            }
        }
    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin._list_apps.main]