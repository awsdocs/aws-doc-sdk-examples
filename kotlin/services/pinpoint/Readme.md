# Amazon Pinpoint code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Pinpoint.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a campaign](src/main/kotlin/com/kotlin/pinpoint/CreateCampaign.kt#L52) (`CreateCampaign`)
- [Create a segment](src/main/kotlin/com/kotlin/pinpoint/CreateSegment.kt#L55) (`CreateSegment`)
- [Create an application](src/main/kotlin/com/kotlin/pinpoint/CreateApp.kt#L46) (`CreateApp`)
- [Delete an application](src/main/kotlin/com/kotlin/pinpoint/DeleteApp.kt#L44) (`DeleteApp`)
- [Delete an endpoint](src/main/kotlin/com/kotlin/pinpoint/DeleteEndpoint.kt#L46) (`DeleteEndpoint`)
- [Get endpoints](src/main/kotlin/com/kotlin/pinpoint/LookUpEndpoint.kt#L48) (`GetEndpoint`)
- [List segments](src/main/kotlin/com/kotlin/pinpoint/ListSegments.kt#L44) (`GetSegments`)
- [Send email and text messages](src/main/kotlin/com/kotlin/pinpoint/SendEmailMessage.kt#L58) (`SendMessages`)


<!--custom.examples.start-->

### Custom Examples

- **AddExampleEndpoint** - Demonstrates how to update an existing endpoint.
- **CreateEndpoint** - Demonstrates how to create an endpoint for an application in Amazon Pinpoint.
- **ListEndpointIds** - Demonstrates how to retrieve information about all the endpoints that are associated with a specific user ID.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Pinpoint Developer Guide](https://docs.aws.amazon.com/pinpoint/latest/developerguide/welcome.html)
- [Amazon Pinpoint API Reference](https://docs.aws.amazon.com/pinpoint/latest/apireference/welcome.html)
- [SDK for Kotlin Amazon Pinpoint reference](https://sdk.amazonaws.com/kotlin/api/latest/pinpoint/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0