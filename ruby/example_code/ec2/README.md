# EC2 code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon Elastic Compute Cloud (Amazon EC2) instances and supporting infrastructure using the SDK for Ruby.

EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create an instance](./ec2-ruby-example-create-instance.rb) (`CreateInstance`)

* [Create a key pair](./ec2-ruby-example-create-key-pair.rb) (`CreateKeyPair`)

* [Create a route table](./ec2-ruby-example-create-route-table.rb) (`CreateRouteTable`)

* [Create a security group](./ec2-ruby-example-create-security-group.rb) (`CreateSecurityGroup`)

* [Create a subnet](./ec2-ruby-example-create-subnet.rb) (`CreateSubnet`)

* [Create a VPC](./ec2-ruby-example-create-vpc.rb) (`CreateVPC`)

* [Get instance information by tag](./ec2-ruby-example-get-instance-info-by-tag.rb) (`DescribeInstances`)

* [List instance state](./ec2-ruby-example-list-state-instance-i-123abc.rb) (`DescribeInstances`)

* [Reboot an instance](./ec2-ruby-example-reboot-instance-i-123abc.rb) (`RebootInstance`)

* [Display list of available AWS Regions](./ec2-ruby-example-regions-availability-zones.rb) (`DescribeRegions`)

* [Start an instance](./ec2-ruby-example-start-instance-i-123abc.rb) (`StarInstance`)

* [Stop an instance](./ec2-ruby-example-stop-instance-i-123abc.rb) (`StopInstance`)

* [Terminate an instance](./ec2-ruby-example-terminate-instance-i-123abc.rb) (`TerminateInstance)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create an internet gateway and attach it to a VPC](./ec2-ruby-example-attach-igw-vpc.rb)

* [Manage addresses](./ec2-ruby-example-elastic-ips.rb)

* [Manage key pairs](./ec2-ruby-example-key-pairs.rb)

* [Manage security groups](./ec2-ruby-example-security-group.rb)

* [Manage instances](./ec2-ruby-example-manage-instances.rb)





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
