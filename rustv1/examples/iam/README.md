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


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](src/iam-service-lib.rs#L221)
- [AttachUserPolicy](src/iam-service-lib.rs#L236)
- [CreateAccessKey](src/iam-service-lib.rs#L270)
- [CreatePolicy](src/iam-service-lib.rs#L18)
- [CreateRole](src/iam-service-lib.rs#L65)
- [CreateServiceLinkedRole](src/iam-service-lib.rs#L417)
- [CreateUser](src/iam-service-lib.rs#L87)
- [DeleteAccessKey](src/iam-service-lib.rs#L294)
- [DeletePolicy](src/iam-service-lib.rs#L338)
- [DeleteRole](src/iam-service-lib.rs#L160)
- [DeleteServiceLinkedRole](src/iam-service-lib.rs#L176)
- [DeleteUser](src/iam-service-lib.rs#L191)
- [DeleteUserPolicy](src/iam-service-lib.rs#L349)
- [DetachRolePolicy](src/iam-service-lib.rs#L321)
- [DetachUserPolicy](src/iam-service-lib.rs#L253)
- [GetAccountPasswordPolicy](src/iam-service-lib.rs#L436)
- [GetRole](src/iam-service-lib.rs#L113)
- [ListAttachedRolePolicies](src/iam-service-lib.rs#L446)
- [ListGroups](src/iam-service-lib.rs#L398)
- [ListPolicies](src/iam-service-lib.rs#L366)
- [ListRolePolicies](src/iam-service-lib.rs#L467)
- [ListRoles](src/iam-service-lib.rs#L95)
- [ListSAMLProviders](src/iam-service-lib.rs#L486)
- [ListUsers](src/iam-service-lib.rs#L123)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a user and assume a role](src/bin/iam-getting-started.rs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.



#### Create a user and assume a role

This example shows you how to create a user and assume a role.

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.end-->

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