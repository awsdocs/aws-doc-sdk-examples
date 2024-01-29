# Amazon EC2 code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

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

- [Allocate an Elastic IP address](ec2-ruby-example-elastic-ips.rb#L47) (`AllocateAddress`)
- [Associate an Elastic IP address with an instance](ec2-ruby-example-elastic-ips.rb#L63) (`AssociateAddress`)
- [Create a Amazon Virtual Private Cloud (Amazon VPC)](ec2-ruby-example-create-vpc.rb#L8) (`CreateVpc`)
- [Create a route table](ec2-ruby-example-create-route-table.rb#L9) (`CreateRouteTable`)
- [Create a security group](ec2-ruby-example-security-group.rb#L10) (`CreateSecurityGroup`)
- [Create a security key pair](ec2-ruby-example-key-pairs.rb#L10) (`CreateKeyPair`)
- [Create a subnet](ec2-ruby-example-create-subnet.rb#L10) (`CreateSubnet`)
- [Describe Regions](ec2-ruby-example-regions-availability-zones.rb#L9) (`DescribeRegions`)
- [Describe instances](ec2-ruby-example-get-all-instance-info.rb#L9) (`DescribeInstances`)
- [Release an Elastic IP address](ec2-ruby-example-elastic-ips.rb#L136) (`ReleaseAddress`)
- [Start an instance](ec2-ruby-example-start-instance-i-123abc.rb#L9) (`StartInstances`)
- [Stop an instance](ec2-ruby-example-stop-instance-i-123abc.rb#L8) (`StopInstances`)
- [Terminate an instance](ec2-ruby-example-terminate-instance-i-123abc.rb#L12) (`TerminateInstances`)


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

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Ruby Amazon EC2 reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Ec2.html)

<!--custom.resources.start-->
* [More Ruby Amazon EC2 code examples](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/ec2-examples.html)
* [SDK for Ruby Developer Guide](https://aws.amazon.com/developer/language/ruby/)
* [SDK for Ruby Amazon EC2 Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/EC2.html)
* [Amazon EC2 Developer Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
* [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0