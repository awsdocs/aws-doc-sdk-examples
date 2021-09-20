//snippet-sourcedescription:[ListAlgorithms.kt demonstrates how to list algorithms.]
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
import aws.sdk.kotlin.services.sagemaker.model.ListAlgorithmsRequest
import aws.sdk.kotlin.services.sagemaker.model.ListModelsRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val sageMakerClient = SageMakerClient{region = "us-west-2" }
    listAlgs(sageMakerClient)
    sageMakerClient.close()
}

suspend fun listAlgs(sageMakerClient:SageMakerClient) {

    try {
        val algorithmsResponse = sageMakerClient.listAlgorithms( ListAlgorithmsRequest{})
        val items = algorithmsResponse.algorithmSummaryList

        if (items != null) {
            for (item in items) {
                println("Algorithm name is ${item.algorithmName}")
            }
        }

    } catch (e: SageMakerException) {
     println(e.message)
     sageMakerClient.close()
    exitProcess(0)
   }

}