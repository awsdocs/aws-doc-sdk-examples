# Amazon SES v2 API code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Simple Email Service v2 API.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SES v2 API is a reliable, scalable, and cost-effective email service._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateContact](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L30)
- [CreateContactList](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L74)
- [CreateEmailIdentity](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L116)
- [CreateEmailTemplate](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L172)
- [DeleteContactList](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L223)
- [DeleteEmailIdentity](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L265)
- [DeleteEmailTemplate](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L307)
- [ListContacts](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L344)
- [SendEmail](NewsLetterWorkflow/Sesv2Scenario/SESv2Wrapper.cs#L381)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Newsletter workflow](NewsLetterWorkflow/Sesv2Scenario/NewsletterWorkflow.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Newsletter workflow

This example shows you how to Amazon SES v2 API newsletter workflow.


<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.start-->
<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.end-->


<!--custom.scenarios.sesv2_NewsletterWorkflow.start-->
<!--custom.scenarios.sesv2_NewsletterWorkflow.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES v2 API Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES v2 API API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [SDK for .NET Amazon SES v2 API reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Sesv2/NSesv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0