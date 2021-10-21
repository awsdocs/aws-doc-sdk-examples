//snippet-sourcedescription:[TerminateJobFlow.kt demonstrates how to terminate a given job flow.]
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

//snippet-start:[emr.kotlin.terminate_job.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.TerminateJobFlowsRequest
import aws.sdk.kotlin.services.emr.model.EmrException
import kotlin.system.exitProcess
//snippet-end:[emr.kotlin.terminate_job.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
          Usage:    
            <id>
    
          Where:
            id - an id of a job flow to shut down. 

        """

     if (args.size != 1) {
          System.out.println(usage);
          System.exit(1);
     }

    val id = args[0]
    val emrClient = EmrClient{region = "us-west-2" }
    terminateFlow(emrClient, id)
}

//snippet-start:[emr.kotlin.terminate_job.main]
suspend fun terminateFlow(emrClient: EmrClient, id: String) {
    try {
        val jobFlowsRequest = TerminateJobFlowsRequest{
            jobFlowIds = listOf(id)
        }

        emrClient.terminateJobFlows(jobFlowsRequest)
        println("You have successfully terminated $id")

    } catch (e: EmrException) {
        println(e.message)
        exitProcess(0)
    }
}

//snippet-end:[emr.kotlin.terminate_job.main]
