# AWS IAM examples for the AWS SDK for Go (v2)

## Overview

These examples demonstrate how to perform AWS Identity and Access Management (IAM) operations using version 2 of the AWS SDK for Go.

With AWS Identity and Access Management (IAM), you can specify who or what can access services and resources in AWS, centrally manage fine-grained permissions, and analyze access to refine permissions across AWS.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create an access key](CreateAccessKey/CreateAccessKeyv2.go) (`CreateAccessKey`)
- [Create an alias for an account](CreateAccountAlias/CreateAccountAliasv2.go) (`CreateAccountAlias`)
- [Delete an access key](DeleteAccessKey/DeleteAccessKeyv2.go) (`DeleteAccessKey`)
- [Delete an account alias](DeleteAccountAlias/DeleteAccountAliasv2.go) (`DeleteAccountAlias`)
- [Delete a server certificate](DeleteServerCert/DeleteServerCertv2.go) (`DeleteServerCert`)
- [Detach a policy from a user](DetachUserPolicy/DetachUserPolicyv2.go) (`DetachUserPolicy`)
- [Get a server certificate](GetServerCert/GetServerCertv2.go) (`GetServerCert`)
- [List a user's access keys](ListAccessKeys/ListAccessKeysv2.go) (`ListAccessKeys`)
- [List account aliases](ListAccountAliases/ListAccountAliasesv2.go) (`ListAccountAliases`)
- [List users defined as administrators](ListAdmins/ListAdminsv2.go) (`ListAdmins`)
- [List server certificates](ListServerCerts/ListServerCertsv2.go) (`ListServerCerts`)
- [Update an access key](UpdateAccessKey/UpdateAccessKeyv2.go) (`UpdateAccessKey`)
- [Update a sserver certificate](UpdateServerCert/UpdateServerCertv2.go) (`UpdateServerCert`)
- [Update a user](UpdateUser/UpdateUserv2.go) (`UpdateUser`)

### Scenarios
* [Create a user and assume a role](common/main.go)

## Run the examples

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

You must have Go 1.17 or later installed.

### Instructions
Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

From a directory containing `go.mod`, use `go test` to run all unit tests:

```
go test ./...
```

This tests all modules in the current folder and any submodules.

## Additional resources

- [AWS IAM developer guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [AWS IAM API reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [AWS SDK for Go V3 Amazon IAM API reference](https://docs.aws.amazon.com/sdk-for-go/api/service/iam/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
