//snippet-sourcedescription:[UpdateService.kt demonstrates how to update the task placement strategies and constraints on an Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Elastic Container Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/20/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ecs

// snippet-start:[ecs.kotlin.update_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.UpdateServiceRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.update_service.import]

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
    val ecsClient = EcsClient{ region = "us-east-1"}
    updateSpecificService(ecsClient, clusterName, serviceArn)
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.update_service.main]
suspend fun updateSpecificService(ecsClient: EcsClient, clusterName: String?, serviceArn: String?) {
    try {
        val serviceRequest = UpdateServiceRequest {
             cluster = clusterName
             service = serviceArn
             desiredCount = 0
        }

        ecsClient.updateService(serviceRequest)
        println("The service was modified")

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ecs.kotlin.update_service.main]