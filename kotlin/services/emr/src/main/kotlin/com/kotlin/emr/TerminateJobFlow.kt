//snippet-sourcedescription:[TerminateJobFlow.kt demonstrates how to terminate a given job flow.]
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

//snippet-start:[erm.kotlin.terminate_job.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.TerminateJobFlowsRequest
import kotlin.system.exitProcess

//snippet-end:[erm.kotlin.terminate_job.import]

/**
To run this Kotlin code example, ensure that you have set up your development environment,
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
          System.out.println(usage)
          exitProcess(1)
     }

    val id = args[0]
    terminateFlow(id)
}

//snippet-start:[erm.kotlin.terminate_job.main]
suspend fun terminateFlow(id: String) {

    val request = TerminateJobFlowsRequest{
        jobFlowIds = listOf(id)
    }

    EmrClient { region = "us-west-2" }.use { emrClient ->
        emrClient.terminateJobFlows(request)
        println("You have successfully terminated $id")
    }
}

//snippet-end:[erm.kotlin.terminate_job.main]