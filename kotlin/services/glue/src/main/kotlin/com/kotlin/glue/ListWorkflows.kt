//snippet-sourcedescription:[ListWorkflows.kt demonstrates how to list workflows.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.list_wfs.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.ListWorkflowsRequest
//snippet-end:[glue.kotlin.list_wfs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllWorkflows()
  }

//snippet-start:[glue.kotlin.list_wfs.main]
suspend fun listAllWorkflows() {

    val request = ListWorkflowsRequest {
        maxResults =10
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
          val response = glueClient.listWorkflows(request)
          response.workflows?.forEach { workflow ->
           println("Workflow name is: $workflow")
        }
    }
}
//snippet-end:[glue.kotlin.list_wfs.main]