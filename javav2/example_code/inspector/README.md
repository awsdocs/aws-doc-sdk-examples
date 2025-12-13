# Amazon Inspector code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Inspector.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Inspector _

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Inspector](src/main/java/com/java/inspector/HelloInspector.java#L26) (`BatchGetAccountStatus`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/java/inspector/InspectorScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchGetAccountStatus](src/main/java/com/java/inspector/InspectorActions.java#L245)
- [BatchGetFindingDetails](src/main/java/com/java/inspector/InspectorActions.java#L628)
- [CreateFilter](src/main/java/com/java/inspector/InspectorActions.java#L370)
- [DeleteFilter](src/main/java/com/java/inspector/InspectorActions.java#L598)
- [Enable](src/main/java/com/java/inspector/InspectorActions.java#L54)
- [ListCoverage](src/main/java/com/java/inspector/InspectorActions.java#L493)
- [ListCoverageStatistics](src/main/java/com/java/inspector/InspectorActions.java#L126)
- [ListFilters](src/main/java/com/java/inspector/InspectorActions.java#L324)
- [ListFindings](src/main/java/com/java/inspector/InspectorActions.java#L434)
- [ListUsageTotals](src/main/java/com/java/inspector/InspectorActions.java#L183)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Inspector

This example shows you how to get started using Amazon Inspector.


#### Learn the basics

This example shows you how to do the following:

- Check Inspector account status.
- Ensure Inspector is enabled.
- Analyze security findings.
- Check scan coverage.
- Create a findings filter.
- List existing filters.
- Check usage and costs.
- Get coverage statistics.

<!--custom.basic_prereqs.inspector_Scenario.start-->
<!--custom.basic_prereqs.inspector_Scenario.end-->


<!--custom.basics.inspector_Scenario.start-->
<!--custom.basics.inspector_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Inspector User Guide](https://docs.aws.amazon.com/inspector/latest/user/what-is-inspector.html)
- [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon Inspector reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/inspector/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
