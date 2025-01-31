# Amazon Translate code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Translate.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Translate is a neural machine translation service for translating text to and from English across a breadth of supported languages._

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

- [DescribeTextTranslationJob](zcl_aws1_xl8_actions.clas.abap#L59)
- [ListTextTranslationJobs](zcl_aws1_xl8_actions.clas.abap#L86)
- [StartTextTranslationJob](zcl_aws1_xl8_actions.clas.abap#L121)
- [StopTextTranslationJob](zcl_aws1_xl8_actions.clas.abap#L181)
- [TranslateText](zcl_aws1_xl8_actions.clas.abap#L207)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with translate jobs](zcl_aws1_xl8_scenario.clas.abap)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Get started with translate jobs

This example shows you how to do the following:

- Start an asynchronous batch translation job.
- Wait for the asynchronous job to complete.
- Describe the asynchronous job.

<!--custom.scenario_prereqs.translate_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.translate_Scenario_GettingStarted.end-->


<!--custom.scenarios.translate_Scenario_GettingStarted.start-->
<!--custom.scenarios.translate_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Translate Developer Guide](https://docs.aws.amazon.com/translate/latest/dg/what-is.html)
- [Amazon Translate API Reference](https://docs.aws.amazon.com/translate/latest/APIReference/welcome.html)
- [SDK for SAP ABAP Amazon Translate reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/xl8/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0