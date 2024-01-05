# AWS KMS code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS Key Management Service (AWS KMS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS KMS is an encryption and key management service scaled for the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

- [Create a grant for a key](src/main/kotlin/com/kotlin/kms/CreateGrant.kt#L50) (`CreateGrant`)
- [Create a key](src/main/kotlin/com/kotlin/kms/CreateCustomerKey.kt#L34) (`CreateKey`)
- [Create an alias for a key](src/main/kotlin/com/kotlin/kms/CreateAlias.kt#L46) (`CreateAlias`)
- [Decrypt ciphertext](src/main/kotlin/com/kotlin/kms/EncryptDataKey.kt#L49) (`Decrypt`)
- [Describe a key](src/main/kotlin/com/kotlin/kms/DescribeKey.kt#L44) (`DescribeKey`)
- [Disable a key](src/main/kotlin/com/kotlin/kms/DisableCustomerKey.kt#L44) (`DisableKey`)
- [Enable a key](src/main/kotlin/com/kotlin/kms/EnableCustomerKey.kt#L44) (`EnableKey`)
- [Encrypt text using a key](src/main/kotlin/com/kotlin/kms/EncryptDataKey.kt#L49) (`Encrypt`)
- [List aliases for a key](src/main/kotlin/com/kotlin/kms/ListAliases.kt#L29) (`ListAliases`)
- [List grants for a key](src/main/kotlin/com/kotlin/kms/ListGrants.kt#L43) (`ListGrants`)
- [List keys](src/main/kotlin/com/kotlin/kms/ListKeys.kt#L28) (`ListKeys`)


<!--custom.examples.start-->

### Custom Examples

- **DeleteAlias** - Demonstrates how to delete an AWS KMS alias.
- **RevokeGrant** - Demonstrates how to revoke a grant for the specified AWS KMS key.

- **DeleteAlias** - Demonstrates how to delete an AWS KMS alias.
- **RevokeGrant** - Demonstrates how to revoke a grant for the specified AWS KMS key.

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

- [AWS KMS Developer Guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API Reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [SDK for Kotlin AWS KMS reference](https://sdk.amazonaws.com/kotlin/api/latest/kms/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0