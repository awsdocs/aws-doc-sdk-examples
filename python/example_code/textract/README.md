# Amazon Textract code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Textract.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AnalyzeDocument](textract_wrapper.py#L61)
- [DetectDocumentText](textract_wrapper.py#L34)
- [GetDocumentAnalysis](textract_wrapper.py#L255)
- [StartDocumentAnalysis](textract_wrapper.py#L207)
- [StartDocumentTextDetection](textract_wrapper.py#L145)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create an Amazon Textract explorer application](../../cross_service/textract_explorer)
- [Detect entities in text extracted from an image](../../cross_service/textract_comprehend_notebook)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create an Amazon Textract explorer application

This example shows you how to explore Amazon Textract output through an interactive application.


<!--custom.scenario_prereqs.cross_TextractExplorer.start-->
<!--custom.scenario_prereqs.cross_TextractExplorer.end-->


<!--custom.scenarios.cross_TextractExplorer.start-->
<!--custom.scenarios.cross_TextractExplorer.end-->

#### Detect entities in text extracted from an image

This example shows you how to use Amazon Comprehend to detect entities in text extracted by Amazon Textract from an image that is stored in Amazon S3.


<!--custom.scenario_prereqs.cross_TextractComprehendDetectEntities.start-->
<!--custom.scenario_prereqs.cross_TextractComprehendDetectEntities.end-->


<!--custom.scenarios.cross_TextractComprehendDetectEntities.start-->
<!--custom.scenarios.cross_TextractComprehendDetectEntities.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Textract Developer Guide](https://docs.aws.amazon.com/textract/latest/dg/what-is.html)
- [Amazon Textract API Reference](https://docs.aws.amazon.com/textract/latest/dg/API_Reference.html)
- [SDK for Python Amazon Textract reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/textract.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0