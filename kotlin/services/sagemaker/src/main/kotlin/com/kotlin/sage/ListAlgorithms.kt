// snippet-sourcedescription:[ListAlgorithms.kt demonstrates how to list algorithms.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.list_algs.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListAlgorithmsRequest
// snippet-end:[sagemaker.kotlin.list_algs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    listAlgs()
}

// snippet-start:[sagemaker.kotlin.list_algs.main]
suspend fun listAlgs() {

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val response = sageMakerClient.listAlgorithms(ListAlgorithmsRequest {})
        response.algorithmSummaryList?.forEach { item ->
            println("Algorithm name is ${item.algorithmName}")
        }
    }
}
// snippet-end:[sagemaker.kotlin.list_algs.main]
