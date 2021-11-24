//snippet-sourcedescription:[CreateAndModifyCluster.kt demonstrates how to create and modify an Amazon Redshift cluster.]
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

// snippet-start:[redshift.kotlin.create_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.CreateClusterRequest
import aws.sdk.kotlin.services.redshift.model.DescribeClustersRequest
import aws.sdk.kotlin.services.redshift.model.ModifyClusterRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin.create_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <clusterId> <masterUsername> <masterUserPassword> 

    Where:
        clusterId - the id of the cluster to create. 
        masterUsername - the master user name. 
        masterUserPassword - the password that corresponds to the master user name. 
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val clusterId = args[0]
    val masterUsername = args[1]
    val masterUserPassword = args[2]
    createCluster(clusterId, masterUsername, masterUserPassword)
    waitForClusterReady(clusterId)
    modifyCluster(clusterId)
    println("The example is done")

}

// snippet-start:[redshift.kotlin.create_cluster.main]
suspend fun createCluster(clusterId: String?, masterUsernameVal: String?, masterUserPasswordVal: String?) {

        val clusterRequest = CreateClusterRequest {
            clusterIdentifier = clusterId
            masterUsername = masterUsernameVal // set the user name here
            masterUserPassword = masterUserPasswordVal // set the user password here
            nodeType = "ds2.xlarge"
            publiclyAccessible = true
            numberOfNodes = 2
        }

        RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
          val clusterResponse = redshiftClient.createCluster(clusterRequest)
          println("Created cluster ${clusterResponse.cluster?.clusterIdentifier}")
    }
}

// Waits until the cluster is available.
suspend fun waitForClusterReady( clusterId: String?) {
    var clusterReady = false
    var clusterReadyStr: String
    val sleepTime: Long = 20
    println("Waiting for the cluster to become available.")


    val clustersRequest = DescribeClustersRequest {
            clusterIdentifier = clusterId
     }
    RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
        // Loop until the cluster is ready.
        while (!clusterReady) {
            val clusterResponse = redshiftClient.describeClusters(clustersRequest)
            val clusterList = clusterResponse.clusters

            if (clusterList != null) {
                for (cluster in clusterList) {

                    clusterReadyStr = cluster.clusterStatus.toString()
                    if (clusterReadyStr.contains("available"))
                        clusterReady = true
                    else {
                        print(".")
                        delay(sleepTime * 1000)
                    }
                }
            }
        }
        println("Cluster is available!")
    }
}

suspend fun modifyCluster( clusterId: String?) {
        val modifyClusterRequest = ModifyClusterRequest {
            clusterIdentifier = clusterId
            preferredMaintenanceWindow = "wed:07:30-wed:08:00"
        }

        RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
          val clusterResponse = redshiftClient.modifyCluster(modifyClusterRequest)
          println("The modified cluster was successfully modified and has ${clusterResponse.cluster?.preferredMaintenanceWindow.toString()} as the maintenance window")
    }
}
// snippet-end:[redshift.kotlin.create_cluster.main]