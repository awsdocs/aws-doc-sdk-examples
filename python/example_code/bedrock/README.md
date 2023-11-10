# Amazon Bedrock code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with the Amazon Bedrock client.

<!--custom.overview.start-->
<!--custom.overview.end-->

*Amazon Bedrock is a fully managed service that offers a choice of high-performing foundation models (FMs) from leading AI companies like AI21 Labs, Anthropic, Cohere, Meta, Stability AI, and Amazon with a single API, along with a broad set of capabilities you need to build generative AI applications, simplifying development while maintaining privacy and security..*

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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
pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

* [Listing the available Bedrock foundation models](bedrock_wrapper.py#L33) (`ListFoundationModels`)

## Run the examples

### Instructions

#### List the available Bedrock foundation models

Start the example by running the following at a command prompt:

```
python bedrock_wrapper.py
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

* [Amazon Bedrock - User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide)
* [Amazon Bedrock - API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference)
* [AWS SDK for Python - Developer Guide](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/index.html)
* [AWS SDK for Python - Amazon Bedrock client reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0