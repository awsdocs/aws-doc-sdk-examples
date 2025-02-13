# IAM code examples for the AWS CLI with Bash script

## Overview

Shows how to use the AWS Command Line Interface with Bash script to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `aws-cli` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](iam_create_user_assume_role_scenario.sh)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](iam_operations.sh#L496)
- [CreateAccessKey](iam_operations.sh#L192)
- [CreatePolicy](iam_operations.sh#L421)
- [CreateRole](iam_operations.sh#L342)
- [CreateUser](iam_operations.sh#L113)
- [DeleteAccessKey](iam_operations.sh#L904)
- [DeletePolicy](iam_operations.sh#L646)
- [DeleteRole](iam_operations.sh#L716)
- [DeleteUser](iam_operations.sh#L985)
- [DetachRolePolicy](iam_operations.sh#L571)
- [GetUser](iam_operations.sh#L17)
- [ListAccessKeys](iam_operations.sh#L273)
- [ListUsers](iam_operations.sh#L56)
- [UpdateAccessKey](iam_operations.sh#L787)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


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
in the `aws-cli` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [AWS CLI with Bash script IAM reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/iam/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
