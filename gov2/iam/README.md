# AWS IAM examples for the AWS SDK for Go (v2)

## Purpose

These examples demonstrate how to perform AWS Identity and Access Management (IAM) operations using version 2 of the AWS SDK for Go.

With AWS Identity and Access Management (IAM), you can specify who or what can access services and resources in AWS, centrally manage fine-grained permissions, and analyze access to refine permissions across AWS.

## ⚠️ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website.
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Create an access key](CreateAccessKey) (`CreateAccessKey`)
- [Create an alias for an account](CreateAccountAlias) (`CreateAccountAlias`)
- [Delete an access key](DeleteAccessKey) (`DeleteAccessKey`)
- [Delete an account alias](DeleteAccountAlias/) (`DeleteAccountAlias`)
- [Delete a server certificate](DeleteServerCert/) (`DeleteServerCert`)
- [Detach a policy from a user](DetachUserPolicy/) (`DetachUserPolicy`)
- [Get a server certificate](GetServerCert/) (`GetServerCert`)
- [List a user's access keys](ListAccessKeys/) (`ListAccessKeys`)
- [List account aliases](ListAccountAliases/) (`ListAccountAliases`)
- [List users defined as administrators](ListAdmins/) (`ListAdmins`)
- [List server certificates](ListServerCerts/) (`ListServerCerts`)
- [Update an access key](UpdateAccessKey/) (`UpdateAccessKey`)
- [Update a sserver certificate](UpdateServerCert/) (`UpdateServerCert`)
- [Update a user](UpdateUser/) (`UpdateUser`)

### Scenarios
* [Create a user and assume a role](common/)

## Run the examples

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

You must have Go 1.17 or later installed.

## Tests

From a directory containing `go.mod`, use `go test` to run all unit tests:

```
go test ./...
```

This tests all modules in the current folder and any submodules.

## Additional resources

- [AWS SDK for Go V3 Amazon IAM service reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3)
- [AWS IAM documentation](https://docs.aws.amazon.com/iam)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
