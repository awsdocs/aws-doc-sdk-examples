# Amazon EC2 code examples for the AWS CLI with Bash script

## Overview

Shows how to use the AWS Command Line Interface with Bash script to work with Amazon Elastic Compute Cloud (Amazon EC2).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `aws-cli` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](get_started_with_ec2_instances.sh)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](ec2_operations.sh#L224)
- [AssociateAddress](ec2_operations.sh#L295)
- [AuthorizeSecurityGroupIngress](ec2_operations.sh#L488)
- [CreateKeyPair](ec2_operations.sh#L17)
- [CreateSecurityGroup](ec2_operations.sh#L149)
- [DeleteKeyPair](ec2_operations.sh#L1442)
- [DeleteSecurityGroup](ec2_operations.sh#L1384)
- [DescribeImages](ec2_operations.sh#L1318)
- [DescribeInstanceTypes](ec2_operations.sh#L953)
- [DescribeInstances](ec2_operations.sh#L876)
- [DescribeKeyPairs](ec2_operations.sh#L93)
- [DescribeSecurityGroups](ec2_operations.sh#L587)
- [DisassociateAddress](ec2_operations.sh#L368)
- [ReleaseAddress](ec2_operations.sh#L428)
- [RunInstances](ec2_operations.sh#L651)
- [StartInstances](ec2_operations.sh#L754)
- [StopInstances](ec2_operations.sh#L815)
- [TerminateInstances](ec2_operations.sh#L1252)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.basics.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basics.ec2_Scenario_GetStartedInstances.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `aws-cli` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [AWS CLI with Bash script Amazon EC2 reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0