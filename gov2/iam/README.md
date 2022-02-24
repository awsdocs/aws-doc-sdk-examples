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

### common/main.go

This example demonstrates the most common IAM actions. 

### AccessKeyLastUsed/AccessKeyLastUsedv2.go

This example retrieves when an IAM access key was last used, 
including the AWS Region and with which service.

`go run AccessKeyLastUsedv2.go -k KeyID`

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

`go run CreateAccountAliasv2.go -a ALIAS`

- _ALIAS_ is the alias created for your account.

The unit test accepts a similar value in _config.json_.

### DeleteAccessKey/DeleteAccessKeyv2.go

This example deletes an IAM access key.

`go run DeleteAccessKeyv2.go -k KeyID -u USER-NAME`

- _KEYID_ is the access key to delete.
- _USER-NAME_ is the name of the user deleting the key.

The unit test accepts similar values in _config.json_.

### DeleteAccountAlias/DeleteAccountAliasv2.go

This example deletes an alias for your IAM account.

`go run DeleteAccountAliasv2.go -a ALIAS`

- _ALIAS_ is the account alias to delete.

The unit test accepts a similar value in _config.json_.

### DeleteServerCert/DeleteServerCertv2.go

This example deletes an IAM server certificate.

`go run DeleteServerCertv2.go -c CERTIFICATE-NAME`

- _CERTIFICATE-NAME_ is the name of the server certificate to delete.

The unit test accepts a similar value in _config.json_.

### DetachUserPolicy/DetachUserPolicyv2.go

This example detaches an Amazon DynamoDB full-access policy from an IAM role.

`go run DetachUserPolicyv2.go -r ROLE-NAME`

- _ROLE-NAME_ is the name of the role from which the policy is detached.

The unit test accepts a similar value in _config.json_.


### GetServerCert/GetServerCertv2.go

This example retrieves an IAM server certificate.

`go run GetServerCertv2.go -c CERTIFICATE`

- _CERTIFICATE_ is the name of the server certificate.

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

`go run ListAccountAliasesv2.go [-m MAX-ITEMS]`

- _MAX-ITEMS_ is the maximum number of aliases to show.
  If this value is less than zero, the code example sets it to 10.
  
The unit test accepts a similar value in _config.json_.

### ListAdmins/ListAdminsv2.go

This example lists the number IAM users and those who have administrative privileges.

`go run ListAdminsv2.go [-d]`

- **-d** to list the user and administrator names.

The unit test accepts a similar value in _config.json_.

### ListServerCerts/ListServerCertsv2.go

This example retrieves the server certificates.

`go run ListServerCertsv2.go`


### UpdateAccessKey/UpdateAccessKeyv2.go

This example sets the status of an IAM access key to active.

`go run UpdateAccessKeyv2.go -k KeyID -u USER-NAME`

- _KEYID_ is the access key to activate.
- _USER-NAME_ is the name of the user activating the key.

The unit test accepts similar values in _config.json_.

### UpdateServerCert/UpdateServerCertv2.go

This example renames an IAM server certificate.

`go run UpdateServerCert/UpdateServerCertv2.go -c CERTIFICATE-NAME -n NEW-NAME`

- _CERTIFICATE-NAME_ is the original name of the server certificate.
- _NEW-NAME_ is the new name of the server certificate.

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
