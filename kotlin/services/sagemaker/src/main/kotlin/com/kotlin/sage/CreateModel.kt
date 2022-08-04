// snippet-sourcedescription:[CreateModel.kt demonstrates how to create a model in Amazon SageMaker.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.create_model.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ContainerDefinition
import aws.sdk.kotlin.services.sagemaker.model.ContainerMode
import aws.sdk.kotlin.services.sagemaker.model.CreateModelRequest
import kotlin.system.exitProcess
// snippet-end:[sagemaker.kotlin.create_model.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <dataUrl> <image> <modelName> <executionRoleArn>

    Where:
        dataUrl - The Amazon S3 path where the model artifacts, which result from model training, are stored.
        image - The Amazon EC2 Container Registry (Amazon ECR) path where inference code is stored (for example, xxxxx5047983.dkr.ecr.us-west-2.amazonaws.com/train).
        modelName - The name of the model.
        executionRoleArn - The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role that Amazon SageMaker can assume to access model artifacts (for example, arn:aws:iam::xxxxx5047983:role/service-role/AmazonSageMaker-ExecutionRole-20200627T12xxxx).

    """

    if (args.size != 4) {
        println(usage)
        exitProcess(1)
    }

    val dataUrl = args[0]
    val image = args[1]
    val modelName = args[2]
    val executionRoleArn = args[3]
    createSagemakerModel(dataUrl, image, modelName, executionRoleArn)
}

// snippet-start:[sagemaker.kotlin.create_model.main]
suspend fun createSagemakerModel(
    dataUrl: String,
    imageVal: String,
    modelNameVal: String,
    executionRoleArnVal: String
) {

    val containerDefinition = ContainerDefinition {
        modelDataUrl = dataUrl
        image = imageVal
        mode = ContainerMode.SingleModel
    }

    val request = CreateModelRequest {
        modelName = modelNameVal
        executionRoleArn = executionRoleArnVal
        primaryContainer = containerDefinition
    }

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val response = sageMakerClient.createModel(request)
        println("The ARN of the model is ${response.modelArn}")
    }
}
// snippet-end:[sagemaker.kotlin.create_model.main]
