# SageMaker code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon SageMaker.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello SageMaker](Actions/HelloSageMaker.cs#L4) (`ListNotebookInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreatePipeline](Actions/SageMakerWrapper.cs#L25)
- [DeletePipeline](Actions/SageMakerWrapper.cs#L146)
- [DescribePipelineExecution](Actions/SageMakerWrapper.cs#L128)
- [StartPipelineExecution](Actions/SageMakerWrapper.cs#L62)
- [UpdatePipeline](Actions/SageMakerWrapper.cs#L25)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with geospatial jobs and pipelines](Actions/SageMakerWrapper.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
- [SDK for .NET SageMaker reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SageMaker/NSageMaker.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0