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
<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->

### Tests
<!--custom.tests.start-->
The example code in this directory is not currently tested.

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources
<!--custom.resources.start-->
* [More ElasticBeanstalk code examples](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/eb-examples.html)
* [SDK for Ruby Developer Guide](https://aws.amazon.com/developer/language/ruby/)
* [SDK for Ruby Amazon ElasticBeanstalk Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/ElasticBeanstalk.html)
* [ElasticBeanstalk Developer Guide](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/Welcome.html)
* [ElasticBeanstalk API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/ElasticBeanstalk.html)
<!--custom.resources.end-->


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
