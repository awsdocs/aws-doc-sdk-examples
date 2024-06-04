# Amazon Bedrock Runtime code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->
### AI21 Labs Jurassic-2

- [Converse API](Models/Ai21LabsJurassic2/Converse/Converse.csx#L4)
- [InvokeModel API](Models/Ai21LabsJurassic2/InvokeModel/InvokeModel.csx#L4)

### Amazon Titan Text

- [Converse API](Models/AmazonTitanText/Converse/Converse.csx#L4)
- [Converse API with response stream](Models/AmazonTitanText/ConverseStream/ConverseStream.csx#L4)
- [InvokeModel API](Models/AmazonTitanText/InvokeModel/InvokeModel.csx#L4)
- [InvokeModel API with response stream](Models/AmazonTitanText/InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)

### Anthropic Claude

- [Converse API](Models/AnthropicClaude/Converse/Converse.csx#L4)
- [Converse API with response stream](Models/AnthropicClaude/ConverseStream/ConverseStream.csx#L4)
- [InvokeModel API](Models/AnthropicClaude/InvokeModel/InvokeModel.csx#L4)
- [InvokeModel API with response stream](Models/AnthropicClaude/InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)

### Cohere Command

- [All models: Converse API](Models/CohereCommand/Converse/Converse.csx#L4)
- [All models: Converse API with response stream](Models/CohereCommand/ConverseStream/ConverseStream.csx#L4)
- [Command R and R+: InvokeModel API](Models/CohereCommand/Command_R_InvokeModel/InvokeModel.csx#L4)
- [Command R and R+: InvokeModel API with response stream](Models/CohereCommand/Command_R_InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)
- [Command and Command Light: InvokeModel API](Models/CohereCommand/Command_InvokeModel/InvokeModel.csx#L4)
- [Command and Command Light: InvokeModel API with response stream](Models/CohereCommand/Command_InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)

### Meta Llama

- [All models: Converse API](Models/MetaLlama/Converse/Converse.csx#L4)
- [All models: Converse API with response stream](Models/MetaLlama/ConverseStream/ConverseStream.csx#L4)
- [Llama 2: InvokeModel API](Models/MetaLlama/Llama2_InvokeModel/InvokeModel.csx#L4)
- [Llama 2: InvokeModel API with a response stream](Models/MetaLlama/Llama2_InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)
- [Llama 3: InvokeModel API](Models/MetaLlama/Llama3_InvokeModel/InvokeModel.csx#L4)
- [Llama 3: InvokeModel API with a response stream](Models/MetaLlama/Llama3_InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)

### Mistral AI

- [Converse API](Models/Mistral/Converse/Converse.csx#L4)
- [Converse API with response stream](Models/Mistral/ConverseStream/ConverseStream.csx#L4)
- [InvokeModel API](Models/Mistral/InvokeModel/InvokeModel.csx#L4)
- [InvokeModel API with response stream](Models/Mistral/InvokeModelWithResponseStream/InvokeModelWithResponseStream.csx#L4)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for .NET Amazon Bedrock Runtime reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Bedrock-runtime/NBedrock-runtime.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0