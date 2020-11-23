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

### AccessKeyLastUsed/AccessKeyLastUsedv2.go

This example retrieves when an IAM access key was last used, 
including the AWS Region and with which service.

`go run AccessKeyLastUsed/AccessKeyLastUsedv2.go -k KeyID`

- _KeyID_ is the ID of the access key.

The unit test accepts a similar value in _config.json_.

### AttachUserPolicy/AttachUserPolicyv2.go

This example attaches an Amazon DynamoDB full-access policy to an IAM role.

`go run AttachUserPolicyv2.go -n ROLE-NAME`

- _ROLE-NAME_ is the name of the role to which the policy is attached.

The unit test accepts a similar value in _config.json_.

### CreateAccessKey/CreateAccessKeyv2.go

This example creates a new IAM access key for a user.

`go run CreateAccessKeyv2.go -u USER-NAME`

- _USER-NAME_ is the name of the user for whom the new key is created.

### CreateAccountAlias/CreateAccountAliasv2.go

This example creates an alias for your IAM account.

`go run CreateAccountAlias/CreateAccountAliasv2.go -a ALIAS`

- _ALIAS_ is the alias created for your account.

The unit test accepts a similar value in _config.json_.

### CreatePolicy/CreatePolicyv2.go

This example creates an IAM policy.

`go run CreatePolicy/CreatePolicyv2.go -n POLICY-NAME`

- _POLICY-NAME_ is the name of the policy to create.

The unit test accepts a similar value in _config.json_.

### CreateUser/CreateUserv2.go

This example creates an IAM user. 

`go run CreateUserv2.go -u USER-NAME`

- _USER-NAME_ is the name of the user to create.

The unit test accepts a similar value in _config.json_.

### DeleteAccessKey/DeleteAccessKeyv2.go

This example deletes an IAM access key.

`go run DeleteAccessKeyv2.go -k KeyID -u USER-NAME`

- _KEYID_ is the access key to delete.
- _USER-NAME_ is the name of the user deleting the key.

The unit test accepts similar values in _config.json_.

### DeleteAccountAlias/DeleteAccountAliasv2.go

This example deletes an alias for your IAM account.

`go run DeleteAccountAlias/DeleteAccountAliasv2.go -a ALIAS`

- _ALIAS_ is the account alias to delete.

The unit test accepts a similar value in _config.json_.

### DeleteUser/DeleteUserv2.go

This example deletes an IAM user.

`go run DeleteUserv2.go -u USER-NAME`

- _USER-NAME_ is the name of the user to delete.

The unit test accepts a similar value in _config.json_.

### DetachUserPolicy/DetachUserPolicyv2.go

This example detaches an Amazon DynamoDB full-access policy from an IAM role.

`go run DetachUserPolicy/DetachUserPolicyv2.go -r ROLE-NAME`

- _ROLE-NAME_ is the name of the role from which the policy is detached.

The unit test accepts a similar value in _config.json_.

### GetPolicy/GetPolicyv2.go

This example retrieves the description of the IAM policy with the specified ARN.

`go run GetPolicy/GetPolicyv2.go -p POLICY-ARN`

- _POLICY-ARN_ is the ARN of the policy.

The unit test accepts a similar value in _config.json_.

### ListAccessKeys/ListAccessKeysv2.go

This example retrieves the access keys for your IAM account.

`go run ListAccessKeysv2.go -u USER-NAME -m MAX-KEYS`

- _USER-NAME_ is the name of the user for which the keys are listed.
- _MAX-KEYS_ is the maximum number of keys to display.
  If this value is negative, the code example sets it to 10. 

The unit test accepts similar values in _config.json_.

### ListAccountAliases/ListAccountAliasesv2.go

This example retrieves the aliases for your IAM account.

`go run ListAccountAliases/ListAccountAliasesv2.go [-m MAX-ITEMS]`

- _MAX-ITEMS_ is the maximum number of aliases to show.
  If this value is less than zero, the code example sets it to 10.
  
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
  
### UpdateAccessKey/UpdateAccessKeyv2.go

This example 

`go run UpdateAccessKeyv2.go -k KeyID -u USER-NAME`

- _KEYID_ is the access key to activate.
- _USER-NAME_ is the name of the user activating the key.

The unit test accepts similar values in _config.json_.
  
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
