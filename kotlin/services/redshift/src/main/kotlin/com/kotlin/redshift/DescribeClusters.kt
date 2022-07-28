// snippet-sourcedescription:[DescribeClusters.kt demonstrates how to describe Amazon Redshift clusters.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Redshift]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.redshift

// snippet-start:[redshift.kotlin.describe_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.DescribeClustersRequest
// snippet-end:[redshift.kotlin.describe_cluster.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    describeRedshiftClusters()
}

// snippet-start:[redshift.kotlin.describe_cluster.main]
suspend fun describeRedshiftClusters() {

    RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
        val clusterResponse = redshiftClient.describeClusters(DescribeClustersRequest {})
        val clusterList = clusterResponse.clusters

        if (clusterList != null) {
            for (cluster in clusterList) {
                println("Cluster database name is ${cluster.dbName}")
                println("Cluster status is ${cluster.clusterStatus}")
            }
        }
    }
}
// snippet-end:[redshift.kotlin.describe_cluster.main]
