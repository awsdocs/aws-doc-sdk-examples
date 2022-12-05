# IAM code examples for the SDK for Go

## Overview

Shows how to use the AWS SDK for Go (v2) to manage AWS Identity and Access
Management (IAM) resources.

*IAM is a web service for securely controlling access to AWS services. With IAM, you
can centrally manage users, security credentials such as access keys, and permissions
that control which AWS resources users and applications can access.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello IAM](hello/hello.go)
  (`ListPolicies`)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Attach a policy to a role](actions/policies.go)
  (`AttachRolePolicy`)
* [Create a policy](actions/policies.go)
  (`CreatePolicy`)
* [Create a role](actions/roles.go)
  (`CreateRole`)
* [Create a service-linked role](actions/roles.go)
  (`CreateServiceLinkedRole`)
* [Create a user](actions/users.go)
  (`CreateUser`)
* [Create an access key](actions/users.go)
  (`CreateAccessKey`)
* [Create an inline policy for a user](actions/users.go)
  (`PutUserPolicy`)
* [Delete a policy](actions/policies.go)
  (`DeletePolicy`)
* [Delete a role](actions/roles.go)
  (`DeleteRole`)
* [Delete a service-linked role](actions/roles.go)
  (`DeleteServiceLinkedRole`)
* [Delete a user](actions/users.go)
  (`DeleteUser`)
* [Delete an access key](actions/users.go)
  (`DeleteAccessKey`)
* [Delete an inline policy from a user](actions/users.go)
  (`DeleteUserPolicy`)
* [Detach a policy from a role](actions/roles.go)
  (`DetachRolePolicy`)
* [Get a policy](actions/policies.go)
  (`GetPolicy`)
* [Get a role](actions/roles.go)
  (`GetRole`)
* [Get a user](actions/users.go)
  (`GetUser`)
* [Get the account password policy](actions/account.go)
  (`GetAccountPasswordPolicy`)
* [List SAML providers](actions/account.go)
  (`ListSAMLProviders`)
* [List a user's access keys](actions/users.go)
  (`ListAccessKeys`)
* [List groups](actions/groups.go)
  (`ListGroups`)
* [List inline policies for a role](actions/roles.go)
  (`ListRolePolicies`)
* [List inline policies for a user](actions/users.go)
  (`ListUserPolicies`)
* [List policies](actions/policies.go)
  (`ListPolicies`)
* [List policies attached to a role](actions/roles.go)
  (`ListAttachedRolePolicies`)
* [List roles](actions/roles.go)
  (`ListRoles`)
* [List users](actions/users.go)
  (`ListUsers`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Create a user and assume a role](scenarios/scenario_assume_role.go)

## Run the examples

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

### Instructions

#### Hello IAM

Get started using the SDK for Go with IAM by listing policies in your account.

```
go run ./hello
```

#### Create a user and assume a role

This interactive scenario runs at a command prompt and shows you how to use IAM
to do the following:

1. Create a user who has no permissions.
2. Create a role that grants permission to list Amazon Simple Storage Service (Amazon S3)
   buckets for the account.
3. Add a policy to let the user assume the role.
4. Try and fail to list buckets without permissions.
5. Assume the role and list S3 buckets using temporary credentials.
6. Delete the policy, role, and user.

Install all required resources and start the example by running the following in the
`iam` folder at a command prompt.

```
go run ./cmd -scenario assumerole
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

Instructions for running the tests for this service can be found in the
[README](../README.md#Tests) in the GoV2 folder.

## Additional resources
* [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
* [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
* [SDK for Go IAM package](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/iam) 

---

Copyright Amazon.com, Inc. or its affiliates.

All Rights Reserved. SPDX-License-Identifier: Apache-2.0
