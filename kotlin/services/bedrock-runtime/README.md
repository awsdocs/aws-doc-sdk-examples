# Amazon Bedrock Runtime code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Bedrock Runtime

<!--custom.overview.start-->
This section provides examples that show how to invoke foundation models using the Amazon Bedrock Runtime API with the AWS SDK for Kotlin.
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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.

<!--custom.prerequisites.start-->
> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [InvokeModel](./src/main/kotlin/com/example/bedrockruntime/InvokeModel.kt) (Demonstrates how to invoke a foundation model to generate content)

<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

<!--custom.instructions.start-->
<!--custom.instructions.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.

<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Kotlin Amazon Bedrock reference](https://sdk.amazonaws.com/kotlin/api/latest/bedrock/index.html)
- [SDK for Kotlin Amazon Bedrock Runtime reference](https://sdk.amazonaws.com/kotlin/api/latest/bedrock-runtime/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

### Input and Output Format

**Input:**

```json
{
  "inputText": "Your prompt here.",
  "textGenerationConfig": {
      "maxTokenCount": 2000,
      "stopSequences": [],
      "temperature": 1.0,
      "topP": 0.7
  }
}
```
You may want to check the doc page [Inference Request Parameters](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters.html) on the AWS Bedrock documentation for more information on the parameters.
The structure of the input varies depending on the model you are using.

**Output:**

```json
/* Body only */
{
  "inputTextTokenCount":7,
  "results":[
    {"tokenCount":9,
      "outputText":"Generated answer.",
      "completionReason":"FINISH"
    }
  ]
}
```

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0