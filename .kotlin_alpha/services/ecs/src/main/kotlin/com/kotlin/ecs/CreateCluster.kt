//snippet-sourcedescription:[CreateCluster.kt demonstrates how to create a cluster for the Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.create_cluster.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.ExecuteCommandConfiguration
import aws.sdk.kotlin.services.ecs.model.ExecuteCommandLogging
import aws.sdk.kotlin.services.ecs.model.ClusterConfiguration
import aws.sdk.kotlin.services.ecs.model.CreateClusterRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.create_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
    Usage:
        <clusterName> 

    Where:
        clusterName - the name of the ECS cluster to create.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val clusterName = args[0]
    val ecsClient = EcsClient{ region = "us-east-1"}
    val clusterArn = createGivenCluster(ecsClient, clusterName)
    println("The cluster ARN is $clusterArn")
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.create_cluster.main]
suspend fun createGivenCluster(ecsClient: EcsClient, clusterNameVal: String?): String? {

    try {
        val commandConfiguration = ExecuteCommandConfiguration {
            logging = ExecuteCommandLogging.fromValue("Default")
        }

        val clusterConfiguration = ClusterConfiguration {
            executeCommandConfiguration = commandConfiguration
           }

        val clusterRequest = CreateClusterRequest {
            clusterName = clusterNameVal
            configuration = clusterConfiguration
        }

        val response = ecsClient.createCluster(clusterRequest)
        return response.cluster?.clusterArn

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}

// snippet-end:[ecs.kotlin.create_cluster.main]