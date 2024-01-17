# Amazon Bedrock Runtime code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon Bedrock Runtime.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Bedrock Runtime is a fully managed service that makes it easy to use foundation models from third-party providers and Amazon._

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

- [Image generation with Amazon Titan Image Generator G1](BedrockRuntimeService.php#L184) (`InvokeModel`)
- [Image generation with Stability.ai Stable Diffusion XL](BedrockRuntimeService.php#L142) (`InvokeModel`)
- [Text generation with AI21 Labs Jurassic-2](BedrockRuntimeService.php#L72) (`InvokeModel`)
- [Text generation with Anthropic Claude 2](BedrockRuntimeService.php#L33) (`InvokeModel`)
- [Text generation with Meta Llama 2 Chat](BedrockRuntimeService.php#L107) (`InvokeModel`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Invoke multiple LLMs on Amazon Bedrock](GettingStartedWithBedrockRuntime.php)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Invoke multiple LLMs on Amazon Bedrock

This example shows you how to invoke multiple large-language-models (LLMs) on Amazon Bedrock.

- Generate text with Anthropic Claude.
- Generate text with AI21 Labs Jurassic-2.
- Generate text with Meta Llama 2 Chat.

<!--custom.scenario_prereqs.bedrock-runtime_Scenario_Invoke_models.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_Invoke_models.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_Invoke_models.start-->
From the `aws-doc-sdk-examples/php/example_code/bedrock-runtime` directory:

Install the required dependencies using Composer:

```
composer install
```

Once all dependencies have been installed, you can run the example by executing the
following command:

```
php Runner.php
```
<!--custom.scenarios.bedrock-runtime_Scenario_Invoke_models.end-->

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
../vendor/bin/phpunit tests/BedrockRuntimeTests.php
```

<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for PHP Amazon Bedrock Runtime reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Bedrock-runtime.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0