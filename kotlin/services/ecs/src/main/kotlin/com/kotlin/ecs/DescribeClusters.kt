//snippet-sourcedescription:[DescribeClusters.kt demonstrates how to describe a cluster for the Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.des_cluster.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.DescribeClustersRequest
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.des_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
    Usage:
        <clusterArn> 

    Where:
        clusterArn - the ARN of the ECS cluster.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val clusterArn = args[0]
    descCluster(clusterArn)
    }

// snippet-start:[ecs.kotlin.des_cluster.main]
suspend fun descCluster(clusterArn: String) {

    val request = DescribeClustersRequest {
        clusters = listOf(clusterArn)
    }

    EcsClient {region = "us-east-1" }.use { ecsClient ->
        val response = ecsClient.describeClusters(request)
        response.clusters?.forEach { cluster ->
            println("The cluster name is ${cluster.clusterName}.")
        }
    }
}
// snippet-end:[ecs.kotlin.des_cluster.main]