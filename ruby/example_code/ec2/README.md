# EC2 code examples for the AWS SDK for Ruby (v3)
## Overview
These examples show how to create and manage Amazon Elastic Compute Cloud (Amazon EC2) instances and supporting infrastructure using the AWS SDK for Ruby (v3).

Amazon Elastic Compute Cloud (Amazon EC2) is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create an instance](./ec2-ruby-example-create-instance.rb)

* [Create a key pair](./ec2-ruby-example-create-key-pair.rb)

* [Create a route table](./ec2-ruby-example-create-route-table.rb)

* [Create a security group](./ec2-ruby-example-create-security-group.rb)

* [Create a subnet](./ec2-ruby-example-create-subnet.rb)

* [Create a VPC](./ec2-ruby-example-create-vpc.rb)

* [Get instance information by tag](./ec2-ruby-example-get-instance-info-by-tag.rb)

* [List state instances](./ec2-ruby-example-list-state-instance-i-123abc.rb)

* [Reboot an instance](./ec2-ruby-example-reboot-instance-i-123abc.rb)

* [Display list of available AWS Regions](./ec2-ruby-example-regions-availability-zones.rb)

* [Start an instance](./ec2-ruby-example-start-instance-i-123abc.rb)

* [Stop an instance](./ec2-ruby-example-stop-instance-i-123abc.rb)

* [Terminate an instance](./ec2-ruby-example-terminate-instance-i-123abc.rb)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create an Internet gateway and attach it to a VPC](./ec2-ruby-example-attach-igw-vpc.rb)

* [Manage addresses](./ec2-ruby-example-elastic-ips.rb)

* [Manage key pairs](./ec2-ruby-example-key-pairs.rb)

* [Manage security groups](./ec2-ruby-example-security-group.rb)

* [Manage instances](./ec2-ruby-example-manage-instances.rb)





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
