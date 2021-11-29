//snippet-sourcedescription:[ListPipelines.kt demonstrates how to retrieve all pipelines.]
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

// snippet-start:[pipeline.kotlin.list_pipelines.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.ListPipelinesRequest
// snippet-end:[pipeline.kotlin.list_pipelines.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    getAllPipelines()
}

// snippet-start:[pipeline.kotlin.list_pipelines.main]
suspend  fun getAllPipelines() {

    CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        val response = pipelineClient.listPipelines(ListPipelinesRequest {})
        response.pipelines?.forEach { pipeline ->
            println("The name of the pipeline is ${pipeline.name}")
        }
    }
}
// snippet-end:[pipeline.kotlin.list_pipelines.main]