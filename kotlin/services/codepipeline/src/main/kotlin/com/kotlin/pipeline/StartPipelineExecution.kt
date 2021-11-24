//snippet-sourcedescription:[StartPipelineExecution.kt demonstrates how to execute a pipeline.]
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

// snippet-start:[pipeline.kotlin.start_pipeline.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.StartPipelineExecutionRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.start_pipeline.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

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
    executePipeline(name)
 }

// snippet-start:[pipeline.kotlin.start_pipeline.main]
suspend fun executePipeline( nameVal:String) {

    val request = StartPipelineExecutionRequest {
        name = nameVal
    }
    CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        val response = pipelineClient.startPipelineExecution (request)
        println("Piepline ${response.pipelineExecutionId} was successfully executed")
    }
}
// snippet-end:[pipeline.kotlin.start_pipeline.main]