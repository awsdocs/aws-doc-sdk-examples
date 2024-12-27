# Amazon ECR code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Elastic Container Registry (Amazon ECR).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon ECR is a fully managed Docker container registry that makes it easy for developers to store, manage, and deploy Docker container images._

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

- [Hello Amazon ECR](hello/hello_ecr.py#L4) (`listImages`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](ecr_getting_started.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateRepository](ecr_wrapper.py#L38)
- [DeleteRepository](ecr_wrapper.py#L66)
- [DescribeImages](ecr_wrapper.py#L207)
- [DescribeRepositories](ecr_wrapper.py#L161)
- [GetAuthorizationToken](ecr_wrapper.py#L142)
- [GetRepositoryPolicy](ecr_wrapper.py#L115)
- [PutLifeCyclePolicy](ecr_wrapper.py#L183)
- [SetRepositoryPolicy](ecr_wrapper.py#L88)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon ECR

This example shows you how to get started using Amazon ECR.

```
python hello/hello_ecr.py
```

#### Learn the basics

This example shows you how to do the following:

- Create an Amazon ECR repository.
- Set repository policies.
- Retrieve repository URIs.
- Get Amazon ECR authorization tokens.
- Set lifecycle policies for Amazon ECR repositories.
- Push a Docker image to an Amazon ECR repository.
- Verify the existence of an image in an Amazon ECR repository.
- List Amazon ECR repositories for your account and get details about them.
- Delete Amazon ECR repositories.

<!--custom.basic_prereqs.ecr_Scenario_RepositoryManagement.start-->
<!--custom.basic_prereqs.ecr_Scenario_RepositoryManagement.end-->

Start the example by running the following at a command prompt:

```
python ecr_getting_started.py
```


<!--custom.basics.ecr_Scenario_RepositoryManagement.start-->
<!--custom.basics.ecr_Scenario_RepositoryManagement.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon ECR User Guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)
- [Amazon ECR API Reference](https://docs.aws.amazon.com/AmazonECR/latest/APIReference/Welcome.html)
- [SDK for Python Amazon ECR reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/scheduler.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
