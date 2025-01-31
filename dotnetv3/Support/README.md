# Support code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Support.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Support provides support for users of Amazon Web Services._

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


You must have a Business, Enterprise On-Ramp, or Enterprise Support plan to use the AWS Support API.

If you call the AWS Support API from an account that doesn't have a Business, Enterprise On-Ramp, or Enterprise Support 
plan, the SubscriptionRequiredException error message appears. For information about changing your support plan, see 
[AWS Premium Support](http://aws.amazon.com/premiumsupport/).
<!--custom.prerequisites.end-->

### Get started

- [Hello Support](Actions/HelloSupport.cs#L6) (`DescribeServices`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/SupportCaseScenario.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddAttachmentsToSet](Actions/SupportWrapper.cs#L98)
- [AddCommunicationToCase](Actions/SupportWrapper.cs#L146)
- [CreateCase](Actions/SupportWrapper.cs#L63)
- [DescribeAttachment](Actions/SupportWrapper.cs#L127)
- [DescribeCases](Actions/SupportWrapper.cs#L201)
- [DescribeCommunications](Actions/SupportWrapper.cs#L172)
- [DescribeServices](Actions/SupportWrapper.cs#L23)
- [DescribeSeverityLevels](Actions/SupportWrapper.cs#L43)
- [ResolveCase](Actions/SupportWrapper.cs#L241)


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

#### Hello Support

This example shows you how to get started using Support.


#### Learn the basics

This example shows you how to do the following:

- Get and display available services and severity levels for cases.
- Create a support case using a selected service, category, and severity level.
- Get and display a list of open cases for the current day.
- Add an attachment set and a communication to the new case.
- Describe the new attachment and communication for the case.
- Resolve the case.
- Get and display a list of resolved cases for the current day.

<!--custom.basic_prereqs.support_Scenario_GetStartedSupportCases.start-->
<!--custom.basic_prereqs.support_Scenario_GetStartedSupportCases.end-->


<!--custom.basics.support_Scenario_GetStartedSupportCases.start-->
<!--custom.basics.support_Scenario_GetStartedSupportCases.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Support User Guide](https://docs.aws.amazon.com/awssupport/latest/user/getting-started.html)
- [Support API Reference](https://docs.aws.amazon.com/awssupport/latest/APIReference/welcome.html)
- [SDK for .NET Support reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/AWSSupport/NAWSSupport.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0