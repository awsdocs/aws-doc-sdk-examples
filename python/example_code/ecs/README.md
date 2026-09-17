# Amazon ECS code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Elastic Container Service (Amazon ECS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon ECS is a highly scalable, fast, container management service that makes it easy to run, stop, and manage Docker containers on a cluster of Amazon EC2 instances._

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

- [Hello Amazon ECS](ecs_hello.py#L11) (`ListClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn Amazon ECS basics](scenarios/ecs_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](ecs_wrapper.py#L42)
- [CreateService](ecs_wrapper.py#L271)
- [DeleteCluster](ecs_wrapper.py#L518)
- [DeleteService](ecs_wrapper.py#L447)
- [DeregisterTaskDefinition](ecs_wrapper.py#L487)
- [DescribeClusters](ecs_wrapper.py#L143)
- [DescribeServices](ecs_wrapper.py#L368)
- [DescribeTasks](ecs_wrapper.py#L234)
- [ListTasks](ecs_wrapper.py#L327)
- [RegisterTaskDefinition](ecs_wrapper.py#L76)
- [RunTask](ecs_wrapper.py#L178)
- [UpdateService](ecs_wrapper.py#L406)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon ECS

This example shows you how to get started using Amazon ECS.

```
python ecs_hello.py
```

#### Learn Amazon ECS basics

This example shows you how to learn Amazon ECS basics.

- Create an ECS cluster.
- Register a Fargate task definition.
- Run a standalone task.
- Create a service.
- List tasks in the service.
- Describe and update the service.
- Clean up all resources.

<!--custom.basic_prereqs.ecs_Scenario.start-->
<!--custom.basic_prereqs.ecs_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/ecs_basics_scenario.py
```


<!--custom.basics.ecs_Scenario.start-->
<!--custom.basics.ecs_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon ECS Developer Guide](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)
- [Amazon ECS API Reference](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) Amazon ECS reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ecs.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
