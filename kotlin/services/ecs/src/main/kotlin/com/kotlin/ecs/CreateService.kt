// snippet-sourcedescription:[CreateService.kt demonstrates how to create a service for the Amazon Elastic Container Service (Amazon ECS) service.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Elastic Container Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ecs

// snippet-start:[ecs.kotlin.create_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.AwsVpcConfiguration
import aws.sdk.kotlin.services.ecs.model.CreateServiceRequest
import aws.sdk.kotlin.services.ecs.model.LaunchType
import aws.sdk.kotlin.services.ecs.model.NetworkConfiguration
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.create_service.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <clusterName> <serviceName> <securityGroups> <subnets> <taskDefinition>

    Where:
        clusterName - The name of the ECS cluster.
        serviceName - The name of the ECS service to create.
        securityGroups - The name of the security group.
        subnets - The name of the subnet.
        taskDefinition - The name of the task definition.
    """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val clusterName = args[0]
    val serviceName = args[1]
    val securityGroups = args[2]
    val subnets = args[3]
    val taskDefinition = args[4]
    val serviceArn = createNewService(clusterName, serviceName, securityGroups, subnets, taskDefinition)
    println("The ARN of the service is $serviceArn")
}

// snippet-start:[ecs.kotlin.create_service.main]
suspend fun createNewService(
    clusterNameVal: String,
    serviceNameVal: String,
    securityGroupsVal: String,
    subnetsVal: String,
    taskDefinitionVal: String
): String? {

    val vpcConfiguration = AwsVpcConfiguration {
        securityGroups = listOf(securityGroupsVal)
        subnets = listOf(subnetsVal)
    }

    val configuration = NetworkConfiguration {
        awsvpcConfiguration = vpcConfiguration
    }

    val request = CreateServiceRequest {
        cluster = clusterNameVal
        networkConfiguration = configuration
        desiredCount = 1
        launchType = LaunchType.Fargate
        serviceName = serviceNameVal
        taskDefinition = taskDefinitionVal
    }

    EcsClient { region = "us-east-1" }.use { ecsClient ->
        val response = ecsClient.createService(request)
        return response.service?.serviceArn
    }
}
// snippet-end:[ecs.kotlin.create_service.main]
