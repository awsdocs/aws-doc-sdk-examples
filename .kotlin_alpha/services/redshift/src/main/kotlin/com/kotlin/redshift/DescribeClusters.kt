//snippet-sourcedescription:[DescribeClusters.kt demonstrates how to describe Amazon Redshift clusters.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.redshift

// snippet-start:[redshift.kotlin.describe_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.DescribeClustersRequest
import aws.sdk.kotlin.services.redshift.model.RedshiftException
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin.describe_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val redshiftClient = RedshiftClient{region="us-west-2"}
    describeRedshiftClusters(redshiftClient)
    redshiftClient.close()
}

// snippet-start:[redshift.kotlin.describe_cluster.main]
suspend fun describeRedshiftClusters(redshiftClient: RedshiftClient) {
    try {
        val clusterResponse= redshiftClient.describeClusters(DescribeClustersRequest{})
        val clusterList = clusterResponse.clusters

        if (clusterList != null) {
            for (cluster in clusterList) {
                println("Cluster database name is ${cluster.dbName}")
                println("Cluster status is ${cluster.clusterStatus}")
            }
        }

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    }
}
// snippet-end:[redshift.kotlin.describe_cluster.main]