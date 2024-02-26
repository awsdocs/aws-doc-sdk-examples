# Agents for Amazon Bedrock code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Agents for Amazon Bedrock.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create an agent](bedrock_agent_wrapper.py#L33) (`CreateAgent`)
- [Create an agent action group](bedrock_agent_wrapper.py#L62) (`CreateAgentActionGroup`)
- [Create an agent alias](bedrock_agent_wrapper.py#L97) (`CreateAgentAlias`)
- [Delete an agent](bedrock_agent_wrapper.py#L119) (`DeleteAgent`)
- [Delete an agent alias](bedrock_agent_wrapper.py#L140) (`DeleteAgentAlias`)
- [Get information about an agent](bedrock_agent_wrapper.py#L162) (`GetAgent`)
- [List the action groups for an agent](bedrock_agent_wrapper.py#L209) (`ListAgentActionGroups`)
- [List the available agents](bedrock_agent_wrapper.py#L186) (`ListAgents`)
- [List the knowledge bases associated with an agent](bedrock_agent_wrapper.py#L238) (`ListAgentKnowledgeBases`)
- [Prepare an agent](bedrock_agent_wrapper.py#L267) (`PrepareAgent`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create and invoke an agent](scenario_get_started_with_agents.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create and invoke an agent

This example shows you how to do the following:

- Create an execution role for the agent.
- Create the agent and deploy a DRAFT version.
- Create a Lambda function that implements the agent's capabilities.
- Create an action group that connects the agent to the Lambda function.
- Deploy the fully configured agent.
- Invoke the agent with user-provided prompts.
- Delete all created resources.

<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockAgents.start-->
<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockAgents.end-->

Start the example by running the following at a command prompt:

```
python scenario_get_started_with_agents.py
```


<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockAgents.start-->
<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockAgents.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Agents for Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)
- [Agents for Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/API_Operations_Agents_for_Amazon_Bedrock.html)
- [SDK for Python Agents for Amazon Bedrock reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock-agent.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0