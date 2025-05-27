# Amazon Bedrock Runtime code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Tool use with the Converse API](src/bin/tool-use.rs)

### Anthropic Claude

- [Converse](src/bin/converse.rs#L43)
- [ConverseStream](src/bin/converse-stream.rs#L70)
- [Scenario: Tool use with the Converse API](src/bin/tool-use.rs#L242)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Tool use with the Converse API

This example shows you how to build a typical interaction between an application, a generative AI model, and connected tools or APIs to mediate interactions between the AI and the outside world. It uses the example of connecting an external weather API to the AI model so it can provide real-time weather information based on user input.


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_ToolUse.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_ToolUse.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Rust Amazon Bedrock Runtime reference](https://docs.rs/aws-sdk-bedrockruntime/latest/aws_sdk_bedrockruntime/))

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
