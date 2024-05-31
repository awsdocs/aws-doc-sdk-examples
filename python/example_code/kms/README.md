# AWS KMS code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Key Management Service (AWS KMS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAlias](alias_management.py#L78)
- [CreateGrant](grant_management.py#L27)
- [CreateKey](key_management.py#L28)
- [Decrypt](key_encryption.py#L50)
- [DeleteAlias](alias_management.py#L164)
- [DescribeKey](key_management.py#L82)
- [DisableKey](key_management.py#L128)
- [EnableKey](key_management.py#L20)
- [Encrypt](key_encryption.py#L26)
- [GenerateDataKey](key_management.py#L104)
- [GetKeyPolicy](key_policies.py#L50)
- [ListAliases](alias_management.py#L103)
- [ListGrants](grant_management.py#L61)
- [ListKeyPolicies](key_policies.py#L28)
- [ListKeys](key_management.py#L54)
- [PutKeyPolicy](key_policies.py#L78)
- [ReEncrypt](key_encryption.py#L76)
- [RetireGrant](grant_management.py#L86)
- [RevokeGrant](grant_management.py#L106)
- [ScheduleKeyDeletion](key_management.py#L161)
- [UpdateAlias](alias_management.py#L135)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Encrypt and decrypt text](key_encryption.py)
- [Learn KMS key core operations](key_management.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Encrypt and decrypt text

This example shows you how to do the following:

- Encrypt plain text by using a KMS key.
- Decrypt ciphertext by using a KMS key.
- Reencrypt ciphertext by using a second KMS key.

<!--custom.scenario_prereqs.kms_Scenario_KeyEncryption.start-->
<!--custom.scenario_prereqs.kms_Scenario_KeyEncryption.end-->

Start the example by running the following at a command prompt:

```
python key_encryption.py
```


<!--custom.scenarios.kms_Scenario_KeyEncryption.start-->
<!--custom.scenarios.kms_Scenario_KeyEncryption.end-->

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

Start the example by running the following at a command prompt:

```
python key_management.py
```


<!--custom.scenarios.kms_Scenario_KeyManagement.start-->
<!--custom.scenarios.kms_Scenario_KeyManagement.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS KMS Developer Guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API Reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [SDK for Python AWS KMS reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kms.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0