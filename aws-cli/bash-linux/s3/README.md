# Amazon S3 code examples for the AWS CLI with Bash script

## Overview

Shows how to use the AWS Command Line Interface with Bash script to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

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

- [Learn the basics](s3_getting_started.sh)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](bucket-lifecycle-operations/bucket_operations.sh#L202)
- [CreateBucket](bucket-lifecycle-operations/bucket_operations.sh#L50)
- [DeleteBucket](bucket-lifecycle-operations/bucket_operations.sh#L343)
- [DeleteObject](bucket-lifecycle-operations/bucket_operations.sh#L272)
- [DeleteObjects](bucket-lifecycle-operations/bucket_operations.sh#L303)
- [GetObject](bucket-lifecycle-operations/bucket_operations.sh#L168)
- [HeadBucket](bucket-lifecycle-operations/bucket_operations.sh#L20)
- [ListObjectsV2](bucket-lifecycle-operations/awsdocs_general.sh#L94)
- [PutObject](bucket-lifecycle-operations/bucket_operations.sh#L134)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.basic_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.basics.s3_Scenario_GettingStarted.start-->
<!--custom.basics.s3_Scenario_GettingStarted.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `aws-cli` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [AWS CLI with Bash script Amazon S3 reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0