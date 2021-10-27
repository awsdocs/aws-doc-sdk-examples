//snippet-sourcedescription:[ListPipelineExecutions.kt demonstrates how to all executions for a specific pipeline.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CodePipeline]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/19/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pipeline

// snippet-start:[pipeline.kotlin.list_pipeline_exe.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.CodePipelineException
import aws.sdk.kotlin.services.codepipeline.model.ListPipelineExecutionsRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.list_pipeline_exe.import]


suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <name> 
        Where:
           name - the name of the pipeline to retrieve 
    """


    // if (args.size != 1) {
    //     println(usage)
    //     exitProcess(1)
    // }

    val name =  "MyFirstPipeline" //args[0]
    val  pipelineClient = CodePipelineClient{region = "us-east-1"}
    listExecutions(pipelineClient, name)
    pipelineClient.close()
}

// snippet-start:[pipeline.kotlin.list_pipeline_exe.main]
suspend fun listExecutions(pipelineClient: CodePipelineClient, name: String?) {
    try {
        val executionsRequest = ListPipelineExecutionsRequest {
            maxResults = 10
            pipelineName = name
        }

        val response = pipelineClient.listPipelineExecutions(executionsRequest)
        val executionSummaryList = response.pipelineExecutionSummaries

        if (executionSummaryList != null) {
            for (exe in executionSummaryList) {
                println("The pipeline execution id is ${exe.pipelineExecutionId}")
                println("The execution status is ${exe.status}")
            }
        }

    } catch (e: CodePipelineException) {
        println(e.message)
        pipelineClient.close()
        exitProcess(0)
    }
}
// snippet-end:[pipeline.kotlin.list_pipeline_exe.main]