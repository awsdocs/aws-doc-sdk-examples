# SageMaker code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon SageMaker.

<!--custom.overview.start-->
<!--custom.overview.end-->

_SageMaker is a fully managed machine learning service._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello SageMaker](src/main/java/com/example/sage/HelloSageMaker.java#L15) (`ListNotebookInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreatePipeline](../../usecases/workflow_sagemaker_pipes/src/main/java/com/example/sage/SagemakerWorkflow.java#L309)
- [DeletePipeline](../../usecases/workflow_sagemaker_pipes/src/main/java/com/example/sage/SagemakerWorkflow.java#L297)
- [DescribePipelineExecution](../../usecases/workflow_sagemaker_pipes/src/main/java/com/example/sage/SagemakerWorkflow.java#L275)
- [StartPipelineExecution](../../usecases/workflow_sagemaker_pipes/src/main/java/com/example/sage/SagemakerWorkflow.java#L348)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with geospatial jobs and pipelines](../../usecases/workflow_sagemaker_pipes/src/main/java/com/example/sage/SagemakerWorkflow.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello SageMaker

This example shows you how to get started using SageMaker.



#### Get started with geospatial jobs and pipelines

This example shows you how to do the following:

- Set up resources for a pipeline.
- Set up a pipeline that executes a geospatial job.
- Start a pipeline execution.
- Monitor the status of the execution.
- View the output of the pipeline.
- Clean up resources.

<!--custom.scenario_prereqs.sagemaker_Scenario_Pipelines.start-->
<!--custom.scenario_prereqs.sagemaker_Scenario_Pipelines.end-->


<!--custom.scenarios.sagemaker_Scenario_Pipelines.start-->
<!--custom.scenarios.sagemaker_Scenario_Pipelines.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->

#### Properties file

Before running the SageMaker JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a model name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **image** - The registry path of the Docker image that contains the training algorithm.
- **modelDataUrl** - The Amazon S3 path where the model artifacts, which result from model training, are stored.
- **executionRoleArn** - The Amazon Resource Name (ARN) of the IAM role that SageMaker uses.
- **modelName** - The model name used in various tests.
- **s3UriData** - The Amazon S3 path where the model data is stored and used in the **CreateTrainingJob** test.
- **s3Uri** - The Amazon S3 path where you want SageMaker to store checkpoints.
- **trainingJobName** - The name of the training job.
- **roleArn** - The ARN of the IAM role that SageMaker uses.
- **s3OutputPath** - The output path located in an Amazon S3 bucket (i.e., s3://trainbucket/sagemaker).
- **channelName** - The channel name.
- **trainingImage** - The training image.

<!--custom.tests.end-->

## Additional resources

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
- [SDK for Java 2.x SageMaker reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sagemaker/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0