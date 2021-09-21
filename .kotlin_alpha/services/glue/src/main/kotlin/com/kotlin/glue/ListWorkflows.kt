//snippet-sourcedescription:[ListWorkflows.kt demonstrates how to list workflows.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[6/4/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.list_wfs.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.ListWorkflowsRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.list_wfs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val glueClient= GlueClient{region ="us-east-1"}
    listAllWorkflows(glueClient)
    glueClient.close()
}

//snippet-start:[glue.kotlin.list_wfs.main]
suspend fun listAllWorkflows(glueClient: GlueClient) {
    try {
        val workflowsRequest = ListWorkflowsRequest {
            maxResults =10
        }

        val workflowsResponse = glueClient.listWorkflows(workflowsRequest)
        val workflows = workflowsResponse.workflows

        if (workflows != null) {
            for (workflow in workflows)
                println("Workflow name is: $workflow")
        }

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.list_wfs.main]