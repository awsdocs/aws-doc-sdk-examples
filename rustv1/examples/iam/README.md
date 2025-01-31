# IAM code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](src/bin/hello.rs#L3) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/bin/iam-getting-started.rs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](src/iam-service-lib.rs#L190)
- [AttachUserPolicy](src/iam-service-lib.rs#L205)
- [CreateAccessKey](src/iam-service-lib.rs#L239)
- [CreatePolicy](src/iam-service-lib.rs#L18)
- [CreateRole](src/iam-service-lib.rs#L34)
- [CreateServiceLinkedRole](src/iam-service-lib.rs#L386)
- [CreateUser](src/iam-service-lib.rs#L56)
- [DeleteAccessKey](src/iam-service-lib.rs#L263)
- [DeletePolicy](src/iam-service-lib.rs#L307)
- [DeleteRole](src/iam-service-lib.rs#L129)
- [DeleteServiceLinkedRole](src/iam-service-lib.rs#L145)
- [DeleteUser](src/iam-service-lib.rs#L160)
- [DeleteUserPolicy](src/iam-service-lib.rs#L318)
- [DetachRolePolicy](src/iam-service-lib.rs#L290)
- [DetachUserPolicy](src/iam-service-lib.rs#L222)
- [GetAccountPasswordPolicy](src/iam-service-lib.rs#L405)
- [GetRole](src/iam-service-lib.rs#L82)
- [ListAttachedRolePolicies](src/iam-service-lib.rs#L415)
- [ListGroups](src/iam-service-lib.rs#L367)
- [ListPolicies](src/iam-service-lib.rs#L335)
- [ListRolePolicies](src/iam-service-lib.rs#L436)
- [ListRoles](src/iam-service-lib.rs#L64)
- [ListSAMLProviders](src/iam-service-lib.rs#L455)
- [ListUsers](src/iam-service-lib.rs#L92)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.


#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.basics.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basics.iam_Scenario_CreateUserAssumeRole.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Rust IAM reference](https://docs.rs/aws-sdk-iam/latest/aws_sdk_iam/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0