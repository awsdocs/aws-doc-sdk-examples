//snippet-sourcedescription:[CreateModel.kt demonstrates how to create a model in Amazon SageMaker.]
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

//snippet-start:[sagemaker.kotlin.train_job.import]
import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.S3DataSource
import aws.sdk.kotlin.services.sagemaker.model.S3DataType
import aws.sdk.kotlin.services.sagemaker.model.S3DataDistribution
import aws.sdk.kotlin.services.sagemaker.model.DataSource
import aws.sdk.kotlin.services.sagemaker.model.ResourceConfig
import aws.sdk.kotlin.services.sagemaker.model.TrainingInstanceType
import aws.sdk.kotlin.services.sagemaker.model.CheckpointConfig
import aws.sdk.kotlin.services.sagemaker.model.OutputDataConfig
import aws.sdk.kotlin.services.sagemaker.model.StoppingCondition
import aws.sdk.kotlin.services.sagemaker.model.AlgorithmSpecification
import aws.sdk.kotlin.services.sagemaker.model.TrainingInputMode
import aws.sdk.kotlin.services.sagemaker.model.CreateTrainingJobRequest
import aws.sdk.kotlin.services.sagemaker.model.Channel
import kotlin.system.exitProcess
//snippet-end:[sagemaker.kotlin.train_job.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <s3UriData> <s3Uri> <trainingJobName> <roleArn> <s3OutputPath> <channelName> <trainingImage>

    Where:
        s3UriData - the location of the training data (for example, s3://trainbucket/train.csv).
        s3Uri - the Amazon S3 path where you want Amazon SageMaker to store checkpoints (for example, s3://trainbucket).
        trainingJobName - the name of the training job. 
        roleArn - the Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role that SageMaker uses.
        s3OutputPath - the output path located in an Amazon S3 bucket (for example, s3://trainbucket/sagemaker).
        channelName  - the channel name (for example, s3://trainbucket/sagemaker).
        trainingImage  - the training image (for example, 000007028032.bbb.zzz.us-west-2.amazonaws.com/xgboost:latest.
    """


    if (args.size != 7) {
       println(usage)
       exitProcess(1)
    }

    val s3UriData = args[0]
    val s3Uri = args[1]
    val trainingJobName = args[2]
    val roleArn = args[3]
    val s3OutputPath = args[4]
    val channelName = args[5]
    val trainingImage = args[6]
    trainJob(s3UriData, s3Uri, trainingJobName, roleArn, s3OutputPath, channelName, trainingImage)
    }

//snippet-start:[sagemaker.kotlin.train_job.main]
suspend fun trainJob(
    s3UriData: String?,
    s3UriVal:String,
    trainingJobNameVal: String?,
    roleArnVal: String?,
    s3OutputPathVal: String?,
    channelNameVal: String?,
    trainingImageVal: String?
) {

        val s3DataSourceOb = S3DataSource {
            s3Uri = s3UriData
            s3DataType = S3DataType.S3Prefix
            s3DataDistributionType = S3DataDistribution.FullyReplicated
        }

        val dataSourceOb = DataSource {
            s3DataSource = s3DataSourceOb
        }

        val channel = Channel {
            channelName = channelNameVal
            contentType = "csv"
            dataSource = dataSourceOb
        }

        val myChannel = mutableListOf<Any>()
        myChannel.add(channel)

        val resourceConfigOb = ResourceConfig {
            instanceType = TrainingInstanceType.MlC5_2_Xlarge
            instanceCount = 10
            volumeSizeInGb = 1
        }

        val checkpointConfigOb = CheckpointConfig {
            s3Uri = s3UriVal
        }

        val outputDataConfigOb = OutputDataConfig {
            s3OutputPath = s3OutputPathVal
        }

        val stoppingConditionOb = StoppingCondition {
            maxRuntimeInSeconds = 1200
        }

        val algorithmSpecificationOb = AlgorithmSpecification {
            trainingImage = trainingImageVal
            trainingInputMode = TrainingInputMode.File
        }

        // Set hyper parameters
        val hyperParametersOb = mutableMapOf<String, String>()
        hyperParametersOb["num_round"] = "100"
        hyperParametersOb["eta"] = "0.2"
        hyperParametersOb["gamma"] = "4"
        hyperParametersOb["max_depth"] = "5"
        hyperParametersOb["min_child_weight"] = "6"
        hyperParametersOb["objective"] = "binary:logistic"
        hyperParametersOb["silent"] = "0"
        hyperParametersOb["subsample"] = "0.8"

        val request = CreateTrainingJobRequest{
            trainingJobName = trainingJobNameVal
            algorithmSpecification = algorithmSpecificationOb
            roleArn = roleArnVal
            resourceConfig = resourceConfigOb
            checkpointConfig = checkpointConfigOb
            inputDataConfig = listOf(channel)
            outputDataConfig = outputDataConfigOb
            stoppingCondition = stoppingConditionOb
            hyperParameters = hyperParametersOb
            }

        SageMakerClient { region = "us-west-2" }.use { sageMakerClient ->
          val response = sageMakerClient.createTrainingJob(request)
          println("The Amazon Resource Name (ARN) of the training job is ${response.trainingJobArn}")
        }
}
//snippet-end:[sagemaker.kotlin.train_job.main]