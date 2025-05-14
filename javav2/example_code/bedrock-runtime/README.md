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
**Note: This project uses JDK 21**
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console)
> before you have requested access to it, you will receive an error message. For more information,
> see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
>
<!--custom.prerequisites.end-->

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Generate videos from text prompts using Amazon Bedrock](../../usecases/video_generation_bedrock_nova_reel/src/main/java/com/example/novareel/VideoGenerationService.java)
- [Tool use with the Converse API](src/main/java/com/example/bedrockruntime/scenario/BedrockScenario.java)

### AI21 Labs Jurassic-2

- [Converse](src/main/java/com/example/bedrockruntime/models/ai21LabsJurassic2/Converse.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/ai21LabsJurassic2/InvokeModel.java#L6)

### Amazon Nova

- [Converse](src/main/java/com/example/bedrockruntime/models/amazon/nova/text/ConverseAsync.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/amazon/nova/text/ConverseStream.java#L6)
- [Scenario: Tool use with the Converse API](src/main/java/com/example/bedrockruntime/scenario/BedrockScenario.java#L15)

### Amazon Nova Canvas

- [InvokeModel](src/main/java/com/example/bedrockruntime/models/amazon/nova/canvas/InvokeModel.java#L6)

### Amazon Titan Image Generator

- [InvokeModel](src/main/java/com/example/bedrockruntime/models/amazonTitanImage/InvokeModel.java#L6)

### Amazon Titan Text

- [Converse](src/main/java/com/example/bedrockruntime/models/amazonTitanText/Converse.java#L7)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/amazonTitanText/ConverseStream.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/amazonTitanText/InvokeModel.java#L6)
- [InvokeModelWithResponseStream](src/main/java/com/example/bedrockruntime/models/amazonTitanText/InvokeModelWithResponseStream.java#L6)

### Amazon Titan Text Embeddings

- [InvokeModel](src/main/java/com/example/bedrockruntime/models/amazonTitanTextEmbeddings/InvokeModel.java#L6)

### Anthropic Claude

- [Converse](src/main/java/com/example/bedrockruntime/models/anthropicClaude/Converse.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/anthropicClaude/ConverseStream.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/anthropicClaude/InvokeModel.java#L6)
- [InvokeModelWithResponseStream](src/main/java/com/example/bedrockruntime/models/anthropicClaude/InvokeModelWithResponseStream.java#L6)
- [Reasoning](src/main/java/com/example/bedrockruntime/models/anthropicClaude/ReasoningAsync.java#L6)
- [Reasoning with a streaming response](src/main/java/com/example/bedrockruntime/models/anthropicClaude/ReasoningStream.java#L6)

### Cohere Command

- [Converse](src/main/java/com/example/bedrockruntime/models/cohereCommand/Converse.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/cohereCommand/ConverseStream.java#L6)
- [InvokeModel: Command R and R+](src/main/java/com/example/bedrockruntime/models/cohereCommand/Command_R_InvokeModel.java#L6)
- [InvokeModel: Command and Command Light](src/main/java/com/example/bedrockruntime/models/cohereCommand/Command_InvokeModel.java#L6)
- [InvokeModelWithResponseStream: Command R and R+](src/main/java/com/example/bedrockruntime/models/cohereCommand/Command_R_InvokeModelWithResponseStream.java#L6)
- [InvokeModelWithResponseStream: Command and Command Light](src/main/java/com/example/bedrockruntime/models/cohereCommand/Command_InvokeModelWithResponseStream.java#L6)

### Meta Llama

- [Converse](src/main/java/com/example/bedrockruntime/models/metaLlama/Converse.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/metaLlama/ConverseStream.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/metaLlama/Llama3_InvokeModel.java#L6)
- [InvokeModelWithResponseStream](src/main/java/com/example/bedrockruntime/models/metaLlama/Llama3_InvokeModelWithResponseStream.java#L6)

### Mistral AI

- [Converse](src/main/java/com/example/bedrockruntime/models/mistral/Converse.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/mistral/ConverseStream.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/mistral/InvokeModel.java#L6)
- [InvokeModelWithResponseStream](src/main/java/com/example/bedrockruntime/models/mistral/InvokeModelWithResponseStream.java#L6)

### Stable Diffusion

- [InvokeModel](src/main/java/com/example/bedrockruntime/models/stabilityAi/InvokeModel.java#L6)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Generate videos from text prompts using Amazon Bedrock

This example shows you how to a Spring Boot app that generates videos from text prompts using Amazon Bedrock and the
Nova-Reel model.


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_GenerateVideos_NovaReel.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_GenerateVideos_NovaReel.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_GenerateVideos_NovaReel.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_GenerateVideos_NovaReel.end-->

#### Tool use with the Converse API

This example shows you how to build a typical interaction between an application, a generative AI model, and connected
tools or APIs to mediate interactions between the AI and the outside world. It uses the example of connecting an
external weather API to the AI model so it can provide real-time weather information based on user input.


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.end-->

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
