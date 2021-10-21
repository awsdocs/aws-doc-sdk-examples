//snippet-sourcedescription:[ListNotebooks.kt demonstrates how to list notebooks.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/20/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

//snippet-start:[sagemaker.kotlin.list_books.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListNotebookInstancesRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess
//snippet-end:[sagemaker.kotlin.list_books.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val sageMakerClient = SageMakerClient{region = "us-west-2" }
    listBooks(sageMakerClient)
    sageMakerClient.close()
}

//snippet-start:[sagemaker.kotlin.list_books.main]
suspend fun listBooks(sageMakerClient:SageMakerClient) {

    try {
        val notebookInstancesResponse = sageMakerClient.listNotebookInstances(ListNotebookInstancesRequest{})
        val items = notebookInstancesResponse.notebookInstances

        if (items != null) {
            for (item in items) {
                println("The notebook name is: ${item.notebookInstanceName}")
            }
        }

    } catch (e: SageMakerException) {
        println(e.message)
        sageMakerClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sagemaker.kotlin.list_books.main]