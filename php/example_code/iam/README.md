# IAM code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](GettingStartedWithIAM.php)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](GettingStartedWithIAM.php#L82)
- [CreatePolicy](GettingStartedWithIAM.php#L70)
- [CreateRole](GettingStartedWithIAM.php#L57)
- [CreateServiceLinkedRole](GettingStartedWithIAM.php#L46)
- [CreateUser](GettingStartedWithIAM.php#L51)
- [GetAccountPasswordPolicy](GettingStartedWithIAM.php#L46)
- [GetPolicy](GettingStartedWithIAM.php#L46)
- [GetRole](GettingStartedWithIAM.php#L46)
- [ListAttachedRolePolicies](GettingStartedWithIAM.php#L46)
- [ListGroups](GettingStartedWithIAM.php#L46)
- [ListPolicies](GettingStartedWithIAM.php#L46)
- [ListRolePolicies](GettingStartedWithIAM.php#L46)
- [ListRoles](GettingStartedWithIAM.php#L46)
- [ListSAMLProviders](GettingStartedWithIAM.php#L46)
- [ListUsers](GettingStartedWithIAM.php#L46)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
Run the example with the following command:
`php GettingStartedWithIAM.php`
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
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for PHP IAM reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Iam.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0