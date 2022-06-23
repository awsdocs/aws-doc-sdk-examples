//snippet-sourcedescription:[DeletePipeline.kt demonstrates how to delete a pipeline.]
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

// snippet-start:[pipeline.kotlin.delete_pipeline.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.DeletePipelineRequest
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.delete_pipeline.import]


suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <name> 
        Where:
           name - the name of the pipeline to delete. 
   """
     if (args.size != 1) {
         println(usage)
         exitProcess(1)
    }

    val name =  args[0]
    deleteSpecificPipeline(name)
   }

// snippet-start:[pipeline.kotlin.delete_pipeline.main]
suspend fun deleteSpecificPipeline(nameVal: String) {

    val request = DeletePipelineRequest {
        name = nameVal
    }
    CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        pipelineClient.deletePipeline(request)
        println("$nameVal was successfully deleted")
    }
}
// snippet-end:[pipeline.kotlin.delete_pipeline.main]