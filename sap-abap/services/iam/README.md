# IAM code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

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

### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](#awsex#cl_iam_actions.clas.abap#L689)
- [AttachUserPolicy](#awsex#cl_iam_actions.clas.abap#L573)
- [CreateAccessKey](#awsex#cl_iam_actions.clas.abap#L377)
- [CreateAccountAlias](#awsex#cl_iam_actions.clas.abap#L782)
- [CreatePolicy](#awsex#cl_iam_actions.clas.abap#L472)
- [CreatePolicyVersion](#awsex#cl_iam_actions.clas.abap#L549)
- [CreateRole](#awsex#cl_iam_actions.clas.abap#L613)
- [CreateServiceLinkedRole](#awsex#cl_iam_actions.clas.abap#L972)
- [CreateUser](#awsex#cl_iam_actions.clas.abap#L298)
- [DeleteAccessKey](#awsex#cl_iam_actions.clas.abap#L397)
- [DeleteAccountAlias](#awsex#cl_iam_actions.clas.abap#L802)
- [DeletePolicy](#awsex#cl_iam_actions.clas.abap#L496)
- [DeletePolicyVersion](#awsex#cl_iam_actions.clas.abap#L1015)
- [DeleteRole](#awsex#cl_iam_actions.clas.abap#L636)
- [DeleteUser](#awsex#cl_iam_actions.clas.abap#L320)
- [DetachRolePolicy](#awsex#cl_iam_actions.clas.abap#L710)
- [DetachUserPolicy](#awsex#cl_iam_actions.clas.abap#L594)
- [GenerateCredentialReport](#awsex#cl_iam_actions.clas.abap#L871)
- [GetAccessKeyLastUsed](#awsex#cl_iam_actions.clas.abap#L454)
- [GetAccountAuthorizationDetails](#awsex#cl_iam_actions.clas.abap#L837)
- [GetAccountPasswordPolicy](#awsex#cl_iam_actions.clas.abap#L913)
- [GetAccountSummary](#awsex#cl_iam_actions.clas.abap#L854)
- [GetCredentialReport](#awsex#cl_iam_actions.clas.abap#L890)
- [GetPolicy](#awsex#cl_iam_actions.clas.abap#L532)
- [GetRole](#awsex#cl_iam_actions.clas.abap#L655)
- [ListAccessKeys](#awsex#cl_iam_actions.clas.abap#L416)
- [ListAccountAliases](#awsex#cl_iam_actions.clas.abap#L820)
- [ListAttachedRolePolicies](#awsex#cl_iam_actions.clas.abap#L729)
- [ListGroups](#awsex#cl_iam_actions.clas.abap#L765)
- [ListPolicies](#awsex#cl_iam_actions.clas.abap#L515)
- [ListPolicyVersions](#awsex#cl_iam_actions.clas.abap#L972)
- [ListRolePolicies](#awsex#cl_iam_actions.clas.abap#L747)
- [ListRoles](#awsex#cl_iam_actions.clas.abap#L672)
- [ListSAMLProviders](#awsex#cl_iam_actions.clas.abap#L932)
- [ListUsers](#awsex#cl_iam_actions.clas.abap#L339)
- [SetDefaultPolicyVersion](#awsex#cl_iam_actions.clas.abap#L992)
- [UpdateAccessKey](#awsex#cl_iam_actions.clas.abap#L434)
- [UpdateUser](#awsex#cl_iam_actions.clas.abap#L356)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for SAP ABAP IAM reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/iam/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
