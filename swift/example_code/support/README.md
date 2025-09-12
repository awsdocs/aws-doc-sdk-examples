# Support code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with AWS Support.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Support](hello/Package.swift#L8) (`DescribeServices`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario/Package.swift)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddAttachmentsToSet](scenario/Sources/Scenario.swift#L456)
- [AddCommunicationToCase](scenario/Sources/Scenario.swift#L505)
- [CreateCase](scenario/Sources/Scenario.swift#L354)
- [DescribeAttachment](scenario/Sources/Scenario.swift#L593)
- [DescribeCases](scenario/Sources/Scenario.swift#L403)
- [DescribeCommunications](scenario/Sources/Scenario.swift#L555)
- [DescribeServices](scenario/Sources/Scenario.swift#L279)
- [DescribeSeverityLevels](scenario/Sources/Scenario.swift#L315)
- [ResolveCase](scenario/Sources/Scenario.swift#L633)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

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
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Support User Guide](https://docs.aws.amazon.com/awssupport/latest/user/getting-started.html)
- [Support API Reference](https://docs.aws.amazon.com/awssupport/latest/APIReference/welcome.html)
- [SDK for Swift Support reference](https://sdk.amazonaws.com/swift/api/awssupport/latest/documentation/awssupport)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
