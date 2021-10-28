//snippet-sourcedescription:[DeployApplication.kt demonstrates how to deploy an application revision.]
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

// snippet-start:[codedeploy.kotlin._deploy_app.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.S3Location
import aws.sdk.kotlin.services.codedeploy.model.BundleType
import aws.sdk.kotlin.services.codedeploy.model.RevisionLocation
import aws.sdk.kotlin.services.codedeploy.model.CreateDeploymentRequest
import aws.sdk.kotlin.services.codedeploy.model.RevisionLocationType
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin._deploy_app.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <appName> <bucketName> <bundleType> <key> <deploymentGroup> 

    Where:
        appName - the name of the application. 
        bucketName - the name of the Amazon S3 bucket that contains the ZIP to deploy. 
        key - the key located in the S3 bucket (for example, mywebapp.zip). 
        deploymentGroup - the name of the deployment group (for example, group1). 
    """

   if (args.size != 4) {
       println(usage)
       exitProcess(1)
   }

    val appName = args[0]
    val bucketName = args[1]
    val key = args[2]
    val deploymentGroup = args[3]
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    val deploymentId = createAppDeployment( codeDeployClient, appName, bucketName, key, deploymentGroup)
    println("The deployment Id is $deploymentId")
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin._deploy_app.main]
suspend fun createAppDeployment(
    deployClient: CodeDeployClient,
    appNameVal: String,
    bucketNameVal: String,
    keyVal: String,
    deploymentGroupVal: String
): String {

    try {
        val s3LocationOb = S3Location {
            bucket = bucketNameVal
            bundleType = BundleType.Zip
            key = keyVal
        }

        val revisionLocation = RevisionLocation {
            s3Location = s3LocationOb
            revisionType = RevisionLocationType.S3
        }

        val deploymentRequest = CreateDeploymentRequest {
            applicationName = appNameVal
            deploymentGroupName = deploymentGroupVal
            description = "A deployment created by the Kotlin API"
            revision = revisionLocation
        }

        val response = deployClient.createDeployment(deploymentRequest)
        return response.deploymentId.toString()

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin._deploy_app.main]