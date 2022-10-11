# IAM code examples for the AWS SDK for Ruby (v3)
## Overview
These examples show how to create and manage AWS users, credentials, and permissions using the AWS SDK for Ruby (v3).

AWS Identity and Access Management (IAM) is a web service for securely controlling access to AWS services. With IAM, you can centrally manage users, security credentials such as access keys, and permissions that control which AWS resources users and applications can access.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

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



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create a user and assume a role](scenario_create_user_assume_role.rb)





## Run the examples


### Prerequisites

1. An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.

1. AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the *AWS SDK for Ruby Developer Guide*.

1. To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Programming Language website.

1. To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.

1. The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the *AWS SDK for Ruby Developer Guide*.



### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your Command Line Interface (CLI). For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!

* To propose a new example, submit an [Enhancement Request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fenhancement&template=enhancement.yaml&title=%5BEnhancement%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~2 min).
* To fix a bug, submit a [Bug Report](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fbug&template=bug.yaml&title=%5BBug%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~5 min).
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)
### Testing
⚠️ Running these tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
