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

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](kms_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAlias](alias_management.py#L89)
- [CreateGrant](grant_management.py#L38)
- [CreateKey](key_management.py#L39)
- [Decrypt](key_encryption.py#L66)
- [DeleteAlias](alias_management.py#L172)
- [DescribeKey](key_management.py#L110)
- [DisableKey](key_management.py#L175)
- [EnableKey](key_management.py#L156)
- [EnableKeyRotation](key_management.py#L216)
- [Encrypt](key_encryption.py#L37)
- [GenerateDataKey](key_management.py#L132)
- [GetKeyPolicy](key_policies.py#L62)
- [ListAliases](alias_management.py#L113)
- [ListGrants](grant_management.py#L67)
- [ListKeyPolicies](key_policies.py#L39)
- [ListKeys](key_management.py#L82)
- [PutKeyPolicy](key_policies.py#L91)
- [ReEncrypt](key_encryption.py#L88)
- [RetireGrant](grant_management.py#L95)
- [RevokeGrant](grant_management.py#L115)
- [ScheduleKeyDeletion](key_management.py#L189)
- [Sign](key_encryption.py#L123)
- [TagResource](key_management.py#L235)
- [UpdateAlias](alias_management.py#L143)
- [Verify](key_encryption.py#L147)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


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

Start the example by running the following at a command prompt:

```
python kms_scenario.py
```


<!--custom.basics.kms_Scenario_Basics.start-->
<!--custom.basics.kms_Scenario_Basics.end-->


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