# Create and run a SageMaker geospatial pipeline using the SDK for Java V2

## Overview

This scenario demonstrates how to work with Amazon SageMaker pipelines and geospatial jobs.

### Amazon SageMaker Pipelines
A [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html) is a series of
interconnected steps that can be used to automate machine learning workflows. Pipelines use interconnected steps and shared parameters to support repeatable workflows that can be customized for your specific use case. You can create and run pipelines from SageMaker Studio using Python, but you can also do this by using AWS SDKs in other
languages. Using the SDKs, you can create and run SageMaker pipelines and also monitor operations for them.

### Explore the scenario
This example scenario demonstrates using AWS Lambda and Amazon Simple Queue Service (Amazon SQS) as part of an Amazon SageMaker pipeline. The pipeline itself executes a geospatial job to reverse geocode a sample set of coordinates into human-readable addresses. Input and output files are located in an Amazon Simple Storage Service (Amazon S3) bucket.

![Workflow image](../../../workflows/sagemaker_pipelines/resources/workflow.png)

When you run the example console application, you can execute the following steps:

- Create the AWS resources and roles needed for the pipeline.
- Create the AWS Lambda function.
- Create the SageMaker pipeline.
- Upload an input file into an S3 bucket.
- Execute the pipeline and monitor its status.
- Display some output from the output file.
- Clean up the pipeline resources.

#### Pipeline steps
[Pipeline steps](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html) define the actions and relationships of the pipeline operations. The pipeline in this example includes an [AWS Lambda step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-lambda)
and a [callback step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-callback).
Both steps are processed by the same example Lambda function.

The Lambda function handler is included as part of the example, with the following functionality:
- Starts a [SageMaker Vector Enrichment Job](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial-vej.html) with the provided job configuration.
- Processes Amazon SQS queue messages from the SageMaker pipeline.
- Starts the export function with the provided export configuration.
- Completes the pipeline when the export is complete.

![Pipeline image](../../../workflows/sagemaker_pipelines/resources/pipeline.png)

#### Pipeline parameters
The example pipeline uses [parameters](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-parameters.html) that you can reference throughout the steps. You can also use the parameters to change
values between runs and control the input and output setting. In this example, the parameters are used to set the Amazon Simple Storage Service (Amazon S3)
locations for the input and output files, along with the identifiers for the role and queue to use in the pipeline.
The example demonstrates how to set and access these parameters before executing the pipeline using an SDK.

#### Geospatial jobs
A SageMaker pipeline can be used for model training, setup, testing, or validation. This example uses a simple job
for demonstration purposes: a [Vector Enrichment Job (VEJ)](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial-vej.html) that processes a set of coordinates to produce human-readable
addresses powered by Amazon Location Service. Other types of jobs can be substituted in the pipeline instead.
## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

To use this tutorial, you need the following:

+ An AWS account.
+ A Java IDE. 
+ Java 1.8 JDK or later.
+ Maven 3.6 or later.
+ Set up your development environment. For more information, see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-basics.html).

To view pipelines in SageMaker Studio, you need to [set up an Amazon SageMaker Domain](https://docs.aws.amazon.com/sagemaker/latest/dg/gs-studio-onboard.html).
To use geospatial capabilities, [you need to use a supported Region](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial.html).

You must download and use these files to successfully run this code example:

+ GeoSpatialPipeline.json
+ latlongtest.csv

These files are located on GitHub in this [folder](../../../workflows/sagemaker_pipelines/resources).

### Java Lambda Function

To successfully run this example, you need to create the Java Sagemaker Lambda function. This Lambda function is required. You can find this project here: [Create the SageMaker geospatial Lambda function using the Lambda Java rumtime API](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_sagemaker_lambda). This project creates a JAR file that is input to this code example.  

Once you create the Java Lambda project, you can build the required JAR file using the **mvn package** command. This will create the JAR file in the target folder. You can use this JAR file as input to this code example. 

### Instructions

You can run this Java code example from within your Java IDE.

#### Get started with geospatial jobs and pipelines

This example shows you how to do the following:

* Set up resources for a pipeline.
* Set up a pipeline that runs a geospatial job.
* Start a pipeline run.
* Monitor the status of the run.
* View the output of the pipeline.
* Clean up resources.

## Additional resources

* [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
* [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
* [Java Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.