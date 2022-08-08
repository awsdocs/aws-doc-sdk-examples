#  AWS IAM code examples for the SDK for C++

## Overview
The code examples in this directory demonstrate how to work with the Amazon Web Services Identity and Access Management (AWS IAM) using the AWS SDK for C++.


AWS IAM is a web service that helps you securely control access to AWS resources. You use IAM to control who is authenticated (signed in) and authorized (has permissions) to use resources.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
- [Access the last-used key](./access_key_last_used.cpp) (GetAccessKeyLastUsed)
- [Assume a role](./assume_role.cpp) (AssumeRole)
- [Attach a role policy](./attach_role_policy.cpp) (AttachRolePolicy)
- [Create an access key](./create_access_key.cpp) (CreateAccessKey)
- [Create an account alias](./create_account_alias.cpp) (CreateAccountAlias)
- [Create a policy](./create_policy.cpp) (CreatePolicy)
- [Create a role](./create_role.cpp) (CreateRole)
- [Create a user](./create_user.cpp) (CreateUser)
- [Delete an access key](./delete_access_key.cpp) (DeleteAccessKey)
- [Delete an account alias](./delete_account_alias.cpp) (DeleteAccountAlias)
- [Delete a policy](./delete_policy.cpp) (DeletePolicy)
- [Delete a server certificate](./delete_server_cert.cpp) (DeleteServerCertificate)
- [Delete a user](./delete_user.cpp) (DeleteUser)
- [Detach a role policy](./detach_role_policy.cpp) (DetachRolePolicy)
- [Get a policy](./get_policy.cpp) (GetPolicy)
- [List the access keys](./list_access_keys.cpp) (ListAccessKeys)
- [List the account aliases](./list_account_aliases.cpp) (ListAccountAliases)
- [List the policies](./list_policies.cpp) (ListPolicies)
- [List the server certificates](./list_server_certs.cpp) (ListServerCertificates)
- [List the users](./list_users.cpp) (ListUsers)
- [Put a role policy](./put_role_policy.cpp) (PutRolePolicy)
- [Update an access key](./update_access_key.cpp) (UpdateAccessKey)
- [Update a server certificate](./update_server_cert.cpp) (UpdateServerCertificate)
- [Update a user](./update_user.cpp) (UpdateUser)

(Updated to this point)
### Scenarios
- [Creating, listing, and deleting S3 buckets](./s3_getting_started_scenario.cpp)
- [Finding, creating, and deleting an S3 bucket in a sequence](./s3-demo.cpp)

## Running the examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to get and build the SDK, and how to build your own code by using the SDK with a sample “Hello World”-style application. 

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
   ./gtests/s3_gtest 
```   

## Additional resources
- [Amazon Simple Storage Service Documentation](https://docs.aws.amazon.com/s3/index.html)
- [Amazon S3 code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/examples-s3.html)
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
