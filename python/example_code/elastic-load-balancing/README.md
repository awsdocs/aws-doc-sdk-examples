# Elastic Load Balancing - Version 2 code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Elastic Load Balancing - Version 2.

<!--custom.overview.start-->
Most of the example code for Elastic Load Balancing can be found in the  
[python/cross_service/resilient_service](../../cross_service/resilient_service) folder,
which contains the [Build and manage a resilient service](../../cross_service/resilient_service/README.md)
scenario.
<!--custom.overview.end-->

_Elastic Load Balancing - Version 2 automatically distributes your incoming traffic across multiple targets, such as EC2 instances, containers, and IP addresses, in one or more Availability Zones._

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

- [Hello Elastic Load Balancing - Version 2](hello.py#L4) (`DescribeLoadBalancers`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateListener](../../cross_service/resilient_service/load_balancer.py#L191)
- [CreateLoadBalancer](../../cross_service/resilient_service/load_balancer.py#L140)
- [CreateTargetGroup](../../cross_service/resilient_service/load_balancer.py#L28)
- [DeleteLoadBalancer](../../cross_service/resilient_service/load_balancer.py#L251)
- [DeleteTargetGroup](../../cross_service/resilient_service/load_balancer.py#L83)
- [DescribeLoadBalancers](../../cross_service/resilient_service/load_balancer.py#L283)
- [DescribeTargetHealth](../../cross_service/resilient_service/load_balancer.py#L336)

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

#### Hello Elastic Load Balancing - Version 2

This example shows you how to get started using Elastic Load Balancing - Version 2.

```
python hello.py
```


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

- [Elastic Load Balancing - Version 2 User Guide](https://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/what-is-load-balancing.html)
- [Elastic Load Balancing - Version 2 API Reference](https://docs.aws.amazon.com/elasticloadbalancing/latest/APIReference/Welcome.html)
- [SDK for Python Elastic Load Balancing - Version 2 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/elbv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
