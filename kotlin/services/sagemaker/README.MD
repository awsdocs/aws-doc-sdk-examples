# SageMaker code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon SageMaker.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello SageMaker](src/main/kotlin/com/kotlin/sage/ListNotebooks.kt#L22) (`ListNotebookInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreatePipeline](../../usecases/workflow_sagemaker_pipes/src/main/kotlin/com/example/sage/SagemakerWorkflow.kt#L494)
- [DeletePipeline](../../usecases/workflow_sagemaker_pipes/src/main/kotlin/com/example/sage/SagemakerWorkflow.kt#L199)
- [DescribePipelineExecution](../../usecases/workflow_sagemaker_pipes/src/main/kotlin/com/example/sage/SagemakerWorkflow.kt#L390)
- [StartPipelineExecution](../../usecases/workflow_sagemaker_pipes/src/main/kotlin/com/example/sage/SagemakerWorkflow.kt#L411)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with geospatial jobs and pipelines](../../usecases/workflow_sagemaker_pipes/src/main/kotlin/com/example/sage/SagemakerWorkflow.kt)


<!--custom.examples.start-->

### Custom Examples

- **CreateModel** - Demonstrates how to create a model in Amazon SageMaker.
- **CreateTransformJob** - Demonstrates how to start a transform job that uses a trained model to get inferences on a dataset.
- **DeleteModel** - Demonstrates how to delete a model in Amazon SageMaker.
- **DescribeTrainingJob** - Demonstrates how to obtain information about a training job.
- **ListAlgorithms** - Demonstrates how to list algorithms.
- **ListModels** - Demonstrates how to retrieve a list of models.
- **ListNotebooks** - Demonstrates how to list notebooks.
- **ListTrainingJobs** - Demonstrates how to retrieve a list of training jobs.
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
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
- [SDK for Kotlin SageMaker reference](https://sdk.amazonaws.com/kotlin/api/latest/sagemaker/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0