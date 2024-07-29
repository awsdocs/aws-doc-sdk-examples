// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.emr

// snippet-start:[erm.kotlin.list_cluster.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.ListClustersRequest
// snippet-end:[erm.kotlin.list_cluster.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllClusters()
}

// snippet-start:[erm.kotlin.list_cluster.main]
suspend fun listAllClusters() {
    EmrClient { region = "us-west-2" }.use { emrClient ->
        val response = emrClient.listClusters(ListClustersRequest {})
        response.clusters?.forEach { cluster ->
            println("The cluster name is ${cluster.name}")
            println("The cluster ARN is ${cluster.clusterArn}")
        }
    }
}
// snippet-end:[erm.kotlin.list_cluster.main]
