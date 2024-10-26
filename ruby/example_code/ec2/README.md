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

### Get started

- [Hello Amazon EC2](hello/hello_ec2.rb#L4) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](ec2-ruby-example-elastic-ips.rb#L46)
- [AssociateAddress](ec2-ruby-example-elastic-ips.rb#L62)
- [CreateKeyPair](ec2-ruby-example-key-pairs.rb#L10)
- [CreateRouteTable](ec2-ruby-example-create-route-table.rb#L9)
- [CreateSecurityGroup](ec2-ruby-example-security-group.rb#L10)
- [CreateSubnet](ec2-ruby-example-create-subnet.rb#L10)
- [CreateVpc](ec2-ruby-example-create-vpc.rb#L8)
- [DescribeInstances](ec2-ruby-example-get-all-instance-info.rb#L9)
- [DescribeRegions](ec2-ruby-example-regions-availability-zones.rb#L9)
- [ReleaseAddress](ec2-ruby-example-elastic-ips.rb#L136)
- [StartInstances](ec2-ruby-example-start-instance-i-123abc.rb#L9)
- [StopInstances](ec2-ruby-example-stop-instance-i-123abc.rb#L8)
- [TerminateInstances](ec2-ruby-example-terminate-instance-i-123abc.rb#L12)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.

```
ruby hello/hello_ec2.rb
```


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
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0