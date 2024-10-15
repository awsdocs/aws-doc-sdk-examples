# SageMaker code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon SageMaker.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello SageMaker](hello.js#L6) (`ListNotebookInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreatePipeline](scenarios/wkflw-sagemaker-geospatial-pipeline/lib.js#L477)
- [DeletePipeline](scenarios/wkflw-sagemaker-geospatial-pipeline/lib.js#L535)
- [DescribePipelineExecution](scenarios/wkflw-sagemaker-geospatial-pipeline/lib.js#L795)
- [StartPipelineExecution](scenarios/wkflw-sagemaker-geospatial-pipeline/lib.js#L710)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with geospatial jobs and pipelines](scenarios/wkflw-sagemaker-geospatial-pipeline/lib.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello SageMaker

This example shows you how to get started using SageMaker.

```bash
node ./hello.js
```


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

Run the scenario:

```bash
cd wkflw-sagemaker-geospatial-pipeline
node index.js
```

<!--custom.scenarios.sagemaker_Scenario_Pipelines.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) SageMaker reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/sagemaker)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0