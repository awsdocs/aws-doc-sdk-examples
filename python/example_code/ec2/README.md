# Amazon EC2 code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Elastic Compute Cloud (Amazon EC2).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](hello/hello_ec2.py#L11) (`DescribeSecurityGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_get_started_instances.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](elastic_ip.py#L39)
- [AssociateAddress](elastic_ip.py#L64)
- [AuthorizeSecurityGroupIngress](security_group.py#L67)
- [CreateKeyPair](key_pair.py#L39)
- [CreateLaunchTemplate](../../cross_service/resilient_service/auto_scaler.py#L346)
- [CreateSecurityGroup](security_group.py#L42)
- [DeleteKeyPair](key_pair.py#L100)
- [DeleteLaunchTemplate](../../cross_service/resilient_service/auto_scaler.py#L409)
- [DeleteSecurityGroup](security_group.py#L137)
- [DescribeAvailabilityZones](../../cross_service/resilient_service/auto_scaler.py#L438)
- [DescribeIamInstanceProfileAssociations](../../cross_service/resilient_service/auto_scaler.py#L184)
- [DescribeImages](instance.py#L252)
- [DescribeInstanceTypes](instance.py#L276)
- [DescribeInstances](instance.py#L105)
- [DescribeKeyPairs](key_pair.py#L71)
- [DescribeSecurityGroups](security_group.py#L106)
- [DescribeSubnets](../../cross_service/resilient_service/auto_scaler.py#L731)
- [DescribeVpcs](../../cross_service/resilient_service/auto_scaler.py#L630)
- [DisassociateAddress](elastic_ip.py#L100)
- [RebootInstances](../../cross_service/resilient_service/auto_scaler.py#L21)
- [ReleaseAddress](elastic_ip.py#L140)
- [ReplaceIamInstanceProfileAssociation](../../cross_service/resilient_service/auto_scaler.py#L205)
- [RunInstances](instance.py#L41)
- [StartInstances](instance.py#L189)
- [StopInstances](instance.py#L220)
- [TerminateInstances](instance.py#L157)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../../cross_service/resilient_service/runner.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.

```
python hello/hello_ec2.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.end-->

Start the example by running the following at a command prompt:

```
python scenario_get_started_instances.py
```


<!--custom.basics.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basics.ec2_Scenario_GetStartedInstances.end-->


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

Start the example by running the following at a command prompt:

```
python ../../cross_service/resilient_service/runner.py
```


<!--custom.scenarios.cross_ResilientService.start-->
Complete details and instructions on how to run this example can be found in the
[README](../../cross_service/resilient_service/README.md) for the example.
<!--custom.scenarios.cross_ResilientService.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Python Amazon EC2 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ec2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0