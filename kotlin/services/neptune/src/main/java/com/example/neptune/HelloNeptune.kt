// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune

import aws.sdk.kotlin.services.neptune.NeptuneClient
import aws.sdk.kotlin.services.neptune.model.DescribeDbClustersRequest

// snippet-start:[neptune.kotlin.hello.main]
/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    print("Hello Amazon Neptune")
    listDBClusters()
}

/**
 * List details of DB clusters in Amazon Neptune.
 */
public suspend fun listDBClusters() {
    val request = DescribeDbClustersRequest {
        maxRecords = 20
    }

    NeptuneClient { region = "us-east-1" }.use { neptuneClient ->
        neptuneClient.describeDbClusters(request)
        val response = neptuneClient.describeDbClusters(request)
        response.dbClusters?.forEach { cluster ->
            println("Cluster Identifier: ${cluster.dbClusterIdentifier}")
            println("Status: ${cluster.status}")
            println("Endpoint: ${cluster.endpoint}")
            println("Engine: ${cluster.engine}")
            println("Engine Version: ${cluster.engineVersion}")
            println("---")
        }
    }
}
// snippet-end:[neptune.kotlin.hello.main]
