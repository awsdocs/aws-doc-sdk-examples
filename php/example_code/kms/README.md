# AWS KMS code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with AWS Key Management Service (AWS KMS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS KMS is an encryption and key management service scaled for the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS KMS](HelloKMS.php#L5) (`ListKeys`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](KmsBasics.php)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAlias](KmsService.php#L140)
- [CreateGrant](KmsService.php#L164)
- [CreateKey](KmsService.php#L35)
- [Decrypt](KmsService.php#L64)
- [DeleteAlias](KmsService.php#L460)
- [DescribeKey](KmsService.php#L195)
- [DisableKey](KmsService.php#L218)
- [EnableKey](KmsService.php#L238)
- [Encrypt](KmsService.php#L88)
- [ListAliases](KmsService.php#L112)
- [ListGrants](KmsService.php#L284)
- [ListKeys](KmsService.php#L260)
- [PutKeyPolicy](KmsService.php#L438)
- [RevokeGrant](KmsService.php#L325)
- [ScheduleKeyDeletion](KmsService.php#L346)
- [Sign](KmsService.php#L390)
- [TagResource](KmsService.php#L368)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS KMS

This example shows you how to get started using AWS KMS.


#### Learn the basics

This example shows you how to do the following:

- Create a KMS key.
- List KMS keys for your account and get details about them.
- Enable and disable KMS keys.
- Generate a symmetric data key that can be used for client-side encryption.
- Generate an asymmetric key used to digitally sign data.
- Tag keys.
- Delete KMS keys.

<!--custom.basic_prereqs.kms_Scenario_Basics.start-->
<!--custom.basic_prereqs.kms_Scenario_Basics.end-->


<!--custom.basics.kms_Scenario_Basics.start-->
<!--custom.basics.kms_Scenario_Basics.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS KMS Developer Guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API Reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [SDK for PHP AWS KMS reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Kms.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
