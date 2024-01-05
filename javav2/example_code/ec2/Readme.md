# Amazon EC2 code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](src/main/java/com/example/ec2/DescribeSecurityGroups.java#L12) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Allocate an Elastic IP address](src/main/java/com/example/ec2/AllocateAddress.java#L11) (`AllocateAddress`)
- [Associate an Elastic IP address with an instance](src/main/java/com/example/ec2/EC2Scenario.java#L326) (`AssociateAddress`)
- [Create a security group](src/main/java/com/example/ec2/CreateSecurityGroup.java#L11) (`CreateSecurityGroup`)
- [Create a security key pair](src/main/java/com/example/ec2/CreateKeyPair.java#L11) (`CreateKeyPair`)
- [Create and run an instance](src/main/java/com/example/ec2/CreateInstance.java#L12) (`RunInstances`)
- [Delete a security group](src/main/java/com/example/ec2/DeleteSecurityGroup.java#L11) (`DeleteSecurityGroup`)
- [Delete a security key pair](src/main/java/com/example/ec2/DeleteKeyPair.java#L12) (`DeleteKeyPair`)
- [Describe instances](src/main/java/com/example/ec2/EC2Scenario.java#L406) (`DescribeInstances`)
- [Disassociate an Elastic IP address from an instance](src/main/java/com/example/ec2/EC2Scenario.java#L309) (`DisassociateAddress`)
- [Get data about a security group](src/main/java/com/example/ec2/EC2Scenario.java#L550) (`DescribeSecurityGroups`)
- [Get data about instance types](src/main/java/com/example/ec2/EC2Scenario.java#L459) (`DescribeInstanceTypes`)
- [List security key pairs](src/main/java/com/example/ec2/DescribeKeyPairs.java#L10) (`DescribeKeyPairs`)
- [Release an Elastic IP address](src/main/java/com/example/ec2/ReleaseAddress.java#L11) (`ReleaseAddress`)
- [Set inbound rules for a security group](src/main/java/com/example/ec2/EC2Scenario.java#L569) (`AuthorizeSecurityGroupIngress`)
- [Start an instance](src/main/java/com/example/ec2/EC2Scenario.java#L361) (`StartInstances`)
- [Stop an instance](src/main/java/com/example/ec2/EC2Scenario.java#L384) (`StopInstances`)
- [Terminate an instance](src/main/java/com/example/ec2/TerminateInstance.java#L11) (`TerminateInstances`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../../usecases/resilient_service/src/main/java/com/example/resilient/Main.java)
- [Get started with instances](src/main/java/com/example/ec2/EC2Scenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.



#### Build and manage a resilient service

This example shows you how to create a load-balanced web service that returns book, movie, and song recommendations. The example shows how the service responds to failures, and how to restructure the service for more resilience when failures occur.

- Use an Amazon EC2 Auto Scaling group to create Amazon Elastic Compute Cloud (Amazon EC2) instances based on a launch template and to keep the number of instances in a specified range.
- Handle and distribute HTTP requests with Elastic Load Balancing.
- Monitor the health of instances in an Auto Scaling group and forward requests only to healthy instances.
- Run a Python web server on each EC2 instance to handle HTTP requests. The web server responds with recommendations and health checks.
- Simulate a recommendation service with an Amazon DynamoDB table.
- Control web server response to requests and health checks by updating AWS Systems Manager parameters.

<!--custom.scenario_prereqs.cross_ResilientService.start-->
<!--custom.scenario_prereqs.cross_ResilientService.end-->


<!--custom.scenarios.cross_ResilientService.start-->
<!--custom.scenarios.cross_ResilientService.end-->

#### Get started with instances

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.scenario_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.scenario_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.scenarios.ec2_Scenario_GetStartedInstances.start-->
<!--custom.scenarios.ec2_Scenario_GetStartedInstances.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon EC2 reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0