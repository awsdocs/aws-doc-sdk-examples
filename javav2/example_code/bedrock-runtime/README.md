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

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console)
> before you have requested access to it, you will receive an error message. For more information,
> see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
>
<!--custom.prerequisites.end-->
### AI21 Labs Jurassic-2

- [Converse](src/main/java/com/example/bedrockruntime/models/ai21LabsJurassic2/Converse.java#L6)
- [InvokeModel](src/main/java/com/example/bedrockruntime/models/ai21LabsJurassic2/InvokeModel.java#L6)

### Amazon Nova

- [Converse](src/main/java/com/example/bedrockruntime/models/amazon/nova/text/ConverseAsync.java#L6)
- [ConverseStream](src/main/java/com/example/bedrockruntime/models/amazon/nova/text/ConverseStream.java#L6)

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
- [InvokeModel: Llama 3](src/main/java/com/example/bedrockruntime/models/metaLlama/Llama3_InvokeModel.java#L6)
- [InvokeModelWithResponseStream: Llama 3](src/main/java/com/example/bedrockruntime/models/metaLlama/Llama3_InvokeModelWithResponseStream.java#L6)

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
