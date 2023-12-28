# IAM code examples for the SDK for Go V1

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for Go V1 to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `go` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](None) (`ListPolicies`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](None) (`AttachRolePolicy`)
- [Create a policy](None) (`CreatePolicy`)
- [Create a role](None) (`CreateRole`)
- [Create a service-linked role](None) (`CreateServiceLinkedRole`)
- [Create a user](None) (`CreateUser`)
- [Create an access key](None) (`CreateAccessKey`)
- [Create an inline policy for a user](None) (`PutUserPolicy`)
- [Delete a policy](None) (`DeletePolicy`)
- [Delete a role](None) (`DeleteRole`)
- [Delete a service-linked role](None) (`DeleteServiceLinkedRole`)
- [Delete a user](None) (`DeleteUser`)
- [Delete an access key](None) (`DeleteAccessKey`)
- [Delete an inline policy from a user](None) (`DeleteUserPolicy`)
- [Detach a policy from a role](None) (`DetachRolePolicy`)
- [Get a policy](None) (`GetPolicy`)
- [Get a role](None) (`GetRole`)
- [Get a user](None) (`GetUser`)
- [Get the account password policy](None) (`GetAccountPasswordPolicy`)
- [List SAML providers](None) (`ListSAMLProviders`)
- [List a user's access keys](None) (`ListAccessKeys`)
- [List groups](None) (`ListGroups`)
- [List inline policies for a role](None) (`ListRolePolicies`)
- [List inline policies for a user](None) (`ListUserPolicies`)
- [List policies](None) (`ListPolicies`)
- [List policies attached to a role](None) (`ListAttachedRolePolicies`)
- [List roles](None) (`ListRoles`)
- [List users](None) (`ListUsers`)


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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `go` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Go V1 IAM reference](https://pkg.go.dev/github.com/aws/aws-sdk-go/service/iam)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0