# IAM code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](hello/hello_iam.rb#L4) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_users.rb)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](attach_role_policy.rb#L6)
- [AttachUserPolicy](attach_user_policy.rb#L38)
- [CreateAccessKey](manage_access_keys.rb#L6)
- [CreateAccountAlias](manage_account_aliases.rb#L6)
- [CreatePolicy](attach_role_policy.rb#L6)
- [CreateRole](manage_roles.rb#L64)
- [CreateServiceLinkedRole](manage_roles.rb#L92)
- [CreateUser](manage_users.rb#L18)
- [DeleteAccessKey](manage_access_keys.rb#L6)
- [DeleteAccountAlias](manage_account_aliases.rb#L6)
- [DeleteRole](manage_roles.rb#L113)
- [DeleteServerCertificate](manage_server_certificates.rb#L6)
- [DeleteServiceLinkedRole](manage_roles.rb#L143)
- [DeleteUser](manage_users.rb#L134)
- [DeleteUserPolicy](manage_users.rb#L134)
- [DetachRolePolicy](attach_role_policy.rb#L6)
- [DetachUserPolicy](attach_user_policy.rb#L56)
- [GetAccountPasswordPolicy](get_account_password_policy.rb#L6)
- [GetPolicy](attach_role_policy.rb#L34)
- [GetRole](manage_roles.rb#L45)
- [GetUser](manage_users.rb#L43)
- [ListAccessKeys](manage_access_keys.rb#L6)
- [ListAccountAliases](manage_account_aliases.rb#L6)
- [ListAttachedRolePolicies](attach_role_policy.rb#L6)
- [ListGroups](list_groups.rb#L6)
- [ListPolicies](attach_role_policy.rb#L6)
- [ListRolePolicies](attach_role_policy.rb#L68)
- [ListRoles](manage_roles.rb#L18)
- [ListSAMLProviders](list_saml_providers.rb#L7)
- [ListServerCertificates](manage_server_certificates.rb#L6)
- [ListUsers](manage_users.rb#L60)
- [PutUserPolicy](attach_user_policy.rb#L17)
- [UpdateServerCertificate](manage_server_certificates.rb#L6)
- [UpdateUser](manage_users.rb#L78)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.

```
ruby hello/hello_iam.rb
```

#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->

Start the example by running the following at a command prompt:

```
ruby scenario_users.rb
```

<!--custom.basics.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basics.iam_Scenario_CreateUserAssumeRole.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Ruby IAM reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Iam.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0