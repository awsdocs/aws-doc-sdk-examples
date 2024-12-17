# ACM code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Certificate Manager (ACM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_ACM helps you to provision, manage, and renew publicly trusted TLS certificates on AWS based websites._

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

- [AddTagsToCertificate](src/main/java/com/example/acm/AddTagsToCertificate.java#L12)
- [DeleteCertificate](src/main/java/com/example/acm/DeleteCert.java#L10)
- [DescribeCertificate](src/main/java/com/example/acm/DescribeCert.java#L11)
- [ExportCertificate](src/main/java/com/example/acm/ExportCertificate.java#L20)
- [ImportCertificate](src/main/java/com/example/acm/ImportCert.java#L16)
- [ListCertificates](src/main/java/com/example/acm/ListCerts.java#L12)
- [ListTagsForCertificate](src/main/java/com/example/acm/ListCertTags.java#L13)
- [RemoveTagsFromCertificate](src/main/java/com/example/acm/RemoveTagsFromCert.java#L13)
- [RenewCertificate](src/main/java/com/example/acm/RenewCert.java#L10)
- [RequestCertificate](src/main/java/com/example/acm/RequestCert.java#L12)


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

- [ACM User Guide](https://docs.aws.amazon.com/acm/latest/userguide/acm-overview.html)
- [ACM API Reference](https://docs.aws.amazon.com/acm/latest/APIReference/Welcome.html)
- [SDK for Java 2.x ACM reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0