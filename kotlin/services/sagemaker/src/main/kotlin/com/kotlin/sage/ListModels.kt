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
//snippet-end:[sagemaker.kotlin.list_models.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
    suspend fun main() {

     listAllModels()
    }

    //snippet-start:[sagemaker.kotlin.list_models.main]
    suspend fun listAllModels() {

         val request = ListModelsRequest {
                maxResults = 15
         }
        SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
           val response = sageMakerClient.listModels(request)
           response.models?.forEach { item ->
              println("Model name is ${item.modelName}")
           }
        }
   }
//snippet-end:[sagemaker.kotlin.list_models.main]

