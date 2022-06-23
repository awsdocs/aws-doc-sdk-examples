//snippet-sourcedescription:[ListPipelineExecutions.kt demonstrates how to all executions for a specific pipeline.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CodePipeline]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pipeline

// snippet-start:[pipeline.kotlin.list_pipeline_exe.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.ListPipelineExecutionsRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.list_pipeline_exe.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <name> 
        Where:
           name - the name of the pipeline.
    """

    if (args.size != 1) {
         println(usage)
         exitProcess(1)
    }

    val name = args[0]
    listExecutions(name)
    }

// snippet-start:[pipeline.kotlin.list_pipeline_exe.main]
suspend fun listExecutions(name: String?) {

     val request = ListPipelineExecutionsRequest {
         maxResults = 10
         pipelineName = name
     }

     CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        val response = pipelineClient.listPipelineExecutions(request)
        response.pipelineExecutionSummaries?.forEach { exe ->
            println("The pipeline execution id is ${exe.pipelineExecutionId}")
            println("The execution status is ${exe.status}")
        }
    }
}
// snippet-end:[pipeline.kotlin.list_pipeline_exe.main]