# IAM code examples for the SDK for JavaScript (v2)

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for JavaScript (v2) to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascript` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](None) (`ListPolicies`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](iam_attachrolepolicy.js#L29) (`AttachRolePolicy`)
- [Attach an inline policy to a role](None) (`PutRolePolicy`)
- [Create a SAML provider](None) (`CreateSAMLProvider`)
- [Create a group](None) (`CreateGroup`)
- [Create a policy](iam_createpolicy.js#L29) (`CreatePolicy`)
- [Create a role](None) (`CreateRole`)
- [Create a service-linked role](None) (`CreateServiceLinkedRole`)
- [Create a user](iam_createuser.js#L29) (`CreateUser`)
- [Create an access key](iam_createaccesskeys.js#L29) (`CreateAccessKey`)
- [Create an alias for an account](iam_createaccountalias.js#L29) (`CreateAccountAlias`)
- [Create an instance profile](None) (`CreateInstanceProfile`)
- [Delete SAML provider](None) (`DeleteSAMLProvider`)
- [Delete a group](None) (`DeleteGroup`)
- [Delete a policy](None) (`DeletePolicy`)
- [Delete a role](None) (`DeleteRole`)
- [Delete a role policy](None) (`DeleteRolePolicy`)
- [Delete a server certificate](iam_deleteservercert.js#L29) (`DeleteServerCertificate`)
- [Delete a service-linked role](None) (`DeleteServiceLinkedRole`)
- [Delete a user](iam_deleteuser.js#L29) (`DeleteUser`)
- [Delete an access key](iam_deleteaccesskey.js#L29) (`DeleteAccessKey`)
- [Delete an account alias](iam_deleteaccountalias.js#L29) (`DeleteAccountAlias`)
- [Delete an instance profile](None) (`DeleteInstanceProfile`)
- [Detach a policy from a role](iam_detachrolepolicy.js#L29) (`DetachRolePolicy`)
- [Get a policy](iam_getpolicy.js#L29) (`GetPolicy`)
- [Get a role](None) (`GetRole`)
- [Get a server certificate](iam_getservercert.js#L29) (`GetServerCertificate`)
- [Get a service-linked role's deletion status](None) (`GetServiceLinkedRoleDeletionStatus`)
- [Get data about the last use of an access key](iam_accesskeylastused.js#L29) (`GetAccessKeyLastUsed`)
- [Get the account password policy](None) (`GetAccountPasswordPolicy`)
- [List SAML providers](None) (`ListSAMLProviders`)
- [List a user's access keys](iam_listaccesskeys.js#L29) (`ListAccessKeys`)
- [List account aliases](iam_listaccountaliases.js#L29) (`ListAccountAliases`)
- [List groups](None) (`ListGroups`)
- [List inline policies for a role](None) (`ListRolePolicies`)
- [List policies](None) (`ListPolicies`)
- [List policies attached to a role](None) (`ListAttachedRolePolicies`)
- [List roles](None) (`ListRoles`)
- [List server certificates](iam_listservercerts.js#L29) (`ListServerCertificates`)
- [List users](iam_listusers.js#L29) (`ListUsers`)
- [Update a server certificate](iam_updateservercert.js#L29) (`UpdateServerCertificate`)
- [Update a user](iam_updateuser.js#L29) (`UpdateUser`)
- [Update an access key](iam_updateaccesskey.js#L29) (`UpdateAccessKey`)
- [Upload a server certificate](None) (`UploadServerCertificate`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascript` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for JavaScript (v2) IAM reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Iam.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0