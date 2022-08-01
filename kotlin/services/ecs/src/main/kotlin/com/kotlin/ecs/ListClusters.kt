// snippet-sourcedescription:[ListClusters.kt demonstrates how to list clusters for the Amazon Elastic Container Service (Amazon ECS) service.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Elastic Container Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ecs

// snippet-start:[ecs.kotlin.list_clusters.import]
import aws.sdk.kotlin.services.ecs.EcsClient
import aws.sdk.kotlin.services.ecs.model.ListClustersRequest
// snippet-end:[ecs.kotlin.list_clusters.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllClusters()
}

// snippet-start:[ecs.kotlin.list_clusters.main]
suspend fun listAllClusters() {

    EcsClient { region = "us-east-1" }.use { ecsClient ->
        val response = ecsClient.listClusters(ListClustersRequest {})
        response.clusterArns?.forEach { cluster ->
            println("The cluster arn is $cluster.")
        }
    }
}
// snippet-end:[ecs.kotlin.list_clusters.main]
