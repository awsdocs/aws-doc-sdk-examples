# IAM code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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


### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](attach_role_policy.cpp#L40) (`AttachRolePolicy`)
- [Attach an inline policy to a role](put_role_policy.cpp#L37) (`PutRolePolicy`)
- [Create a policy](create_policy.cpp#L45) (`CreatePolicy`)
- [Create a role](create_role.cpp#L37) (`CreateRole`)
- [Create a user](create_user.cpp#L42) (`CreateUser`)
- [Create an access key](create_access_key.cpp#L37) (`CreateAccessKey`)
- [Create an alias for an account](create_account_alias.cpp#L37) (`CreateAccountAlias`)
- [Delete a policy](delete_policy.cpp#L40) (`DeletePolicy`)
- [Delete a server certificate](delete_server_certificate.cpp#L36) (`DeleteServerCertificate`)
- [Delete a user](delete_user.cpp#L44) (`DeleteUser`)
- [Delete an access key](delete_access_key.cpp#L37) (`DeleteAccessKey`)
- [Delete an account alias](delete_account_alias.cpp#L37) (`DeleteAccountAlias`)
- [Detach a policy from a role](detach_role_policy.cpp#L43) (`DetachRolePolicy`)
- [Get a policy](get_policy.cpp#L38) (`GetPolicy`)
- [Get a server certificate](get_server_certificate.cpp#L38) (`GetServerCertificate`)
- [Get data about the last use of an access key](access_key_last_used.cpp#L38) (`GetAccessKeyLastUsed`)
- [List a user's access keys](list_access_keys.cpp#L39) (`ListAccessKeys`)
- [List account aliases](list_account_aliases.cpp#L38) (`ListAccountAliases`)
- [List policies](list_policies.cpp#L37) (`ListPolicies`)
- [List server certificates](list_server_certificates.cpp#L37) (`ListServerCertificates`)
- [List users](list_users.cpp#L36) (`ListUsers`)
- [Update a server certificate](update_server_certificate.cpp#L37) (`UpdateServerCertificate`)
- [Update a user](update_user.cpp#L39) (`UpdateUser`)
- [Update an access key](update_access_key.cpp#L42) (`UpdateAccessKey`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a user and assume a role](iam_create_user_assume_role_scenario.cpp)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.



#### Create a user and assume a role

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.end-->

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