// snippet-sourcedescription:[DescribeTrainingJob.kt demonstrates how to obtain information about a training job.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.describe_train_job.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.DescribeTrainingJobRequest
import kotlin.system.exitProcess
// snippet-end:[sagemaker.kotlin.describe_train_job.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <trainingJobName>

    Where:
        trainingJobName - The name of the training job.

    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val trainingJobName = args[0]
    describeTrainJob(trainingJobName)
}

// snippet-start:[sagemaker.kotlin.describe_train_job.main]
suspend fun describeTrainJob(trainingJobNameVal: String?) {

    val request = DescribeTrainingJobRequest {
        trainingJobName = trainingJobNameVal
    }

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val jobResponse = sageMakerClient.describeTrainingJob(request)
        println("The job status is ${jobResponse.trainingJobStatus}")
    }
}
// snippet-end:[sagemaker.kotlin.describe_train_job.main]
