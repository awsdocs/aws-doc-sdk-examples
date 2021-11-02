//snippet-sourcedescription:[ListClusters.kt demonstrates how to list clusters.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/19/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.emr

//snippet-start:[emr.kotlin.list_cluster.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.ListClustersRequest
import aws.sdk.kotlin.services.emr.model.EmrException
import kotlin.system.exitProcess
//snippet-end:[emr.kotlin.list_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val emrClient = EmrClient{region = "us-west-2" }
    listAllClusters(emrClient)
}

//snippet-start:[emr.kotlin.list_cluster.main]
suspend fun listAllClusters(emrClient: EmrClient) {
    try {
         val clustersRequest = ListClustersRequest {
        }
        val response = emrClient.listClusters(clustersRequest)
        val clusters = response.clusters

        if (clusters != null) {
            for (cluster in clusters) {
                println("The cluster name is ${cluster.name}")
                println("The cluster ARN is ${cluster.clusterArn}")
            }
        }

    } catch (e: EmrException) {
        println(e.message)
        exitProcess(0)
    }
}
//snippet-end:[emr.kotlin.list_cluster.main]
