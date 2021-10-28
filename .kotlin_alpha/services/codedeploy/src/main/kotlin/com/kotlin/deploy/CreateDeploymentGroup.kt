//snippet-sourcedescription:[CreateDeploymentGroup.kt demonstrates how to create a deployment group.]
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

// snippet-start:[codedeploy.kotlin.create_deployment_group.import]
import aws.sdk.kotlin.services.codedeploy.CodeDeployClient
import aws.sdk.kotlin.services.codedeploy.model.DeploymentStyle
import aws.sdk.kotlin.services.codedeploy.model.DeploymentType
import aws.sdk.kotlin.services.codedeploy.model.DeploymentOption
import aws.sdk.kotlin.services.codedeploy.model.Ec2TagFilter
import aws.sdk.kotlin.services.codedeploy.model.CreateDeploymentGroupRequest
import aws.sdk.kotlin.services.codedeploy.model.Ec2TagFilterType
import aws.sdk.kotlin.services.codedeploy.model.CodeDeployException
import kotlin.system.exitProcess
// snippet-end:[codedeploy.kotlin.create_deployment_group.import]


suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <deploymentGroupName> <appName> <serviceRoleArn> <tagKey> <tagValue> 

    Where:
        deploymentGroupName - the name of the deployment group. 
        appName - the name of the application. 
        serviceRoleArn - a service role Amazon Resource Name (ARN) that allows AWS CodeDeploy to act on the user's behalf.  
        tagKey - the tag filter key (ie, AppName). 
        tagValue - the tag filter value (ie, mywebapp).
    """

    if (args.size != 5) {
        println(usage)
        exitProcess(1)
    }

    val deploymentGroupName = args[0]
    val appName = args[1]
    val serviceRoleArn = args[2]
    val tagKey = args[3]
    val tagValue = args[4]
    val codeDeployClient = CodeDeployClient{region ="us-east-1"}
    val groupId = createNewDeploymentGroup(codeDeployClient, deploymentGroupName, appName, serviceRoleArn, tagKey, tagValue)
    println("The group deployment ID is $groupId")
    codeDeployClient.close()
}

// snippet-start:[codedeploy.kotlin.create_deployment_group.main]
suspend fun createNewDeploymentGroup(
    deployClient: CodeDeployClient,
    deploymentGroupNameVal: String?,
    appNameVal: String?,
    serviceRoleArnVal: String?,
    tagKeyVal: String?,
    tagValueVal: String?
): String? {
    try {

        val style = DeploymentStyle {
            deploymentType = DeploymentType.InPlace
            deploymentOption = DeploymentOption.WithoutTrafficControl
        }

        val tagFilter = Ec2TagFilter {
            key = tagKeyVal
            value = tagValueVal
            type = Ec2TagFilterType.KeyAndValue
        }
        val tags = mutableListOf<Ec2TagFilter>()
        tags.add(tagFilter)

        val groupRequest = CreateDeploymentGroupRequest {
            deploymentGroupName = deploymentGroupNameVal
            applicationName = appNameVal
            serviceRoleArn = serviceRoleArnVal
            deploymentStyle = style
            ec2TagFilters = tags
        }

        val groupResponse = deployClient.createDeploymentGroup(groupRequest)
        return groupResponse.deploymentGroupId

    } catch (e: CodeDeployException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[codedeploy.kotlin.create_deployment_group.main]