// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.ecs

// snippet-start:[ecs.kotlin.delete_service.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.DeleteServiceRequest
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.delete_service.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <clusterName> <serviceArn> 

    Where:
        clusterName - The name of the ECS cluster.
        serviceArn - The ARN of the ECS service.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val clusterName = args[0]
    val serviceArn = args[1]
    deleteSpecificService(clusterName, serviceArn)
}

// snippet-start:[ecs.kotlin.delete_service.main]
suspend fun deleteSpecificService(
    clusterName: String?,
    serviceArn: String?,
) {
    val request =
        DeleteServiceRequest {
            cluster = clusterName
            service = serviceArn
        }

    EcsClient { region = "us-east-1" }.use { ecsClient ->
        ecsClient.deleteService(request)
        println("The Service was successfully deleted.")
    }
}
// snippet-end:[ecs.kotlin.delete_service.main]
