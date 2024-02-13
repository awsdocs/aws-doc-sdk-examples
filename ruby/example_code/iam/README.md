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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](attach_role_policy.rb#L6) (`AttachRolePolicy`)
- [Attach a policy to a user](attach_user_policy.rb#L38) (`AttachUserPolicy`)
- [Create a policy](attach_role_policy.rb#L6) (`CreatePolicy`)
- [Create a role](manage_roles.rb#L61) (`CreateRole`)
- [Create a service-linked role](manage_roles.rb#L89) (`CreateServiceLinkedRole`)
- [Create a user](manage_users.rb#L18) (`CreateUser`)
- [Create an access key](manage_access_keys.rb#L6) (`CreateAccessKey`)
- [Create an alias for an account](manage_account_aliases.rb#L6) (`CreateAccountAlias`)
- [Create an inline policy for a user](attach_user_policy.rb#L17) (`PutUserPolicy`)
- [Delete a role](manage_roles.rb#L109) (`DeleteRole`)
- [Delete a server certificate](manage_server_certificates.rb#L6) (`DeleteServerCertificate`)
- [Delete a service-linked role](manage_roles.rb#L138) (`DeleteServiceLinkedRole`)
- [Delete a user](manage_users.rb#L134) (`DeleteUser`)
- [Delete an access key](manage_access_keys.rb#L6) (`DeleteAccessKey`)
- [Delete an account alias](manage_account_aliases.rb#L6) (`DeleteAccountAlias`)
- [Delete an inline policy from a user](manage_users.rb#L134) (`DeleteUserPolicy`)
- [Detach a policy from a role](attach_role_policy.rb#L6) (`DetachRolePolicy`)
- [Detach a policy from a user](attach_user_policy.rb#L56) (`DetachUserPolicy`)
- [Get a policy](attach_role_policy.rb#L34) (`GetPolicy`)
- [Get a role](manage_roles.rb#L44) (`GetRole`)
- [Get a user](manage_users.rb#L43) (`GetUser`)
- [Get the account password policy](get_account_password_policy.rb#L1) (`GetAccountPasswordPolicy`)
- [List SAML providers](list_saml_providers.rb#L7) (`ListSAMLProviders`)
- [List a user's access keys](manage_access_keys.rb#L6) (`ListAccessKeys`)
- [List account aliases](manage_account_aliases.rb#L6) (`ListAccountAliases`)
- [List groups](list_groups.rb#L6) (`ListGroups`)
- [List inline policies for a role](attach_role_policy.rb#L68) (`ListRolePolicies`)
- [List policies](attach_role_policy.rb#L6) (`ListPolicies`)
- [List policies attached to a role](attach_role_policy.rb#L6) (`ListAttachedRolePolicies`)
- [List roles](manage_roles.rb#L18) (`ListRoles`)
- [List server certificates](manage_server_certificates.rb#L6) (`ListServerCertificates`)
- [List users](manage_users.rb#L60) (`ListUsers`)
- [Update a server certificate](manage_server_certificates.rb#L6) (`UpdateServerCertificate`)
- [Update a user](manage_users.rb#L78) (`UpdateUser`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->



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
* [More Ruby AWS IAM code examples](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/ruby_iam_code_examples.html)
* [SDK for Ruby Developer Guide](https://aws.amazon.com/developer/language/ruby/)
* [SDK for Ruby Amazon IAM Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/IAM.html)
* [AWS IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
* [AWS IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0