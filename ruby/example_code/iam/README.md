# IAM code examples for the SDK for Ruby
## Overview
These examples show how to create and manage AWS Identity and Access Management (IAM) users, credentials, and permissions using the SDK for Ruby.

IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Attach a policy to a role](scenario_create_user_assume_role.rb) (`AttachRolePolicy`)

* [Create a policy](scenario_create_user_assume_role.rb) (`CreatePolicy`)

* [Create a role](scenario_create_user_assume_role.rb) (`CreateRole`)

* [Create a service-linked role](iam_wrapper.rb) (`CreateServiceLinkedRole`)

* [Create a user](scenario_create_user_assume_role.rb) (`CreateUser`)

* [Create an access key](scenario_create_user_assume_role.rb) (`CreateAccessKey`)

* [Create an inline policy for a user](scenario_create_user_assume_role.rb) (`CreateUser`)

* [Delete a policy](scenario_create_user_assume_role.rb) (`DeletePolicy`)

* [Delete a role](scenario_create_user_assume_role.rb) (`DeleteRole`)

* [Delete a service-linked role](iam_wrapper.rb) (`DeleteRole`)

* [Delete a user](scenario_create_user_assume_role.rb) (`DeleteUser`)

* [Delete an access key](scenario_create_user_assume_role.rb) (`DeleteAccessKey`)

* [Delete an inline policy from a user](scenario_create_user_assume_role.rb) (`DeletePolicy`)

* [Detach a policy from a role](scenario_create_user_assume_role.rb) (`DetachRolePolicy`)

* [Get a policy](iam_wrapper.rb) (`GetPolicy`)

* [Get a role](iam_wrapper.rb) (`GetRole`)

* [Get the account password policy](iam_wrapper.rb) (`GetAccountPasswordPolicy`)

* [List SAML providers](iam_wrapper.rb) (`ListSAMLProviders`)

* [List a user's access keys](scenario_create_user_assume_role.rb) (`ListAccessKeys`)

* [List groups](iam_wrapper.rb) (`ListGroups`)

* [List policies](iam_wrapper.rb) (`ListPolicies`)

* [List policies attached to a role](scenario_create_user_assume_role.rb) (`ListAttachedRolePolicies`)

* [List roles](iam_wrapper.rb) (`ListRoles`)

* [List users](iam_wrapper.rb) (`ListUsers`)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create a user and assume a role](scenario_create_user_assume_role.rb)





## Run the examples

### Prerequisites

See the [Ruby README.md(https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md) for pre-requisites.

### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
