# AWS SDK for Go code examples for AWS Identity and Access Management (IAM)

## Purpose

These examples demonstrate how to perform several IAM operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### AccessKeyLastUsed/AccessKeyLastUsed.go

This example retrieves when an access key was last used,
including in which service and AWS Region.

`go run AccessKeyLastUsed.go -k KEY-ID`

- _KEY-ID_ is the ID of the access key.

The unit test mocks the IAM service client an `AccessKeyLastUsed` function.

### AttachUserPolicy/AttachUserPolicy.go

This example attaches an Amazon DynamoDB full-access policy to an IAM role.

`go run AttachUserPolicy.go -r ROLE`

- _ROLE_ is the role name.

The unit test mocks the service client and the `AttachRolePolicy` function.

### CreateAccessKey/CreateAccessKey.go

This example creates a new access key ID and secret key for a user.

`go run CreateAccessKey.go -u USER`

- _USER_ is the name of the user to created the access key ID and secret key.

The unit test mocks the IAM service client and `CreateAccessKey` function.

### CreateAccountAlias/CreateAccountAlias.go

This example creates an alias for your IAM account.

`go run CreateAccountAlias.go -a ALIAS`

- _ALIAS_ is the alias for the account.

The unit test mocks the service client and the `CreateAccountAlias` function.

### CreatePolicy/CreatePolicy.go

This example creates a new policy.

`go run CreatePolicy.go -n POLICY`

- _POLICY_ is the name of the policy.

The unit test mocks the service client and the `CreatePolicy` function.

### CreateUser/CreateUser.go

This example creates a new IAM user.

`go run CreateUser.go -u USERNAME`

- _USERNAME_ is the name of the user.

The unit test mocks the service client and the `CreateUser` function.

### DeleteAccessKey/DeleteAccessKey.go

This example deletes an IAM access key.

`go run DeleteAccessKey.go -k KEY-ID -u USER-NAME`

- _KEY-ID_ is the ID of the access key.
- _USER-NAME_ is the name of a user.

The unit test mocks the IAM service client and the `DeleteAccessKey` function.

### DeleteAccountAlias/DeleteAccountAlias

This example removes an alias for an IAM account.

`go run DeleteAccountAlias.go -a ALIAS`

- _ALIAS_ is the alias for the account.

The unit test mocks the IAM service client and the `DeleteAccountAlias` function.

### DeleteServerCert/DeleteServerCert.go

This example deletes an IAM server certificate.

`go run DeleteServerCert.go -c CERT-NAME`

- _CERT-NAME_ is the name of the cerificate.

The unit test mocks the IAM service client and the `DeleteServerCertificate` function.

### DeleteUser/DeleteUser.go

This example deletes an IAM user.

`go run DeleteUser.go -u USERNAME`

- _USERNAME_ is the name of the user to delete.

The unit test mocks the service client and the `DeleteUser` function.

### DetachUserPolicy/DetachUserPolicy.go

This example detaches an Amazon DynamoDB full-access policy from an IAM role.

`go run DetachUserPolicy.go -r ROLE`

- _ROLE_ is the role name.

The unit test mocks the service client and the `DetachRolePolicy` function.

### GetPolicy/GetPolicy.go

This example retrieves the description for a policy.

`go run GetPolicy.go -a POLICY-ARN`

- _POLICY-ARN_ is the ARN of a policy.

The unit test mocks the IAM service client and the `GetPolicy` function.

### GetPublicKeys/GetPublicKeys.go

This example gets the bodies of a user's public SSH keys.

`go run GetPublicKeys -u USER-NAME`

- _USER-NAME_ is the name of a user.

The unit test mocks the IAM service client and the `ListSSHPublicKeys` function.

### GetServerCert/GetServerCert.go

This example retrieves information about an IAM server certificate.

`go run GetServerCert.go -c CERT-NAME`

- _CERT-NAME_ is the name of a server certificate.

The unit test mocks the IAM service client and the `GetServerCertificate` function.

### ListAccessKeys/ListAccessKeys.go

This example lists the accesss keys for a specific user.

`go run ListAccessKeys -u USER`

The unit test mocks the IAM service client and the `ListAccessKeys` function.

### ListAccountAliases/ListAccountAliases.go

This example lists the aliases for your account.

`go run ListAccountAliases -m MAX-ITEMS`

- _MAX-ITEMS_ is the maximum number of aliases to show.

The unit test mocks the service client and the `ListAccountAliases` function.

### ListAdmins/ListAdmins.go

This example lists the number of users and users who have administrative rights.

`go run ListAdmins.go`

### ListServerCerts/ListServerCerts.go

This example lists the metadata about your server certificates.

`go run ListServerCerts.go`

The unit test mocks the IAM service client and the `ListServerCertificates` function.

### ListUsers/ListUsers.go

This example lists your IAM users.

`go run ListUsers.go [-m MAX-USERS]`

- _MAX-USERS_ is the maximum number of users to show.
  The default is 10.

The unit test accepts a similar value in _config.json_.

### UpdateAccessKey/UpdateAccessKey.go

This example activates an access key.

`go run UpdateAccessKey.go -k KEY-ID -u USER-NAME`

- _KEY-ID_ is the ID of an access key.
- _USER-NAME_ is the name of a user.

The unit test mocks the IAM service client and the `UpdateAccessKey` function.

### UpdateServerCert/UpdateServerCert.go

This example renames an IAM server certificate.

`go run UpdateServerCert.go -c CERT-NAME -n NEW-NAME`

- _CERT-NAME_ is the original name of the server certificate.
- _NEW-NAME_ is the new name of the server certificate.

The unit test mocks the IAM service client and the `UpdateServerCertificate` function.

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

To run a unit test, enter the following:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files.

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter the following:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
