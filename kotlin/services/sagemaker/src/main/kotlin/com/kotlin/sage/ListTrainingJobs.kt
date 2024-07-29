// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.list_jobs.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.ListTrainingJobsRequest
// snippet-end:[sagemaker.kotlin.list_jobs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    listJobs()
}

// snippet-start:[sagemaker.kotlin.list_jobs.main]
suspend fun listJobs() {
    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val response = sageMakerClient.listTrainingJobs(ListTrainingJobsRequest { })
        response.trainingJobSummaries?.forEach { item ->
            println("Name is ${item.trainingJobName}")
            println("Status is ${item.trainingJobStatus}")
        }
    }
}
// snippet-end:[sagemaker.kotlin.list_jobs.main]
