# Amazon Pinpoint code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Pinpoint.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Pinpoint helps you engage your customers by sending them email, SMS and voice messages, and push notifications._

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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a campaign](src/main/java/com/example/pinpoint/CreateCampaign.java#L12) (`CreateCampaign`)
- [Create a segment](src/main/java/com/example/pinpoint/CreateSegment.java#L12) (`CreateSegment`)
- [Create an application](src/main/java/com/example/pinpoint/CreateApp.java#L11) (`CreateApp`)
- [Delete an application](src/main/java/com/example/pinpoint/DeleteApp.java#L12) (`DeleteApp`)
- [Delete an endpoint](src/main/java/com/example/pinpoint/DeleteEndpoint.java#L12) (`DeleteEndpoint`)
- [Export an endpoint](src/main/java/com/example/pinpoint/ExportEndpoints.java#L11) (`CreateExportJob`)
- [Get endpoints](src/main/java/com/example/pinpoint/LookUpEndpoint.java#L11) (`GetEndpoint`)
- [Import a segment](src/main/java/com/example/pinpoint/ImportSegment.java#L10) (`CreateImportJob`)
- [List endpoints](src/main/java/com/example/pinpoint/ListEndpointIds.java#L11) (`GetUserEndpoints`)
- [List segments](src/main/java/com/example/pinpoint/ListSegments.java#L13) (`GetSegments`)
- [Send email and text messages](src/main/java/com/example/pinpoint/SendEmailMessage.java#L12) (`SendMessages`)
- [Update an endpoint](src/main/java/com/example/pinpoint/UpdateEndpoint.java#L12) (`UpdateEndpoint`)
- [Update channels](src/main/java/com/example/pinpoint/UpdateChannel.java#L10) (`GetSmsChannel`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Pinpoint Developer Guide](https://docs.aws.amazon.com/pinpoint/latest/developerguide/welcome.html)
- [Amazon Pinpoint API Reference](https://docs.aws.amazon.com/pinpoint/latest/apireference/welcome.html)
- [SDK for Java 2.x Amazon Pinpoint reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/pinpoint/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0