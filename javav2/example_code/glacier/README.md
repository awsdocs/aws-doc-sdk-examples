# Amazon Glacier code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Glacier.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Glacier provides durable and extremely low-cost storage for infrequently used data with security features for data archiving and backup._

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

- [CreateVault](src/main/java/com/example/glacier/CreateVault.java#L6)
- [DeleteArchive](src/main/java/com/example/glacier/DeleteArchive.java#L6)
- [DeleteVault](src/main/java/com/example/glacier/DeleteVault.java#L6)
- [InitiateJob](src/main/java/com/example/glacier/ArchiveDownload.java#L6)
- [ListVaults](src/main/java/com/example/glacier/ListVaults.java#L6)
- [UploadArchive](src/main/java/com/example/glacier/UploadArchive.java#L6)


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

- [Amazon Glacier Developer Guide](https://docs.aws.amazon.com/amazonglacier/latest/dev/introduction.html)
- [Amazon Glacier API Reference](https://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-api.html)
- [SDK for Java 2.x Amazon Glacier reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/glacier/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
