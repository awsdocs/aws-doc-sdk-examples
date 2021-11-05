//snippet-sourcedescription:[ListTaskDefinitions.kt demonstrates how to list task definitions.]
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

// snippet-start:[ecs.kotlin.list_tasks.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.DescribeTasksRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.list_tasks.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
    Usage:
        <clusterArn> <taskId>

    Where:
        clusterArn - the ARN of an ECS cluster..
        taskId - the task Id value.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val clusterArn = args[0]
    val taskId =   args[1]
    val ecsClient = EcsClient{ region = "us-east-1"}
    getAllTasks(ecsClient, clusterArn, taskId)
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.list_tasks.main]
suspend fun getAllTasks(ecsClient: EcsClient, clusterArn: String, taskId: String) {
    try {
        val tasksRequest = DescribeTasksRequest {
             cluster = clusterArn
             tasks = listOf(taskId)
        }

        val response = ecsClient.describeTasks(tasksRequest)
        response.tasks?.forEach { task ->
            println("The task ARN is " + task.taskDefinitionArn)
        }

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ecs.kotlin.list_tasks.main]