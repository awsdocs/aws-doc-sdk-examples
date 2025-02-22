# AWS IoT SiteWise code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS IoT SiteWise.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT SiteWise _

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

- [Hello AWS IoT SiteWise](src/main/java/com/example/iotsitewise/HelloSitewise.java#L14) (`ListAssetModels`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/iotsitewise/scenario/SitewiseScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchPutAssetPropertyValue](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L169)
- [CreateAsset](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L138)
- [CreateAssetModel](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L88)
- [CreateGateway](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L468)
- [CreatePortal](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L347)
- [DeleteAsset](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L293)
- [DeleteAssetModel](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L320)
- [DeleteGateway](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L513)
- [DeletePortal](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L381)
- [DescribeAssetModel](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L264)
- [DescribeGateway](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L540)
- [DescribePortal](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L439)
- [GetAssetPropertyValue](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L233)
- [ListAssetModels](src/main/java/com/example/iotsitewise/scenario/SitewiseActions.java#L408)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT SiteWise

This example shows you how to get started using AWS IoT SiteWise.


#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT SiteWise Asset Model.
- Create an AWS IoT SiteWise Asset.
- Retrieve the property ID values.
- Send data to an AWS IoT SiteWise Asset.
- Retrieve the value of the AWS IoT SiteWise Asset property.
- Create an AWS IoT SiteWise Portal.
- Create an AWS IoT SiteWise Gateway.
- Describe the AWS IoT SiteWise Gateway.
- Delete the AWS IoT SiteWise Assets.

<!--custom.basic_prereqs.iotsitewise_Scenario.start-->
<!--custom.basic_prereqs.iotsitewise_Scenario.end-->


<!--custom.basics.iotsitewise_Scenario.start-->
<!--custom.basics.iotsitewise_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT SiteWise Developer Guide](https://docs.aws.amazon.com/iot-sitewise/latest/userguide/what-is-sitewise.html)
- [AWS IoT SiteWise API Reference](https://docs.aws.amazon.com/iot-sitewise/latest/APIReference/Welcome.html)
- [SDK for Java 2.x AWS IoT SiteWise reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/iotsitewise/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
