#  AWS IAM code examples for the SDK for C++

## Overview

The code examples in this directory demonstrate how to work with AWS Identity and Access Management (IAM) using the AWS SDK for C++.

*IAM is a web service that helps you securely control access to AWS resources. You use IAM to control who is authenticated (signed in) and authorized (has permissions) to use resources.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

- [Access the last used key](./access_key_last_used.cpp) (GetAccessKeyLastUsed)
- [Attach a role policy](./attach_role_policy.cpp) (AttachRolePolicy)
- [Create an access key](./create_access_key.cpp) (CreateAccessKey)
- [Create an account alias](./create_account_alias.cpp) (CreateAccountAlias)
- [Create a policy](./create_policy.cpp) (CreatePolicy)
- [Create a role](./create_role.cpp) (CreateRole)
- [Create a user](./create_user.cpp) (CreateUser)
- [Delete an access key](./delete_access_key.cpp) (DeleteAccessKey)
- [Delete an account alias](./delete_account_alias.cpp) (DeleteAccountAlias)
- [Delete a policy](./delete_policy.cpp) (DeletePolicy)
- [Delete a server certificate](./delete_server_certificate.cpp) (DeleteServerCertificate)
- [Delete a user](./delete_user.cpp) (DeleteUser)
- [Detach a role policy](./detach_role_policy.cpp) (DetachRolePolicy)
- [Get a policy](./get_policy.cpp) (GetPolicy)
- [Get a server certificate](./get_server_certificate.cpp) (GetServerCertificate)
- [List the access keys](./list_access_keys.cpp) (ListAccessKeys)
- [List the account aliases](./list_account_aliases.cpp) (ListAccountAliases)
- [List the policies](./list_policies.cpp) (ListPolicies)
- [List the server certificates](./list_server_certificates.cpp) (ListServerCertificates)
- [List the users](./list_users.cpp) (ListUsers)
- [Put a role policy](./put_role_policy.cpp) (putRolePolicy)
- [Update an access key](./update_access_key.cpp) (UpdateAccessKey)
- [Update a server certificate](./update_server_certificate.cpp) (UpdateServerCertificate)
- [Update a user](./update_user.cpp) (UpdateUser)

### Scenarios

- [Create an IAM user, create an IAM role, and apply the role to the user](./iam_create_user_assume_role_scenario.cpp) (CreateUser, GetUser, CreateRole, CreatePolicy, AssumeRole, ListBuckets, AttachRolePolicy, DetachRolePolicy, DeletePolicy, DeleteRole, DeleteUser)

## Run the examples

Before using the code examples, first complete the installation and setup steps of [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting started section covers how to get and build the SDK, and how to build your own code by using the SDK with a sample “Hello World”-style application. 

For information on the structure of the code examples and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

## Tests

⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
```

## Additional resources

- [AWS Identity and Access Management (IAM) documentation](https://aws.amazon.com/iam/index.html)
- [AWS SDK for C++ documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [IAM code examples using the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-iam.html)


  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
