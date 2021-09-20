//snippet-sourcedescription:[CreateTransformJob.kt demonstrates how to start a transform job that uses a trained model to get inferences on a dataset.]
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

//snippet-start:[sagemaker.kotlin.transform_job.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.TransformS3DataSource
import aws.sdk.kotlin.services.sagemaker.model.S3DataType
import aws.sdk.kotlin.services.sagemaker.model.TransformDataSource
import aws.sdk.kotlin.services.sagemaker.model.TransformInput
import aws.sdk.kotlin.services.sagemaker.model.SplitType
import aws.sdk.kotlin.services.sagemaker.model.TransformOutput
import aws.sdk.kotlin.services.sagemaker.model.TransformResources
import aws.sdk.kotlin.services.sagemaker.model.TransformInstanceType
import aws.sdk.kotlin.services.sagemaker.model.CreateTransformJobRequest
import aws.sdk.kotlin.services.sagemaker.model.SageMakerException
import kotlin.system.exitProcess
//snippet-end:[sagemaker.kotlin.transform_job.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <s3Uri> <s3OutputPath> <modelName> <transformJobName>

    Where:
        s3Uri - identifies the key name of an Amazon S3 object that contains the data (ie, s3://mybucket/churn.txt).
        s3OutputPath - the Amazon S3 location where the results are stored.
        modelName - the name of the model.
        transformJobName - the name of the transform job.
    """

   if (args.size != 4) {
        println(usage)
        exitProcess(1)
   }

    val s3Uri = args[0];
    val s3OutputPath = args[1];
    val modelName = args[2];
    val transformJobName = args[3];

    val sageMakerClient = SageMakerClient{region = "us-west-2" }
    transformJob(sageMakerClient, s3Uri, s3OutputPath, modelName, transformJobName);
    sageMakerClient.close()
}

//snippet-start:[sagemaker.kotlin.transform_job.main]
suspend fun transformJob(
    sageMakerClient: SageMakerClient,
    s3UriVal: String?,
    s3OutputPathVal: String?,
    modelNameVal: String?,
    transformJobNameVal: String?
) {
    try {
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

        val jobRequest = CreateTransformJobRequest {
            transformJobName = transformJobNameVal
            modelName = modelNameVal
            transformInput = input
            transformOutput = output
            transformResources = resources
        }

        val jobResponse = sageMakerClient.createTransformJob(jobRequest)
        println("Response ${jobResponse.transformJobArn}")

    } catch (e: SageMakerException) {
        println(e.message)
        sageMakerClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sagemaker.kotlin.transform_job.main]