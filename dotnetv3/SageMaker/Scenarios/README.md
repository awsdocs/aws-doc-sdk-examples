# Create and execute a SageMaker geospatial pipeline using an AWS SDK

## Overview

This scenario demonstrates how to work with Amazon SageMaker (SageMaker) pipelines and geospatial jobs.

A [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html) is a series of 
interconnected steps that can be used to automate machine learning workflows. Pipelines can be created 
and executed from SageMaker Studio using Python, but you can also work with them using AWS SDKs in other
languages. Creating, executing, and monitoring operations for SageMaker pipelines are all available using the SDKs.

### Pipeline Steps
This example pipeline includes an [AWS Lambda step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-lambda) 
and a [callback step](https://docs.aws.amazon.com/sagemaker/latest/dg/build-and-manage-steps.html#step-type-callback), 
which are processed by the same example Lambda function. The Lambda handles starting a SageMaker job, or an export function, or the processing of the Amazon SQS queue message that 
is used for SageMaker callback steps. This Lambda code is included as part of this example. 

![](C:\Work\Repos\Forks\aws-doc-sdk-examples\dotnetv3\SageMaker\Pipeline.PNG)

### Pipeline Parameters
The example pipeline also includes parameters that can be referenced in throughout the steps, and can be used to change
values between executions. In this example, the parameters are used to set the Amazon Simple Storage Service (Amazon S3)
locations for the input and output files, along with the identifiers for the role and queue to use in the pipeline. 
The example demonstrates how to set and access these parameters.

### Geospatial Jobs
A SageMaker pipeline can be used for model training, setup, testing, or validation. This example uses a simple job
for demonstration purposes - a [Vector Enrichment Job (VEJ)](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial-vej.html) that processes a set of coordinates to produce human-readable 
addresses powered by Amazon Location Service. Other types of job or jobs could be substituted in the pipeline instead.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

To view pipelines in SageMaker Studio, you will need to [set up an Amazon SageMaker Domain](https://docs.aws.amazon.com/sagemaker/latest/dg/gs-studio-onboard.html).
To use geospatial capabilities, [you will need to use a supported region](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial.html).
You may use the provided input file, or provide your own. The AWS Lambda function is provided as a zip archive, but can also be
packaged using the [Lambda plugin for .NET](https://docs.aws.amazon.com/lambda/latest/dg/csharp-package-toolkit.html).

For general prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

#### Get started with geospatial jobs and pipelines

This example shows you how to do the following:

* Set up resources for a pipeline.
* Set up a pipeline that executes a geospatial job.
* Start a pipeline execution.
* Monitor the status of the execution.
* View the output of the pipeline.
* Clean up resources.

## Additional resources

* [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
* [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
* [SDK for .NET SageMaker reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SageMaker/NSageMaker.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0