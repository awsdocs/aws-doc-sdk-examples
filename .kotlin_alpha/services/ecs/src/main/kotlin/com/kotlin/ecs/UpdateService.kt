//snippet-sourcedescription:[UpdateService.kt demonstrates how to update the task placement strategies and constraints on an Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.update_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.UpdateServiceRequest
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.update_service.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        DeleteService    <clusterName> <serviceArn> 

    Where:
        clusterName - the name of the ECS cluster.
        serviceArn - the ARN of the ECS service to service.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val clusterName = args[0]
    val serviceArn = args[1]
    updateSpecificService(clusterName, serviceArn)
 }

// snippet-start:[ecs.kotlin.update_service.main]
suspend fun updateSpecificService(clusterName: String?, serviceArn: String?) {

    val request = UpdateServiceRequest {
        cluster = clusterName
        service = serviceArn
        desiredCount = 0
    }

    EcsClient { region = "us-east-1" }.use { ecsClient ->
        ecsClient.updateService(request)
        println("The service was modified")
    }
}
// snippet-end:[ecs.kotlin.update_service.main]