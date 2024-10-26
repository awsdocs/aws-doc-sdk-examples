# IAM code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites



Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](hello_iam/CMakeLists.txt#L4) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](iam_create_user_assume_role_scenario.cpp)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](attach_role_policy.cpp#L38)
- [CreateAccessKey](create_access_key.cpp#L35)
- [CreateAccountAlias](create_account_alias.cpp#L35)
- [CreatePolicy](create_policy.cpp#L69)
- [CreateRole](create_role.cpp#L35)
- [CreateUser](create_user.cpp#L40)
- [DeleteAccessKey](delete_access_key.cpp#L35)
- [DeleteAccountAlias](delete_account_alias.cpp#L35)
- [DeletePolicy](delete_policy.cpp#L38)
- [DeleteServerCertificate](delete_server_certificate.cpp#L34)
- [DeleteUser](delete_user.cpp#L42)
- [DetachRolePolicy](detach_role_policy.cpp#L41)
- [GetAccessKeyLastUsed](access_key_last_used.cpp#L36)
- [GetPolicy](get_policy.cpp#L36)
- [GetServerCertificate](get_server_certificate.cpp#L36)
- [ListAccessKeys](list_access_keys.cpp#L37)
- [ListAccountAliases](list_account_aliases.cpp#L36)
- [ListPolicies](list_policies.cpp#L35)
- [ListServerCertificates](list_server_certificates.cpp#L35)
- [ListUsers](list_users.cpp#L34)
- [PutRolePolicy](put_role_policy.cpp#L35)
- [UpdateAccessKey](update_access_key.cpp#L40)
- [UpdateServerCertificate](update_server_certificate.cpp#L35)
- [UpdateUser](update_user.cpp#L37)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file in this folder. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

If the source file is in a different folder, instructions can be found in the README in that
folder.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.


#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.basics.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basics.iam_Scenario_CreateUserAssumeRole.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.



```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest
```


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for C++ IAM reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-iam/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0