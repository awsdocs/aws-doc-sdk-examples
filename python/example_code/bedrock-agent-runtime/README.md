# Amazon Bedrock Agents Runtime code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Bedrock Agents Runtime.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Bedrock Agents Runtime offers you the ability to run autonomous agents in your application._

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
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](flows/flow-conversation.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [InvokeAgent](bedrock_agent_runtime_wrapper.py#L33)
- [InvokeFlow](bedrock_agent_runtime_wrapper.py#L71)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to use InvokeFlow to converse with an Amazon Bedrock flow that includes an agent node.


<!--custom.basic_prereqs.bedrock-agent-runtime_Scenario_ConverseWithFlow.start-->
<!--custom.basic_prereqs.bedrock-agent-runtime_Scenario_ConverseWithFlow.end-->

Start the example by running the following at a command prompt:

```
python flows/flow-conversation.py
```


<!--custom.basics.bedrock-agent-runtime_Scenario_ConverseWithFlow.start-->
<!--custom.basics.bedrock-agent-runtime_Scenario_ConverseWithFlow.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Agents Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)
- [Amazon Bedrock Agents Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/API_Operations_Agents_for_Amazon_Bedrock_Runtime.html)
- [SDK for Python Amazon Bedrock Agents Runtime reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock-agent-runtime.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
