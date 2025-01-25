# Amazon Bedrock code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Bedrock.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Bedrock enables you to build and scale generative AI applications with foundation models._

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
> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Bedrock](hello_bedrock.py#L5) (`ListFoundationModels`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [GetFoundationModel](bedrock_wrapper.py#L33)
- [ListFoundationModels](bedrock_wrapper.py#L53)


<!--custom.examples.start-->
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and orchestrate generative AI applications with AWS Step Functions](https://github.com/aws-samples/amazon-bedrock-serverless-prompt-chaining)
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
Run the example by executing the following command inside the `example_code` folder:

```
python bedrock_wrapper.py
```
<!--custom.instructions.end-->

#### Hello Amazon Bedrock

This example shows you how to get started using Amazon Bedrock.

```
python hello_bedrock.py
```


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Python Amazon Bedrock reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
