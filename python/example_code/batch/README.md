# AWS Batch code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Batch.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Batch enables you to run batch computing workloads on the AWS Cloud._

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

- [Hello AWS Batch](batch_hello.py#L18) (`listJobsPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/batch_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateComputeEnvironment](batch_wrapper.py#L64)
- [CreateJobQueue](batch_wrapper.py#L109)
- [DeleteComputeEnvironment](batch_wrapper.py#L481)
- [DeleteJobQueue](batch_wrapper.py#L427)
- [DeregisterJobDefinition](batch_wrapper.py#L302)
- [DescribeComputeEnvironments](batch_wrapper.py#L36)
- [DescribeJobs](batch_wrapper.py#L249)
- [ListJobs](batch_wrapper.py#L271)
- [RegisterJobDefinition](batch_wrapper.py#L152)
- [SubmitJob](batch_wrapper.py#L216)
- [UpdateComputeEnvironment](batch_wrapper.py#L447)
- [UpdateJobQueue](batch_wrapper.py#L322)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Batch

This example shows you how to get started using AWS Batch.

```
python batch_hello.py
```

#### Learn the basics

This example shows you how to do the following:

- Create an AWS Batch compute environment.
- Check the status of the compute environment.
- Set up an AWS Batch job queue and job definition.
- Register a job definition.
- Submit an AWS Batch Job.
- Get a list of jobs applicable to the job queue.
- Check the status of job.
- Delete AWS Batch resources.

<!--custom.basic_prereqs.batch_Scenario.start-->
<!--custom.basic_prereqs.batch_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/batch_basics_scenario.py
```


<!--custom.basics.batch_Scenario.start-->
<!--custom.basics.batch_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Batch User Guide](https://docs.aws.amazon.com/batch/latest/userguide/what-is-batch.html)
- [AWS Batch API Reference](https://docs.aws.amazon.com/batch/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) AWS Batch reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/batch.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
