//snippet-sourcedescription:[CreateService.kt demonstrates how to create a service for the Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Elastic Container Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ecs

// snippet-start:[ecs.kotlin.create_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.AwsVpcConfiguration
import aws.sdk.kotlin.services.ecs.model.NetworkConfiguration
import aws.sdk.kotlin.services.ecs.model.CreateServiceRequest
import aws.sdk.kotlin.services.ecs.model.LaunchType
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.create_service.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <clusterName> <serviceName> <securityGroups> <subnets> <taskDefinition>

    Where:
        clusterName - the name of the ECS cluster.
        serviceName - the name of the ECS service to create.
        securityGroups - the name of the security group.
        subnets - the name of the subnet.
        taskDefinition - the name of the task definition.
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
    val serviceArn = createNewService(clusterName, serviceName, securityGroups, subnets, taskDefinition )
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