//snippet-sourcedescription:[ListModels.kt demonstrates how to retrieve a list of models.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

//snippet-start:[sagemaker.kotlin.list_models.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListModelsRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess
//snippet-end:[sagemaker.kotlin.list_models.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
    suspend fun main() {

        val sageMakerClient = SageMakerClient{region = "us-west-2" }
        listAllModels(sageMakerClient)
        sageMakerClient.close()
    }

    //snippet-start:[sagemaker.kotlin.list_models.main]
    suspend fun listAllModels(sageMakerClient:SageMakerClient) {

        try {
            val modelsRequest = ListModelsRequest {
                maxResults = 15
            }

            val modelResponse = sageMakerClient.listModels(modelsRequest)
            val items = modelResponse.models
            if (items != null) {
                for (item in items) {
                    println("Model name is ${item.modelName}")
                }
            }

        } catch (e: SageMakerException) {
            println(e.message)
            sageMakerClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[sagemaker.kotlin.list_models.main]

