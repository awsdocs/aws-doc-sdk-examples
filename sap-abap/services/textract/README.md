# Amazon Textract code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Textract.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Textract enables you to add document text detection and analysis to your applications._

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

- [AnalyzeDocument](zcl_aws1_tex_actions.clas.abap#L61)
- [DetectDocumentText](zcl_aws1_tex_actions.clas.abap#L136)
- [GetDocumentAnalysis](zcl_aws1_tex_actions.clas.abap#L197)
- [StartDocumentAnalysis](zcl_aws1_tex_actions.clas.abap#L255)
- [StartDocumentTextDetection](zcl_aws1_tex_actions.clas.abap#L326)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with document analysis](zcl_aws1_tex_scenario.clas.abap)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Get started with document analysis

This example shows you how to do the following:

- Start asynchronous analysis.
- Get document analysis.

<!--custom.scenario_prereqs.textract_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.textract_Scenario_GettingStarted.end-->


<!--custom.scenarios.textract_Scenario_GettingStarted.start-->
<!--custom.scenarios.textract_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Textract Developer Guide](https://docs.aws.amazon.com/textract/latest/dg/what-is.html)
- [Amazon Textract API Reference](https://docs.aws.amazon.com/textract/latest/dg/API_Reference.html)
- [SDK for SAP ABAP Amazon Textract reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/tex/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0