// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.list_models.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListModelsRequest
// snippet-end:[sagemaker.kotlin.list_models.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    listAllModels()
}

// snippet-start:[sagemaker.kotlin.list_models.main]
suspend fun listAllModels() {
    val request =
        ListModelsRequest {
            maxResults = 15
        }
    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val response = sageMakerClient.listModels(request)
        response.models?.forEach { item ->
            println("Model name is ${item.modelName}")
        }
    }
}
// snippet-end:[sagemaker.kotlin.list_models.main]
