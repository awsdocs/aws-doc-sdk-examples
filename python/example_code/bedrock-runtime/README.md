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

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console)
> before you have requested access to it, you will receive an error message. For more information,
> see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
>
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Bedrock Runtime](hello/hello_bedrock_runtime_invoke.py#L5) (`InvokeModel`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Tool use with the Converse API](cross-model-scenarios/tool_use_demo/tool_use_demo.py)

### AI21 Labs Jurassic-2

- [Converse](models/ai21_labs_jurassic2/converse.py#L4)
- [InvokeModel](models/ai21_labs_jurassic2/invoke_model.py#L4)

### Amazon Nova

- [Converse](models/amazon_nova/amazon_nova_text/converse.py#L4)
- [ConverseStream](models/amazon_nova/amazon_nova_text/converse_stream.py#L4)

### Amazon Nova Canvas

- [InvokeModel](models/amazon_nova/amazon_nova_canvas/invoke_model.py#L4)

### Amazon Titan Image Generator

- [InvokeModel](models/amazon_titan_image_generator/invoke_model.py#L4)

### Amazon Titan Text

- [Converse](models/amazon_titan_text/converse.py#L4)
- [ConverseStream](models/amazon_titan_text/converse_stream.py#L4)
- [InvokeModel](models/amazon_titan_text/invoke_model.py#L4)
- [InvokeModelWithResponseStream](models/amazon_titan_text/invoke_model_with_response_stream.py#L4)

### Amazon Titan Text Embeddings

- [InvokeModel](models/amazon_titan_text_embeddings/invoke_model.py#L4)

### Anthropic Claude

- [Converse](models/anthropic_claude/converse.py#L4)
- [ConverseStream](models/anthropic_claude/converse_stream.py#L4)
- [InvokeModel](models/anthropic_claude/invoke_model.py#L4)
- [InvokeModelWithResponseStream](models/anthropic_claude/invoke_model_with_response_stream.py#L4)
- [Scenario: Tool use with the Converse API](cross-model-scenarios/tool_use_demo/tool_use_demo.py)

### Cohere Command

- [Converse](models/cohere_command/converse.py#L4)
- [ConverseStream](models/cohere_command/converse_stream.py#L4)
- [InvokeModel: Command R and R+](models/cohere_command/command_r_invoke_model.py#L4)
- [InvokeModel: Command and Command Light](models/cohere_command/command_invoke_model.py#L4)
- [InvokeModelWithResponseStream: Command R and R+](models/cohere_command/command_r_invoke_model_with_response_stream.py#L4)
- [InvokeModelWithResponseStream: Command and Command Light](models/cohere_command/command_invoke_model_with_response_stream.py#L4)
- [Scenario: Tool use with the Converse API](cross-model-scenarios/tool_use_demo/tool_use_demo.py)

### Meta Llama

- [Converse](models/meta_llama/converse.py#L4)
- [ConverseStream](models/meta_llama/converse_stream.py#L4)
- [InvokeModel: Llama 3](models/meta_llama/llama3_invoke_model.py#L4)
- [InvokeModelWithResponseStream: Llama 3](models/meta_llama/llama3_invoke_model_with_response_stream.py#L4)

### Mistral AI

- [Converse](models/mistral_ai/converse.py#L4)
- [ConverseStream](models/mistral_ai/converse_stream.py#L4)
- [InvokeModel](models/mistral_ai/invoke_model.py#L4)
- [InvokeModelWithResponseStream](models/mistral_ai/invoke_model_with_response_stream.py#L4)

### Stable Diffusion

- [InvokeModel](models/stability_ai/invoke_model.py#L4)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The examples are wrapped into two separate scripts, with *claude_3.py* demonstrating the use of Claude 3, and
*bedrock_runtime_wrapper.py* demonstrating Claude 2, Titan, Stable Diffusion, Jurassic-2, Llama 2, and both models from
Mistral AI.

- To run the demo for Anthropic Claude 3, navigate to the `python/example/code/bedrock-runtime` directory and type:
  ```commandline
  python models/anthropic/claude_3.py
  ```

- To run the demo for the other models, navigate to the `python/example/code/bedrock-runtime` directory and type:
  ```commandline
  python bedrock_runtime_wrapper.py
  ```

<!--custom.instructions.end-->

#### Hello Amazon Bedrock Runtime

This example shows you how to get started using Amazon Bedrock Runtime.

```
python hello/hello_bedrock_runtime_invoke.py
```


#### Tool use with the Converse API

This example shows you how to build a typical interaction between an application, a generative AI model, and connected tools or APIs to mediate interactions between the AI and the outside world. It uses the example of connecting an external weather API to the AI model so it can provide real-time weather information based on user input.


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.end-->

Start the example by running the following at a command prompt:

```
python cross-model-scenarios/tool_use_demo/tool_use_demo.py
```


<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.end-->

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
