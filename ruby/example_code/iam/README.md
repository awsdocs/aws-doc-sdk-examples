# IAM code examples for AWS SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to manage AWS Identity and Access
Management (IAM) resources.

*IAM is a web service for securely controlling access to AWS services. With IAM, you
can centrally manage users, security credentials such as access keys, and permissions
that control which AWS resources users and applications can access.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Attach a policy to a role](scenario_create_user_assume_role.rb)
* [Create a policy](scenario_create_user_assume_role.rb)
* [Create a role](scenario_create_user_assume_role.rb)
* [Create a service-linked role](iam_wrapper.rb)
* [Create a user](scenario_create_user_assume_role.rb)
* [Create an access key](scenario_create_user_assume_role.rb)
* [Create an inline policy for a user](scenario_create_user_assume_role.rb)
* [Delete a policy](scenario_create_user_assume_role.rb)
* [Delete a role](scenario_create_user_assume_role.rb)
* [Delete a service-linked role](iam_wrapper.rb)
* [Delete a user](scenario_create_user_assume_role.rb)
* [Delete an access key](scenario_create_user_assume_role.rb)
* [Delete an inline policy from a user](scenario_create_user_assume_role.rb)
* [Detach a policy from a role](scenario_create_user_assume_role.rb)
* [Get a policy](iam_wrapper.rb)
* [Get a role](iam_wrapper.rb)
* [Get the account password policy](iam_wrapper.rb)
* [List SAML providers](iam_wrapper.rb)
* [List a user's access keys](scenario_create_user_assume_role.rb)
* [List groups](iam_wrapper.rb)
* [List policies](iam_wrapper.rb)
* [List policies attached to a role](scenario_create_user_assume_role.rb)
* [List roles](iam_wrapper.rb)
* [List users](iam_wrapper.rb)

### Scenario

* [Create a user and assume a role](scenario_create_user_assume_role.rb)

## Running the examples

Each scenario and usage demo can be run from the command prompt. Some 
scenarios run through a script without requiring input. Others interactively ask for 
more information as they run.

To start a scenario, run it at a command prompt.

```
ruby scenario_create_user_assume_role.rb
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the 
[README](../../README.md#Prerequisites) in the Ruby folder.

## Tests

Instructions for running the tests for this service can be found in the
[README](../../README.md#Tests) in the Ruby folder.

## Additional resources

* [AWS Identity and Access Management User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
* [AWS Identity and Access Management API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
* [AWS SDK for Ruby Aws::IAM Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/IAM.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
