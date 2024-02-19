# Agents for Amazon Bedrock code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Agents for Amazon Bedrock.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Agents for Amazon Bedrock offer you the ability to build and configure autonomous agents in your application._

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
> ⚠ You must request access to a foundation model before you can use it. If you try to use the foundation model before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
<!--custom.prerequisites.end-->

### Get started

- [Hello Agents for Amazon Bedrock](hello.js) (`ListAgents`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create an agent](actions/create-agent.js) (`CreateAgent`)
- [Delete an agent](actions/delete-agent.js) (`DeleteAgent`)
- [Get information about an agent](actions/get-agent.js) (`GetAgent`)
- [List the action groups for an agent](actions/list-agent-action-groups.js) (`ListAgentActionGroups`)
- [List the available agents](actions/list-agents.js) (`ListAgents`)


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

#### Hello Agents for Amazon Bedrock

This example shows you how to get started using Agents for Amazon Bedrock.

```bash
node ./hello.js
```


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Agents for Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)
- [Agents for Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/API_Operations_Agents_for_Amazon_Bedrock.html)
- [SDK for JavaScript (v3) Agents for Amazon Bedrock reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/bedrock-agent)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0