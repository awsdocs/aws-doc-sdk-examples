//snippet-sourcedescription:[ListTrainingJobs.kt demonstrates how to retrieve a list of training jobs.]
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

//snippet-start:[sagemaker.kotlin.list_jobs.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListTrainingJobsRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess
//snippet-end:[sagemaker.kotlin.list_jobs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val sageMakerClient = SageMakerClient{region = "us-west-2" }
    listJobs(sageMakerClient)
    sageMakerClient.close()
}

//snippet-start:[sagemaker.kotlin.list_jobs.main]
suspend fun listJobs(sageMakerClient:SageMakerClient) {

    try {

        val response = sageMakerClient.listTrainingJobs(ListTrainingJobsRequest{ })
        val items = response.trainingJobSummaries
        if (items != null) {
            for (item in items) {
                println("Name is ${item.trainingJobName}")
                println("Status is ${item.trainingJobStatus.toString()}")
            }
        }

    } catch (e: SageMakerException) {
        println(e.message)
        sageMakerClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sagemaker.kotlin.list_jobs.main]