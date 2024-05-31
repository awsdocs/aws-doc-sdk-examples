# AWS KMS code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Key Management Service (AWS KMS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello KMS key](src/main/java/com/example/kms/HelloKMS.java#L6) (`listKeysPaginator`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAlias](src/main/java/com/example/kms/scenario/KMSScenario.java#L638)
- [CreateGrant](src/main/java/com/example/kms/scenario/KMSScenario.java#L594)
- [CreateKey](src/main/java/com/example/kms/scenario/KMSScenario.java#L684)
- [Decrypt](src/main/java/com/example/kms/scenario/KMSScenario.java#L531)
- [DescribeKey](src/main/java/com/example/kms/scenario/KMSScenario.java#L507)
- [DisableKey](src/main/java/com/example/kms/scenario/KMSScenario.java#L324)
- [EnableKey](src/main/java/com/example/kms/scenario/KMSScenario.java#L705)
- [Encrypt](src/main/java/com/example/kms/scenario/KMSScenario.java#L659)
- [ListAliases](src/main/java/com/example/kms/scenario/KMSScenario.java#L304)
- [ListGrants](src/main/java/com/example/kms/scenario/KMSScenario.java#L568)
- [ListKeyPolicies](src/main/java/com/example/kms/scenario/KMSScenario.java#L401)
- [ListKeys](src/main/java/com/example/kms/HelloKMS.java#L6)
- [RevokeGrant](src/main/java/com/example/kms/scenario/KMSScenario.java#L550)
- [ScheduleKeyDeletion](src/main/java/com/example/kms/scenario/KMSScenario.java#L473)
- [Sign](src/main/java/com/example/kms/scenario/KMSScenario.java#L340)
- [TagResource](src/main/java/com/example/kms/scenario/KMSScenario.java#L378)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Learn KMS key core operations](src/main/java/com/example/kms/scenario/KMSScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
To find instructions for running the code example, see the [README](../../README.md#build-and-run-the-service-examples) in the `javav2` folder.
<!--custom.instructions.end-->

#### Hello KMS key

This example shows you how to get started using KMS key.



#### Learn KMS key core operations

This example shows you how to do the following:

- Create a KMS key.
- List KMS keys for your account and get details about them.
- Enable and disable KMS keys.
- Generate a symmetric data key that can be used for client-side encryption.
- Generate an asymmetric key used to digitally sign data.
- Tag keys.
- Delete KMS keys.

<!--custom.scenario_prereqs.kms_Scenario_KeyManagement.start-->
<!--custom.scenario_prereqs.kms_Scenario_KeyManagement.end-->


<!--custom.scenarios.kms_Scenario_KeyManagement.start-->
<!--custom.scenarios.kms_Scenario_KeyManagement.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS KMS Developer Guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API Reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [SDK for Java 2.x AWS KMS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/kms/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0