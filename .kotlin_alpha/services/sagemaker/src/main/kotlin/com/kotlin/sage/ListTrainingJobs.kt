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
//snippet-end:[sagemaker.kotlin.list_jobs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    listJobs()
   }

//snippet-start:[sagemaker.kotlin.list_jobs.main]
suspend fun listJobs() {

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val response = sageMakerClient.listTrainingJobs(ListTrainingJobsRequest{ })
        response.trainingJobSummaries?.forEach { item ->
                println("Name is ${item.trainingJobName}")
                println("Status is ${item.trainingJobStatus.toString()}")

        }
    }
}
//snippet-end:[sagemaker.kotlin.list_jobs.main]