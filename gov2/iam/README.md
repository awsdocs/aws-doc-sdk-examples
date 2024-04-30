# IAM code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](hello/hello.go#L4) (`ListPolicies`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](actions/roles.go#L132)
- [CreateAccessKey](actions/users.go#L175)
- [CreatePolicy](actions/policies.go#L65)
- [CreateRole](actions/roles.go#L46)
- [CreateServiceLinkedRole](actions/roles.go#L98)
- [CreateUser](actions/users.go#L74)
- [DeleteAccessKey](actions/users.go#L193)
- [DeletePolicy](actions/policies.go#L118)
- [DeleteRole](actions/roles.go#L200)
- [DeleteServiceLinkedRole](actions/roles.go#L117)
- [DeleteUser](actions/users.go#L160)
- [DeleteUserPolicy](actions/users.go#L144)
- [DetachRolePolicy](actions/roles.go#L166)
- [GetAccountPasswordPolicy](actions/account.go#L26)
- [GetPolicy](actions/policies.go#L100)
- [GetRole](actions/roles.go#L81)
- [GetUser](actions/users.go#L47)
- [ListAccessKeys](actions/users.go#L209)
- [ListAttachedRolePolicies](actions/roles.go#L148)
- [ListGroups](actions/groups.go#L27)
- [ListPolicies](actions/policies.go#L47)
- [ListRolePolicies](actions/roles.go#L182)
- [ListRoles](actions/roles.go#L28)
- [ListSAMLProviders](actions/account.go#L44)
- [ListUserPolicies](actions/users.go#L126)
- [ListUsers](actions/users.go#L29)
- [PutUserPolicy](actions/users.go#L92)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a user and assume a role](scenarios/scenario_assume_role.go)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```

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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Go V2 IAM reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/iam)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0