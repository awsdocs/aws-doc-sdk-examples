# Amazon Bedrock Runtime code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
> 
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Image generation with Amazon Titan Image Generator G1](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L399) (`InvokeModel`)
- [Image generation with Stability.ai Stable Diffusion XL](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L329) (`InvokeModel`)
- [Text generation with AI21 Labs Jurassic-2](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L205) (`InvokeModel`)
- [Text generation with Anthropic Claude 2](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L143) (`InvokeModel`)
- [Text generation with Anthropic Claude using a response stream](src/main/java/com/example/bedrockruntime/InvokeModelWithResponseStream.java#L28) (`InvokeModelWithResponseStream`)
- [Text generation with Meta Llama 2 Chat](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L268) (`InvokeModel`)
- [Text generation with Mistral 7B](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L33) (`InvokeModel`)
- [Text generation with Mixtral 8x7B](src/main/java/com/example/bedrockruntime/InvokeModelAsync.java#L88) (`InvokeModel`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Java 2.x Amazon Bedrock Runtime reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/bedrock-runtime/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0