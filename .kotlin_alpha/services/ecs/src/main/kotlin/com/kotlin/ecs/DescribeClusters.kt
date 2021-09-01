//snippet-sourcedescription:[DescribeClusters.kt demonstrates how to describe a cluster for the Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.des_cluster.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.DescribeClustersRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
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
    val ecsClient = EcsClient{ region = "us-east-1"}
    descCluster(ecsClient, clusterArn)
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.des_cluster.main]
suspend fun descCluster(ecsClient: EcsClient, clusterArn: String) {
    try {
        val clustersRequest = DescribeClustersRequest {
            clusters = listOf(clusterArn)
        }

        val response = ecsClient.describeClusters(clustersRequest)
        val clusters  = response.clusters
        if (clusters != null) {
            for (cluster in clusters) {
                System.out.println("The cluster name is ${cluster.clusterName}.")
            }
        }

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ecs.kotlin.des_cluster.main]