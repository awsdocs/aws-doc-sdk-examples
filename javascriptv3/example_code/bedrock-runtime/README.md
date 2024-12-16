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

- [Hello Amazon Bedrock Runtime](hello.js) (`InvokeModel`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Invoke multiple foundation models on Amazon Bedrock](scenarios/cli_text_playground.js)

### AI21 Labs Jurassic-2

- [Converse](models/ai21LabsJurassic2/converse.js#L4)
- [InvokeModel](models/ai21LabsJurassic2/invoke_model.js)

### Amazon Titan Text

- [Converse](models/amazonTitanText/converse.js#L4)
- [ConverseStream](models/amazonTitanText/converseStream.js#L4)
- [InvokeModel](models/amazonTitanText/invoke_model.js)

### Anthropic Claude

- [Converse](models/anthropicClaude/converse.js#L4)
- [ConverseStream](models/anthropicClaude/converseStream.js#L4)
- [InvokeModel](models/anthropicClaude/invoke_claude_3.js)
- [InvokeModelWithResponseStream](models/anthropicClaude/invoke_claude_3.js)

### Cohere Command

- [Converse](models/cohereCommand/converse.js#L4)
- [ConverseStream](models/cohereCommand/converseStream.js#L4)

### Meta Llama

- [Converse](models/metaLlama/converse.js#L4)
- [ConverseStream](models/metaLlama/converseStream.js#L4)
- [InvokeModel: Llama 3](models/metaLlama/llama3/invoke_model_quickstart.js#L4)
- [InvokeModelWithResponseStream: Llama 3](models/metaLlama/llama3/invoke_model_with_response_stream_quickstart.js#L4)

### Mistral AI

- [Converse](models/mistral/converse.js#L4)
- [ConverseStream](models/mistral/converseStream.js#L4)
- [InvokeModel](models/mistral/invoke_mistral_7b.js)


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

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Bedrock Runtime

This example shows you how to get started using Amazon Bedrock Runtime.

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
