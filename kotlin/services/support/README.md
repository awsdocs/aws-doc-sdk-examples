# Support code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS Support.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Support](src/main/kotlin/com/example/support/HelloSupport.kt#L9) (`DescribeServices`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/example/support/SupportScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddAttachmentsToSet](src/main/kotlin/com/example/support/SupportScenario.kt#L201)
- [AddCommunicationToCase](src/main/kotlin/com/example/support/SupportScenario.kt#L178)
- [CreateCase](src/main/kotlin/com/example/support/SupportScenario.kt#L247)
- [DescribeAttachment](src/main/kotlin/com/example/support/SupportScenario.kt#L143)
- [DescribeCases](src/main/kotlin/com/example/support/SupportScenario.kt#L223)
- [DescribeCommunications](src/main/kotlin/com/example/support/SupportScenario.kt#L157)
- [DescribeServices](src/main/kotlin/com/example/support/SupportScenario.kt#L293)
- [DescribeSeverityLevels](src/main/kotlin/com/example/support/SupportScenario.kt#L272)
- [ResolveCase](src/main/kotlin/com/example/support/SupportScenario.kt#L130)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


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


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

    Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

<!--custom.tests.end-->

## Additional resources

- [Support User Guide](https://docs.aws.amazon.com/awssupport/latest/user/getting-started.html)
- [Support API Reference](https://docs.aws.amazon.com/awssupport/latest/APIReference/welcome.html)
- [SDK for Kotlin Support reference](https://sdk.amazonaws.com/kotlin/api/latest/support/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0