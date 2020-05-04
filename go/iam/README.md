# AWS SDK for Go Code Examples for AWS Identity and Access Management (IAM)

## Purpose

These examples demonstrates how to perform several DynamoDB operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateAccountAlias/CreateAccountAlias.go

This example creates an alias for your IAM account.

`go run CreateAccountAlias.go -a ALIAS`

- _ALIAS_ is the alias for the account.

The unit test mocks the service client and the `CreateAccountAlias` function.

### CreateUser/CreateUser.go

This example creates a new IAM user.

`go run CreateUser.go -u USERNAME`

- _USERNAME_ is the name of the user.

The unit test mocks the service client and the `CreateUser` function.

### ListAccountAliases/ListAccountAliases.go

This example lists the aliases for your account.

`go run ListAccountAliases -m MAX-ITEMS`

- \_MAX-ITEMS is the maximum number of aliases to show.

The unit test mocks the service client and the `ListAccountAliases` function.

### ListUsers/ListUsers.go

This example lists your IAM users.

`go run ListUsers.go [-m MAX-USERS]`

- _MAX-USERS_ is the maximum number of users to show.
  The default is 10.

The unit test accepts a similar value in _config.json_.

### UpdateUser/UpdateUser.go

This example changes the name of an existing IAM user.

`go run UpdateUser.go -u USERNAME -n NEW-NAME`

- _USERNAME_ is the name of an existing IAM user.
- _NEW-NAME_ is the new user name.

The unit test mocks the service client and the `UpdateUser` function.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
