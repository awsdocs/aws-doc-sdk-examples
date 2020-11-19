# AWS SDK for Go V2 code examples for IAM

## Purpose

These examples demonstrates how to perform several
AWS Identity and Access Management (IAM)
operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateUser/CreateUserv2.go

This example creates an IAM user. 

`go run CreateUserv2.go -u USER-NAME`

- _USER-NAME_ is the name of the user to create.

The unit test accepts a similar value in _config.json_.

### DeleteUser/DeleteUserv2.go

This example deletes an IAM user.

`go run DeleteUserv2.go -u USER-NAME`

- _USER-NAME_ is the name of the user to delete.

The unit test accepts a similar value in _config.json_.

### ListAdmins/ListAdminsv2.go

This example lists the number IAM users and those who have administrative privileges.

`go run ListAdminsv2.go [-d]`

- **-d** to list the user and administrator names.

The unit test accepts a similar value in _config.json_.

### ListUsers/ListUsersv2.go

This example retrieves a list of your IAM users.

`go run ListUsersv2.go [-m MAX-USERS]`

- _MAX-USERS_ is the maximum number of users to list.
  The code example restricts this to the range of 0 to 100.
  The default value is 10.
  
### UpdateUser/UpdateUserv2.go

This example changes the name of the user.

`go run UpdateUserv2.go -u USER-NAME -n NEW-NAME`

- _USER-NAME_ is the name of the user to change.
- _NEW-NAME_ is the new name of the user.

The unit test accepts similar values in _config.json_.

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

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
