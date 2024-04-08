# Amazon Bedrock Runtime code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with Amazon Bedrock Runtime.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Bedrock](hello/hello.go#L4) (`InvokeModel`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Image generation with Amazon Titan Image Generator G1](actions/invoke_model.go#L191) (`InvokeModel`)
- [Text generation with AI21 Labs Jurassic-2](actions/invoke_model.go#L83) (`InvokeModel`)
- [Text generation with Amazon Titan Text G1](actions/invoke_model.go#L261) (`InvokeModel`)
- [Text generation with Anthropic Claude 2](actions/invoke_model.go#L27) (`InvokeModel`)
- [Text generation with Anthropic Claude 2 with a response stream](actions/invoke_model_with_response_stream.go#L30) (`InvokeModelWithResponseStream`)
- [Text generation with Meta Llama 2 Chat](actions/invoke_model.go#L140) (`InvokeModel`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Invoke multiple foundation models on Amazon Bedrock](scenarios/scenario_invoke_models.go)


<!--custom.examples.start-->
> ⚠ You must request access to a model before you can use it. If you try to use the model (with the API or console) before you have requested access to it, you will receive an error message. For more information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
#### Region configuration
By default, examples are set to `us-east-1`. To specify a different region, use the `-region` flag as shown in this example:

```
go run ./hello -region=eu-central-1
```

Be aware that not all regions may support Bedrock and its models yet. Verify service availability for your region [here](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/). For available models per region, refer to the [Bedrock dashboard](https://console.aws.amazon.com/bedrock) in the AWS Management Console.
<!--custom.instructions.end-->

#### Hello Amazon Bedrock

This example shows you how to get started using Amazon Bedrock.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```

#### Invoke multiple foundation models on Amazon Bedrock

This example shows you how to prepare and send a prompt to a variety of large-language models (LLMs) on Amazon Bedrock


<!--custom.scenario_prereqs.bedrock-runtime_Scenario_InvokeModels.start-->
<!--custom.scenario_prereqs.bedrock-runtime_Scenario_InvokeModels.end-->


<!--custom.scenarios.bedrock-runtime_Scenario_InvokeModels.start-->
<!--custom.scenarios.bedrock-runtime_Scenario_InvokeModels.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Bedrock Runtime User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Amazon Bedrock Runtime API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/welcome.html)
- [SDK for Go V2 Amazon Bedrock Runtime reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/bedrock-runtime)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0