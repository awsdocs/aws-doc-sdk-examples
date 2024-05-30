# Amazon Bedrock Runtime code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->

> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console)
> before you have requested access to it, you will receive an error message. For more information,
> see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).

<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Bedrock](hello.js) (`InvokeModel`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Invoke multiple foundation models on Amazon Bedrock](scenarios/cli_text_playground.js)

### AI21 Labs Jurassic-2

- [Converse API](models/old/ai21_labs_jurassic2/converse.js#L4)
- [InvokeModel API](models/ai21_labs_jurassic2/jurassic2.js)

### Amazon Titan Text

- [Converse API](models/old/amazon_titan/converse.js#L4)
- [Converse API with response stream](models/amazonTitanText/converseStream.js#L4)
- [InvokeModel API](models/amazon_titan/titan_text.js)

### Anthropic Claude

- [Converse API](models/old/anthropic_claude/converse.js#L4)
- [Converse API with response stream](models/anthropicClaude/converseStream.js#L4)
- [InvokeModel API](models/anthropic_claude/claude_3.js)
- [InvokeModel API with response stream](models/anthropic_claude/claude_3.js)

### Cohere Command

- [All models: Converse API](models/cohereCommand/converse.js#L4)
- [All models: Converse API with response stream](models/cohereCommand/converseStream.js#L4)

### Meta Llama

- [All models: Converse API](models/metaLlama/converse.js#L4)
- [All models: Converse API with response stream](models/metaLlama/converseStream.js#L4)
- [Llama 2: InvokeModel API](models/meta/llama2/invoke_model_quickstart.js#L4)
- [Llama 2: InvokeModel API with a response stream](models/meta/llama2/invoke_model_with_response_stream_quickstart.js#L4)
- [Llama 3: InvokeModel API](models/meta/llama3/invoke_model_quickstart.js#L4)
- [Llama 3: InvokeModel API with a response stream](models/meta/llama3/invoke_model_with_response_stream_quickstart.js#L4)

### Mistral AI

- [Converse API](models/mistral/converse.js#L4)
- [Converse API with response stream](models/mistral/converseStream.js#L4)
- [InvokeModel API](models/mistral_ai/mistral_7b.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**
Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Bedrock

This example shows you how to get started using Amazon Bedrock.

```bash
node ./hello.js
```


#### Invoke multiple foundation models on Amazon Bedrock

This example shows you how to prepare and send a prompt to a variety of large-language models (LLMs) on Amazon Bedrock


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_InvokeModels.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_InvokeModels.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_InvokeModels.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_InvokeModels.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for JavaScript (v3) Amazon Bedrock Runtime reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/bedrock-runtime)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0