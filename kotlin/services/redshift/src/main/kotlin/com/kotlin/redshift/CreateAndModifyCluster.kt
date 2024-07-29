// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.redshift

// snippet-start:[redshift.kotlin.create_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.CreateClusterRequest
import aws.sdk.kotlin.services.redshift.model.DescribeClustersRequest
import aws.sdk.kotlin.services.redshift.model.ModifyClusterRequest
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin.create_cluster.import]

/**
 Before running this Kotlin code example, set up your development environment,
 including your credentials.

 For more information, see the following documentation topic:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

 This example requires an AWS Secrets Manager secret that contains the database credentials. If you do not create a
 secret, this example will not work. For details, see:
 https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_how-services-use-secrets_RS.html
*/

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <clusterId> <secretName>

    Where:
        clusterId - The id of the cluster to create. 
        secretName - The name of the AWS Secrets Manager secret that contains the database credentials. 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val clusterId = args[0]
    val secretName = args[1]
    val gson = Gson()
    val user = gson.fromJson(getSecretValues(secretName).toString(), User::class.java)
    val username = user.username
    val userPassword = user.password
    createCluster(clusterId, username, userPassword)
    waitForClusterReady(clusterId)
    modifyCluster(clusterId)
    println("The example is done")
}

// snippet-start:[redshift.kotlin.create_cluster.main]
suspend fun createCluster(
    clusterId: String?,
    masterUsernameVal: String?,
    masterUserPasswordVal: String?,
) {
    val clusterRequest =
        CreateClusterRequest {
            clusterIdentifier = clusterId
            masterUsername = masterUsernameVal
            masterUserPassword = masterUserPasswordVal
            nodeType = "ra3.4xlarge"
            publiclyAccessible = true
            numberOfNodes = 2
        }

    RedshiftClient { region = "us-east-1" }.use { redshiftClient ->
        val clusterResponse = redshiftClient.createCluster(clusterRequest)
        println("Created cluster ${clusterResponse.cluster?.clusterIdentifier}")
    }
}
// snippet-end:[redshift.kotlin.create_cluster.main]

// Waits until the cluster is available.
suspend fun waitForClusterReady(clusterId: String?) {
    var clusterReady = false
    var clusterReadyStr: String
    val sleepTime: Long = 20
    println("Waiting for the cluster to become available.")

    val clustersRequest =
        DescribeClustersRequest {
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
                    if (clusterReadyStr.contains("available")) {
                        clusterReady = true
                    } else {
                        print(".")
                        delay(sleepTime * 1000)
                    }
                }
            }
        }
        println("Cluster is available!")
    }
}

// snippet-start:[redshift.kotlin.mod_cluster.main]
suspend fun modifyCluster(clusterId: String?) {
    val modifyClusterRequest =
        ModifyClusterRequest {
            clusterIdentifier = clusterId
            preferredMaintenanceWindow = "wed:07:30-wed:08:00"
        }

    RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
        val clusterResponse = redshiftClient.modifyCluster(modifyClusterRequest)
        println(
            "The modified cluster was successfully modified and has ${clusterResponse.cluster?.preferredMaintenanceWindow} as the maintenance window",
        )
    }
}
// snippet-end:[redshift.kotlin.mod_cluster.main]

suspend fun getSecretValues(secretName: String?): String? {
    val valueRequest =
        GetSecretValueRequest {
            secretId = secretName
        }

    SecretsManagerClient { region = "us-west-2" }.use { secretsClient ->
        val valueResponse = secretsClient.getSecretValue(valueRequest)
        return valueResponse.secretString
    }
}
