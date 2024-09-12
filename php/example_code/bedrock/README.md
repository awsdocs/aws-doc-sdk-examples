# Amazon Bedrock code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon Bedrock.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
- [PHP](https://www.php.net/) version 8.2 or higher
- [Composer](https://getcomposer.org), for dependency management
- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the
  [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [ListFoundationModels](BedrockService.php#L13)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
From the `aws-doc-sdk-examples/php/example_code/bedrock` directory:

Install the required dependencies using Composer:

```
composer install
```

Once all dependencies have been installed, you can run the example by executing the
following command:

```
php Runner.php
```
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
From the `aws-doc-sdk-examples/php/example_code/bedrock-runtime` directory:

Install the reequired dependencies using Composer:

```
composer install
```
Run the tests with the following command:
```
../vendor/bin/phpunit tests/BedrockBasicsTests.php
```

<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for PHP Amazon Bedrock reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Bedrock.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0