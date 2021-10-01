//snippet-sourcedescription:[CreateAndModifyCluster.kt demonstrates how to create and modify an Amazon Redshift cluster.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/31/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.redshift

// snippet-start:[redshift.kotlin.create_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.CreateClusterRequest
import aws.sdk.kotlin.services.redshift.model.RedshiftException
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

    val redshiftClient = RedshiftClient{region="us-west-2"}
    createCluster( redshiftClient, clusterId, masterUsername, masterUserPassword)
    waitForClusterReady(redshiftClient, clusterId)
    modifyCluster(redshiftClient, clusterId)
    println("The example is done")
    redshiftClient.close()
}

// snippet-start:[redshift.kotlin.create_cluster.main]
suspend fun createCluster(
    redshiftClient: RedshiftClient,
    clusterId: String?,
    masterUsernameVal: String?,
    masterUserPasswordVal: String?
) {
    try {
        val clusterRequest = CreateClusterRequest {
            clusterIdentifier = clusterId
            masterUsername = masterUsernameVal // set the user name here
            masterUserPassword = masterUserPasswordVal // set the user password here
            nodeType = "ds2.xlarge"
            publiclyAccessible = true
            numberOfNodes = 2
        }

        val clusterResponse = redshiftClient.createCluster(clusterRequest)
        println("Created cluster ${clusterResponse.cluster?.clusterIdentifier}")

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    }
}

// Waits until the cluster is available.
suspend fun waitForClusterReady(redshiftClient: RedshiftClient, clusterId: String?) {
    var clusterReady = false
    var clusterReadyStr = ""
    val sleepTime: Long = 20
    println("Waiting for the cluster to become available.")

    try {
        val clustersRequest = DescribeClustersRequest {
            clusterIdentifier = clusterId
        }

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

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    } catch (e: InterruptedException) {
        System.err.println(e.message)
        exitProcess(1)
    }
}

suspend fun modifyCluster(redshiftClient: RedshiftClient, clusterId: String?) {
    try {
        val modifyClusterRequest = ModifyClusterRequest {
            clusterIdentifier = clusterId
            preferredMaintenanceWindow = "wed:07:30-wed:08:00"
        }

        val clusterResponse = redshiftClient.modifyCluster(modifyClusterRequest)
        println("The modified cluster was successfully modified and has ${clusterResponse.cluster?.preferredMaintenanceWindow.toString()} as the maintenance window")

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    }
}
// snippet-end:[redshift.kotlin.create_cluster.main]
