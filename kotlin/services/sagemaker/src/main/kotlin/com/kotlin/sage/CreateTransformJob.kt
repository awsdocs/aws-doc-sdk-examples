// snippet-sourcedescription:[CreateTransformJob.kt demonstrates how to start a transform job that uses a trained model to get inferences on a dataset.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sage

// snippet-start:[sagemaker.kotlin.transform_job.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.CreateTransformJobRequest
import aws.sdk.kotlin.services.sagemaker.model.S3DataType
import aws.sdk.kotlin.services.sagemaker.model.SplitType
import aws.sdk.kotlin.services.sagemaker.model.TransformDataSource
import aws.sdk.kotlin.services.sagemaker.model.TransformInput
import aws.sdk.kotlin.services.sagemaker.model.TransformInstanceType
import aws.sdk.kotlin.services.sagemaker.model.TransformOutput
import aws.sdk.kotlin.services.sagemaker.model.TransformResources
import aws.sdk.kotlin.services.sagemaker.model.TransformS3DataSource
import kotlin.system.exitProcess
// snippet-end:[sagemaker.kotlin.transform_job.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <s3Uri> <s3OutputPath> <modelName> <transformJobName>

    Where:
        s3Uri - Identifies the key name of an Amazon S3 object that contains the data (ie, s3://mybucket/churn.txt).
        s3OutputPath - The Amazon S3 location where the results are stored.
        modelName - The name of the model.
        transformJobName - The name of the transform job.
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(1)
    }

    val s3Uri = args[0]
    val s3OutputPath = args[1]
    val modelName = args[2]
    val transformJobName = args[3]
    transformJob(s3Uri, s3OutputPath, modelName, transformJobName)
}

// snippet-start:[sagemaker.kotlin.transform_job.main]
suspend fun transformJob(
    s3UriVal: String?,
    s3OutputPathVal: String?,
    modelNameVal: String?,
    transformJobNameVal: String?
) {

    val s3DataSourceOb = TransformS3DataSource {
        s3DataType = S3DataType.S3Prefix
        s3Uri = s3UriVal
    }

    val dataSourceOb = TransformDataSource {
        s3DataSource = s3DataSourceOb
    }

    val input = TransformInput {
        dataSource = dataSourceOb
        contentType = "text/csv"
        splitType = SplitType.Line
    }

    val output = TransformOutput {
        s3OutputPath = s3OutputPathVal
    }

    val resources = TransformResources {
        instanceCount = 1
        instanceType = TransformInstanceType.MlC4_4_Xlarge
    }

    val request = CreateTransformJobRequest {
        transformJobName = transformJobNameVal
        modelName = modelNameVal
        transformInput = input
        transformOutput = output
        transformResources = resources
    }

    SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
        val jobResponse = sageMakerClient.createTransformJob(request)
        println("Response ${jobResponse.transformJobArn}")
    }
}
// snippet-end:[sagemaker.kotlin.transform_job.main]
