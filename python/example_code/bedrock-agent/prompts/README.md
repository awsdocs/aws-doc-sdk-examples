# Amazon Bedrock Managed Prompts code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Bedrock Managed Prompts.

_Amazon Bedrock Managed Prompts allow you to create, version, and manage prompt templates for use with Amazon Bedrock models._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Important Note on AWS Clients

Amazon Bedrock Managed Prompts functionality is accessed through the following AWS clients:

- `bedrock-agent` - For managing prompts, versions, and aliases
- `bedrock-agent-runtime` - For invoking prompts

Make sure to use the correct client for each operation.

## Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreatePrompt](prompt.py#L32)
- [GetPrompt](prompt.py#L71)
- [UpdatePrompt](prompt.py#L110)
- [DeletePrompt](prompt.py#L154)
- [ListPrompts](prompt.py#L187)
- [CreatePromptVersion](prompt_version.py#L18)
- [GetPromptVersion](prompt_version.py#L54)
- [DeletePromptVersion](prompt_version.py#L91)
- [ListPromptVersions](prompt_version.py#L128)
- [InvokePrompt](run_prompt.py#L23)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create and invoke a managed prompt](scenario_get_started_with_prompts.py)

## Run the examples

### Instructions

#### List prompts

Shows how to list Amazon Bedrock managed prompts, versions of a prompt, and aliases of a prompt.

Start the example by running the following at a command prompt:

```
python list_prompts.py
```

The example first lists the prompts in the current AWS Region. It
then prompts for a prompt ID, which you can get from the list of prompts. Finally, the example lists the prompt versions and prompt aliases for the prompt ID that you entered.

#### Run a prompt

Shows how to invoke an Amazon Bedrock managed prompt.

Start the example by running the following at a command prompt:

```
python run_prompt.py --prompt-id YOUR_PROMPT_ID --version-or-alias VERSION_OR_ALIAS --input-variables '{"variable1": "value1", "variable2": "value2"}'
```

Replace:
- `YOUR_PROMPT_ID` with the ID of your prompt
- `VERSION_OR_ALIAS` with either a version number or alias name
- The JSON string with the appropriate input variables for your prompt template

#### Create and invoke a managed prompt

This example shows you how to do the following:

- Create a managed prompt
- Create a version of the prompt
- Create an alias for the prompt version
- Invoke the prompt using the alias
- Update the prompt
- Create a new version
- Update the alias to point to the new version
- Invoke the updated prompt
- Clean up resources (optional)

Start the example by running the following at a command prompt:

```
python scenario_get_started_with_prompts.py
```

By default, the example will clean up all resources it creates. If you want to keep the resources for further exploration, use the `--no-cleanup` flag:

```
python scenario_get_started_with_prompts.py --no-cleanup
```

You can also specify a different AWS region or model ID:

```
python scenario_get_started_with_prompts.py --region us-west-2 --model-id anthropic.claude-3-sonnet-20240229-v1:0
```

## Additional resources

- [Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Python Amazon Bedrock reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/bedrock.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
