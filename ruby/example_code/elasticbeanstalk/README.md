# ElasticBeanstalk code examples for the SDK for Ruby
## Overview
These examples show how to create and manage AWS Elastic Beanstalk environments using the SDK for Ruby.

ElasticBeanstalk allows you to quickly deploy and manage applications in the AWS Cloud without worrying about the infrastructure that runs those applications.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [List stacks](./eb_list_stacks.rb) (`ListAvailableSolutionStacks`)

* [List all apps](./elb-ruby-example-list-all-apps.rb) (`DescribeApplications, DescribeEnvironments`)

* [List details of an app](./elb-ruby-example-list-name-description-url-myrailsapp.rb) (`DescribeApplications`)

* [Update an app](./elb-ruby-example-update-myrailsapp.rb) (`UpdateApplication`)






## Run the examples

### Prerequisites

See the [Ruby README.md](../../../ruby/README.md) for prerequisites.

### Instructions
The easiest way to interact with this example code is by invoking [Single Actions](#single-actions) from your command line. This may require some modification to override hard-coded values, and some actions also expect runtime parameters. For example, `ruby some_action.rb ARG1 ARG2` will invoke `some_action.rb` with two arguments.

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
