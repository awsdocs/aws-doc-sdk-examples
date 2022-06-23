//snippet-sourcedescription:[DescribeCluster.kt demonstrates how to describe a given cluster.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.emr

//snippet-start:[erm.kotlin.describe_cluster.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.DescribeClusterRequest
import kotlin.system.exitProcess
//snippet-end:[erm.kotlin.describe_cluster.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <clusterIdVal> 

        Where:
            clusterIdVal - the id of the cluster to describe. 
        """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val clusterIdVal = args[0]
    describeMyCluster(clusterIdVal )
  }

//snippet-start:[erm.kotlin.describe_cluster.main]
suspend fun describeMyCluster(clusterIdVal: String?) {

     val request = DescribeClusterRequest {
         clusterId = clusterIdVal
     }

     EmrClient { region = "us-west-2" }.use { emrClient ->
        val response = emrClient.describeCluster(request)
        println("The name of the cluster is ${response.cluster?.name}")

     }
}
//snippet-end:[erm.kotlin.describe_cluster.main]