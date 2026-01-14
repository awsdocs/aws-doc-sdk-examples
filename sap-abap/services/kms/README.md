# AWS KMS code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with AWS Key Management Service (AWS KMS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS KMS](%23awsex%23cl_kms_actions.clas.abap#L271) (`ListKeys`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAlias](%23awsex%23cl_kms_actions.clas.abap#L457)
- [CreateGrant](%23awsex%23cl_kms_actions.clas.abap#L546)
- [CreateKey](%23awsex%23cl_kms_actions.clas.abap#L225)
- [Decrypt](%23awsex%23cl_kms_actions.clas.abap#L738)
- [DeleteAlias](%23awsex%23cl_kms_actions.clas.abap#L526)
- [DescribeKey](%23awsex%23cl_kms_actions.clas.abap#L288)
- [DisableKey](%23awsex%23cl_kms_actions.clas.abap#L355)
- [EnableKey](%23awsex%23cl_kms_actions.clas.abap#L335)
- [EnableKeyRotation](%23awsex%23cl_kms_actions.clas.abap#L399)
- [Encrypt](%23awsex%23cl_kms_actions.clas.abap#L712)
- [GenerateDataKey](%23awsex%23cl_kms_actions.clas.abap#L309)
- [GetKeyPolicy](%23awsex%23cl_kms_actions.clas.abap#L642)
- [ListAliases](%23awsex%23cl_kms_actions.clas.abap#L485)
- [ListGrants](%23awsex%23cl_kms_actions.clas.abap#L574)
- [ListKeyPolicies](%23awsex%23cl_kms_actions.clas.abap#L692)
- [ListKeys](%23awsex%23cl_kms_actions.clas.abap#L271)
- [PutKeyPolicy](%23awsex%23cl_kms_actions.clas.abap#L665)
- [ReEncrypt](%23awsex%23cl_kms_actions.clas.abap#L766)
- [RetireGrant](%23awsex%23cl_kms_actions.clas.abap#L594)
- [RevokeGrant](%23awsex%23cl_kms_actions.clas.abap#L616)
- [ScheduleKeyDeletion](%23awsex%23cl_kms_actions.clas.abap#L375)
- [Sign](%23awsex%23cl_kms_actions.clas.abap#L796)
- [TagResource](%23awsex%23cl_kms_actions.clas.abap#L423)
- [UpdateAlias](%23awsex%23cl_kms_actions.clas.abap#L502)
- [Verify](%23awsex%23cl_kms_actions.clas.abap#L826)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS KMS

This example shows you how to get started using AWS KMS.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS KMS Developer Guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API Reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP AWS KMS reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/kms/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
