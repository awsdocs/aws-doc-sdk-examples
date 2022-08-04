// snippet-sourcedescription:[DeleteModel.kt demonstrates how to delete a model in Amazon SageMaker.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.delete_model.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.DeleteModelRequest
import kotlin.system.exitProcess
// snippet-end:[sagemaker.kotlin.delete_model.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <modelName>

    Where:
        modelName - The name of the model.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val modelName = args[0]
    deleteSagemakerModel(modelName)
}

// snippet-start:[sagemaker.kotlin.delete_model.main]
suspend fun deleteSagemakerModel(modelNameVal: String?) {

    val request = DeleteModelRequest {
        modelName = modelNameVal
    }

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        sageMakerClient.deleteModel(request)
    }
}
// snippet-end:[sagemaker.kotlin.delete_model.main]
