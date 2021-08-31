//snippet-sourcedescription:[ListClusters.kt demonstrates how to list clusters for the Amazon Elastic Container Service (Amazon ECS) service.]
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

// snippet-start:[ecs.kotlin.list_clusters.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.ListClustersRequest
import aws.sdk.kotlin.services.ecs.model.EcsException
import kotlin.system.exitProcess
// snippet-end:[ecs.kotlin.list_clusters.import]

suspend fun main(){

    val ecsClient = EcsClient{ region = "us-east-1"}
    listAllClusters(ecsClient)
    ecsClient.close()
}

// snippet-start:[ecs.kotlin.list_clusters.main]
suspend  fun listAllClusters(ecsClient: EcsClient) {
    try {
        val response = ecsClient.listClusters(ListClustersRequest{})
        val clusters = response.clusterArns
        if (clusters != null) {
            for (cluster in clusters) {
                println("The cluster arn is ${cluster}.")
            }
        }

    } catch (ex: EcsException) {
        println(ex.message)
        ecsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ecs.kotlin.list_clusters.main]