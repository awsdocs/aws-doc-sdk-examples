# AWS Batch code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Batch.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Batch](src/main/java/com/example/batch/HelloBatch.java#L6) (`listJobsPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/batch/scenario/BatchScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateComputeEnvironment](src/main/java/com/example/batch/scenario/BatchActions.java#L102)
- [CreateJobQueue](src/main/java/com/example/batch/scenario/BatchActions.java#L193)
- [DeleteComputeEnvironment](src/main/java/com/example/batch/scenario/BatchActions.java#L142)
- [DeleteJobQueue](src/main/java/com/example/batch/scenario/BatchActions.java#L370)
- [DeregisterJobDefinition](src/main/java/com/example/batch/scenario/BatchActions.java#L323)
- [DescribeComputeEnvironments](src/main/java/com/example/batch/scenario/BatchActions.java#L162)
- [DescribeJobQueues](src/main/java/com/example/batch/scenario/BatchActions.java#L394)
- [DescribeJobs](src/main/java/com/example/batch/scenario/BatchActions.java#L490)
- [ListJobsPaginator](src/main/java/com/example/batch/scenario/BatchActions.java#L230)
- [RegisterJobDefinition](src/main/java/com/example/batch/scenario/BatchActions.java#L257)
- [SubmitJob](src/main/java/com/example/batch/scenario/BatchActions.java#L463)
- [UpdateComputeEnvironment](src/main/java/com/example/batch/scenario/BatchActions.java#L439)
- [UpdateJobQueue](src/main/java/com/example/batch/scenario/BatchActions.java#L347)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Batch

This example shows you how to get started using AWS Batch.


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


<!--custom.basics.batch_Scenario.start-->
<!--custom.basics.batch_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Batch User Guide](https://docs.aws.amazon.com/batch/latest/userguide/what-is-batch.html)
- [AWS Batch API Reference](https://docs.aws.amazon.com/batch/latest/APIReference/Welcome.html)
- [SDK for Java 2.x AWS Batch reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
