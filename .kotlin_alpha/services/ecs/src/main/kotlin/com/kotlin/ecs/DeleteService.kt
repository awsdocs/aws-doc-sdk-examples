//snippet-sourcedescription:[DeleteService.kt demonstrates how to delete a service for the Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.delete_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.DeleteServiceRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.delete_service.import]

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <clusterName> <serviceArn> 

    Where:
        clusterName - the name of the ECS cluster.
        serviceArn - the ARN of the ECS service.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val clusterName = args[0]
    val serviceArn = args[1]
    val ecsClient = EcsClient{ region = "us-east-1"}
    deleteSpecificService(ecsClient, clusterName, serviceArn)
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.delete_service.main]
suspend fun deleteSpecificService(ecsClient: EcsClient, clusterName: String?, serviceArn: String?) {

    try {
        val serviceRequest = DeleteServiceRequest {
             cluster = clusterName
             service = serviceArn
        }

        ecsClient.deleteService(serviceRequest)
        println("The Service was successfully deleted.")

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ecs.kotlin.delete_service.main]