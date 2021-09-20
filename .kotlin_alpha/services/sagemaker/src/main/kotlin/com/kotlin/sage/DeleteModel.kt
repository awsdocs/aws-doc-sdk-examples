//snippet-sourcedescription:[DeleteModel.kt demonstrates how to delete a model in Amazon SageMaker.]
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

import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.DeleteModelRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>) {

    val USAGE = """
    Usage:
        <modelName>

    Where:
        modelName - The name of the model.
    """

    if (args.size != 1) {
        println(USAGE)
        exitProcess(1)
    }

    val modelName = args[0]
    val sageMakerClient = SageMakerClient{region = "us-west-2" }
    deleteSagemakerModel(sageMakerClient, modelName)
    sageMakerClient.close()
}

suspend fun deleteSagemakerModel(sageMakerClient: SageMakerClient, modelNameVal: String?) {

    try {
        val deleteModelRequest = DeleteModelRequest {
            modelName = modelNameVal
        }
        sageMakerClient.deleteModel(deleteModelRequest)

    } catch (e: SageMakerException) {
        println(e.message)
        sageMakerClient.close()
        exitProcess(0)
    }
}