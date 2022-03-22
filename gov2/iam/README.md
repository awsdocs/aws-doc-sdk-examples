# AWS SDK for Go V2 code examples for IAM

## Purpose

These examples demonstrates how to perform several AWS Identity and Access Management (IAM) operations using version 2 of the AWS SDK for Go.

## Code examples

### Scenario examples
* [`common/`](common/) -- This scenario and examples demonstrate the creation of users, access keys, roles, policies, service-linked roles, and assuming a role in order to take on specific permissions from a policy.

### API Examples

The API examples included in this directory cover the following IAM actions:

- CreateAccessKey
- CreateAccountAlias
- DeleteAccessKey
- DeleteAccountAlias
- DeleteServerCert
- DetachUserPolicy
- GetServerCert
- ListAccessKeys
- ListAccountAliases
- ListAdmins
- ListServerCerts
- UpdateAccessKey
- UpdateServerCert
- UpdateUser

Each is provided in its own directory. 

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.



## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.


## Running the code

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Running the tests

From a directory containing `go.mod`, use `go test` to run all unit tests:

```
go test ./...
```

This tests all modules in the current folder and any submodules.

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the *AWS SDK for Go Developer Guide*.

You must have Go 1.17 or later installed.

## Additional information

- [AWS SDK for Go V3 Amazon IAM service reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3)
- [AWS IAM documentation](https://docs.aws.amazon.com/iam)

---


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
