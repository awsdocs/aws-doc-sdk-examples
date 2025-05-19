# Amazon Bedrock Agents code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Bedrock Agents.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Bedrock Agents offer you the ability to build and configure autonomous agents in your application._

## ⚠ Important

- Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

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

- [CreateAgent](bedrock_agent_wrapper.py#L32)
- [CreateAgentActionGroup](bedrock_agent_wrapper.py#L61)
- [CreateAgentAlias](bedrock_agent_wrapper.py#L96)
- [CreateFlow](flows/flow.py#L18)
- [CreateFlowAlias](flows/flow_alias.py#L15)
- [CreateFlowVersion](flows/flow_version.py#L18)
- [CreateKnowledgeBase](knowledge_bases/knowledge_base.py#L29)
- [CreatePrompt](prompts/prompt.py#L18)
- [CreatePromptVersion](prompts/prompt.py#L84)
- [DeleteAgent](bedrock_agent_wrapper.py#L118)
- [DeleteAgentAlias](bedrock_agent_wrapper.py#L139)
- [DeleteFlow](flows/flow.py#L155)
- [DeleteFlowAlias](flows/flow_alias.py#L98)
- [DeleteFlowVersion](flows/flow_version.py#L91)
- [DeleteKnowledgeBase](knowledge_bases/knowledge_base.py#L165)
- [DeletePrompt](prompts/prompt.py#L159)
- [GetAgent](bedrock_agent_wrapper.py#L161)
- [GetFlow](flows/flow.py#L192)
- [GetFlowVersion](flows/flow_version.py#L54)
- [GetKnowledgeBase](knowledge_bases/knowledge_base.py#L86)
- [GetPrompt](prompts/prompt.py#L124)
- [ListAgentActionGroups](bedrock_agent_wrapper.py#L208)
- [ListAgentKnowledgeBases](bedrock_agent_wrapper.py#L237)
- [ListAgents](bedrock_agent_wrapper.py#L185)
- [ListFlowAliases](flows/flow_alias.py#L132)
- [ListFlowVersions](flows/flow_version.py#L128)
- [ListFlows](flows/flow.py#L229)
- [ListKnowledgeBases](knowledge_bases/knowledge_base.py#L195)
- [ListPrompts](prompts/prompt.py#L191)
- [PrepareAgent](bedrock_agent_wrapper.py#L266)
- [PrepareFlow](flows/flow.py#L58)
- [UpdateFlow](flows/flow.py#L112)
- [UpdateFlowAlias](flows/flow_alias.py#L55)
- [UpdateKnowledgeBase](knowledge_bases/knowledge_base.py#L116)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create and invoke a flow](flows/playlist_flow.py)
- [Create and invoke a managed prompt](prompts/scenario_get_started_with_prompts.py)
- [Create and invoke an agent](scenario_get_started_with_agents.py)

<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Create and invoke a flow

This example shows you how to do the following:

- Create an execution role for the flow.
- Create the flow.
- Deploy the fully configured flow.
- Invoke the flow with user-provided prompts.
- Delete all created resources.

<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockFlows.start-->

The flow includes a prompt node that generates a playlist for a chosen genre
and number of songs. The example creates the nodes and permissions
for the flow.

<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockFlows.end-->

Start the example by running the following at a command prompt:

```
python flows/playlist_flow.py
```

<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockFlows.start-->

When prompted, enter the genre of music and the number of songs you want
in the playlist.
Optionally, the script can delete the resources that it creates. If you want to use the flow later, such as in the Amazon Bedrock console, enter `n` when the script prompts you to delete resources. Note that you will then need to manually delete the resources.

<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockFlows.end-->

#### Create and invoke a managed prompt

This example shows you how to do the following:

- Create a managed prompt.
- Create a version of the prompt.
- Invoke the prompt using the version.
- Clean up resources (optional).

<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockPrompts.start-->
<!--custom.scenario_prereqs.bedrock-agent_GettingStartedWithBedrockPrompts.end-->

Start the example by running the following at a command prompt:

```
python prompts/scenario_get_started_with_prompts.py
```

<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockPrompts.start-->

This example shows you how to do the following:

- Create a managed prompt
- Create a version of the prompt
- Invoke the prompt using the version
- Update the prompt
- Create a new version
- Invoke the updated prompt
- Clean up resources (optional)

Start the example by running the following at a command prompt:

```
python prompts/scenario_get_started_with_prompts.py
```

By default, the example will clean up all resources it creates. If you want to keep the resources for further exploration, use the `--no-cleanup` flag:

```
python prompts/scenario_get_started_with_prompts.py --no-cleanup
```

You can also specify a different AWS region or model ID:

```
python prompts/scenario_get_started_with_prompts.py --region us-west-2 --model-id anthropic.claude-3-sonnet-20240229-v1:0
```

<!--custom.scenarios.bedrock-agent_GettingStartedWithBedrockPrompts.end-->

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

- [Amazon Bedrock Agents User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)
- [Amazon Bedrock Agents API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/API_Operations_Agents_for_Amazon_Bedrock.html)
- [SDK for Python Amazon Bedrock Agents reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock-agent.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
