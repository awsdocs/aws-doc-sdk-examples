// snippet-sourcedescription:[GetPipeline.kt demonstrates how to retrieve a specific pipeline.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CodePipeline]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pipeline

// snippet-start:[pipeline.kotlin.get_pipeline.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.GetPipelineRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.get_pipeline.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <name> 
        Where:
           name - the name of the pipeline to retrieve. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val name = args[0]
    getSpecificPipeline(name)
}

// snippet-start:[pipeline.kotlin.get_pipeline.main]
suspend fun getSpecificPipeline(nameVal: String?) {

    val request = GetPipelineRequest {
        name = nameVal
        version = 1
    }
    CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        val response = pipelineClient.getPipeline(request)
        response.pipeline?.stages?.forEach { stage ->
            println("Stage name is " + stage.name.toString() + " and actions are:")

            stage.actions?.forEach { action ->
                println("Action name is ${action.name}")
                println("Action type id is ${action.actionTypeId}")
            }
        }
    }
}
// snippet-end:[pipeline.kotlin.get_pipeline.main]
