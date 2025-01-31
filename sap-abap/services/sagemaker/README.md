# SageMaker code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon SageMaker.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateEndpoint](zcl_aws1_sgm_actions.clas.abap#L120)
- [CreateModel](zcl_aws1_sgm_actions.clas.abap#L170)
- [CreateTrainingJob](zcl_aws1_sgm_actions.clas.abap#L201)
- [CreateTransformJob](zcl_aws1_sgm_actions.clas.abap#L334)
- [DeleteEndpoint](zcl_aws1_sgm_actions.clas.abap#L399)
- [DeleteModel](zcl_aws1_sgm_actions.clas.abap#L432)
- [DescribeTrainingJob](zcl_aws1_sgm_actions.clas.abap#L454)
- [ListAlgorithms](zcl_aws1_sgm_actions.clas.abap#L475)
- [ListModels](zcl_aws1_sgm_actions.clas.abap#L496)
- [ListNotebookInstances](zcl_aws1_sgm_actions.clas.abap#L517)
- [ListTrainingJobs](zcl_aws1_sgm_actions.clas.abap#L537)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with models and endpoints](zcl_aws1_sgm_scenario.clas.abap)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Get started with models and endpoints

This example shows you how to do the following:

- Start a training job and create a SageMaker model.
- Create an endpoint configuration.
- Create an endpoint, then clean up resources.

<!--custom.scenario_prereqs.sagemaker_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.sagemaker_Scenario_GettingStarted.end-->


<!--custom.scenarios.sagemaker_Scenario_GettingStarted.start-->
<!--custom.scenarios.sagemaker_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [SageMaker Developer Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/whatis.html)
- [SageMaker API Reference](https://docs.aws.amazon.com/sagemaker/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP SageMaker reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/sgm/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0