//snippet-sourcedescription:[DeleteCluster.kt demonstrates how to delete an Amazon Redshift cluster.]
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

// snippet-start:[redshift.kotlin.delete_cluster.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.DeleteClusterRequest
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin.delete_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <clusterId>  

    Where:
        clusterId - the id of the cluster. 
       
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val clusterId = args[0]
    deleteRedshiftCluster(clusterId)
    println("The example is done")
   }

// snippet-start:[redshift.kotlin.delete_cluster.main]
suspend fun deleteRedshiftCluster(clusterId: String?) {

    val request = DeleteClusterRequest {
            clusterIdentifier = clusterId
            skipFinalClusterSnapshot = true
    }

    RedshiftClient { region = "us-west-2" }.use { redshiftClient ->
        val response = redshiftClient.deleteCluster(request)
        println("The status is ${response.cluster?.clusterStatus}")
    }
}
// snippet-end:[redshift.kotlin.delete_cluster.main]