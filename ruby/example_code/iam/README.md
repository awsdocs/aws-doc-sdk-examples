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

- [Attach a policy to a role](scenario_create_user_assume_role.rb#L100) (`AttachRolePolicy`)
- [Create a policy](scenario_create_user_assume_role.rb#L99) (`CreatePolicy`)
- [Create a role](scenario_create_user_assume_role.rb#L72) (`CreateRole`)
- [Create a service-linked role](iam_wrapper.rb#L166) (`CreateServiceLinkedRole`)
- [Create a user](scenario_create_user_assume_role.rb#L37) (`CreateUser`)
- [Create an access key](scenario_create_user_assume_role.rb#L55) (`CreateAccessKey`)
- [Create an inline policy for a user](scenario_create_user_assume_role.rb#L130) (`PutUserPolicy`)
- [Delete a policy](scenario_create_user_assume_role.rb#L215) (`DeletePolicy`)
- [Delete a role](scenario_create_user_assume_role.rb#L215) (`DeleteRole`)
- [Delete a service-linked role](iam_wrapper.rb#L186) (`DeleteServiceLinkedRole`)
- [Delete a user](scenario_create_user_assume_role.rb#L237) (`DeleteUser`)
- [Delete an access key](scenario_create_user_assume_role.rb#L237) (`DeleteAccessKey`)
- [Delete an inline policy from a user](scenario_create_user_assume_role.rb#L237) (`DeleteUserPolicy`)
- [Detach a policy from a role](scenario_create_user_assume_role.rb#L215) (`DetachRolePolicy`)
- [Get a policy](iam_wrapper.rb#L101) (`GetPolicy`)
- [Get a role](iam_wrapper.rb#L49) (`GetRole`)
- [Get the account password policy](iam_wrapper.rb#L133) (`GetAccountPasswordPolicy`)
- [List SAML providers](iam_wrapper.rb#L151) (`ListSAMLProviders`)
- [List a user's access keys](scenario_create_user_assume_role.rb#L237) (`ListAccessKeys`)
- [List groups](iam_wrapper.rb#L118) (`ListGroups`)
- [List policies](iam_wrapper.rb#L81) (`ListPolicies`)
- [List policies attached to a role](scenario_create_user_assume_role.rb#L215) (`ListAttachedRolePolicies`)
- [List roles](iam_wrapper.rb#L29) (`ListRoles`)
- [List users](iam_wrapper.rb#L66) (`ListUsers`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a user and assume a role](scenario_create_user_assume_role.rb)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->



#### Create a user and assume a role

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenario_prereqs.iam_Scenario_CreateUserAssumeRole.end-->

Start the example by running the following at a command prompt:

```
ruby scenario_create_user_assume_role.rb
```

<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.scenarios.iam_Scenario_CreateUserAssumeRole.end-->

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