//snippet-sourcedescription:[GetPipeline.kt demonstrates how to retrieve a specific pipeline.]
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

// snippet-start:[pipeline.kotlin.get_pipeline.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.CodePipelineException
import aws.sdk.kotlin.services.codepipeline.model.GetPipelineRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.get_pipeline.import]

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
           name - the name of the pipeline to retrieve 
    """


   // if (args.size != 1) {
   //     println(usage)
   //     exitProcess(1)
   // }

    val name =  "MyFirstPipeline" //args[0]
    val  pipelineClient = CodePipelineClient{region = "us-east-1"}
    getSpecificPipeline(pipelineClient, name)
    pipelineClient.close()
}

// snippet-start:[pipeline.kotlin.get_pipeline.main]
suspend  fun getSpecificPipeline(pipelineClient: CodePipelineClient, nameVal: String?) {
    try {
        val pipelineRequest = GetPipelineRequest {
            name = nameVal
            version = 1
        }

        val response = pipelineClient.getPipeline(pipelineRequest)
        val stages  = response.pipeline?.stages
        if (stages != null) {
            for (stage in stages) {
                println("Stage name is " + stage.name.toString() + " and actions are:")

                //Get the stage actions
                val actions = stage.actions
                if (actions != null) {
                    for (action in actions) {
                        println("Action name is " + action.name)
                        println("Action type id is " + action.actionTypeId)
                    }
                }
            }
        }

    } catch (e: CodePipelineException) {
        println(e.message)
        pipelineClient.close()
        exitProcess(0)
    }
}
// snippet-end:[pipeline.kotlin.get_pipeline.main]