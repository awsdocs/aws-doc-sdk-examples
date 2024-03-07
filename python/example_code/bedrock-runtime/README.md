# Amazon Bedrock Runtime code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
> 
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Image generation with Amazon Titan Image Generator G1](bedrock_runtime_wrapper.py#L275) (`InvokeModel`)
- [Image generation with Stability.ai Stable Diffusion XL](bedrock_runtime_wrapper.py#L232) (`InvokeModel`)
- [Multimodal image analysis with Anthropic Claude 3](models/anthropic/claude_3.py#L94) (`InvokeModel`)
- [Text generation with AI21 Labs Jurassic-2](bedrock_runtime_wrapper.py#L79) (`InvokeModel`)
- [Text generation with Anthropic Claude 2](bedrock_runtime_wrapper.py#L39) (`InvokeModel`)
- [Text generation with Anthropic Claude 2 with a response stream](bedrock_runtime_wrapper.py#L320) (`InvokeModelWithResponseStream`)
- [Text generation with Anthropic Claude 3](models/anthropic/claude_3.py#L33) (`InvokeModel`)
- [Text generation with Meta Llama 2 Chat](bedrock_runtime_wrapper.py#L115) (`InvokeModel`)
- [Text generation with Mistral 7B](bedrock_runtime_wrapper.py#L152) (`InvokeModel`)
- [Text generation with Mixtral 8x7B](bedrock_runtime_wrapper.py#L192) (`InvokeModel`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The examples are wrapped into two separate scripts, with *claude_3.py* demonstrating the use of Claude 3, and *bedrock_runtime_wrapper.py* demonstrating Claude 2, Titan, Stable Diffusion, Jurassic-2, Llama 2, and both models from Mistral AI.

- To run the demo for Anthropic Claude 3, navigate to the `python/example/code/bedrock-runtime` directory and type:
  ```commandline
  python models/anthropic/claude_3.py
  ```

- To run the demo for the other models, navigate to the `python/example/code/bedrock-runtime` directory and type:
  ```commandline
  python bedrock_runtime_wrapper.py
  ```
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Python Amazon Bedrock Runtime reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock-runtime.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0