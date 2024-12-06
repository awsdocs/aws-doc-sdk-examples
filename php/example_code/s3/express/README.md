# S3 Directory Buckets code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon S3 Directory Buckets.

<!--custom.overview.start-->
<!--custom.overview.end-->

_S3 Directory Buckets are designed to store data within a single AWS Zone. Directory buckets organize data hierarchically into directories, providing a structure similar to a file system._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](S3ExpressBasics.php)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Set up a VPC and VPC Endpoint.
- Set up the Policies, Roles, and User to work with S3 directory buckets and the S3 Express One Zone storage class.
- Create two S3 Clients.
- Create two buckets.
- Create an object and copy it over.
- Demonstrate performance difference.
- Populate the buckets to show the lexicographical difference.
- Prompt the user to see if they want to clean up the resources.

<!--custom.basic_prereqs.s3-directory-buckets_Scenario_ExpressBasics.start-->
<!--custom.basic_prereqs.s3-directory-buckets_Scenario_ExpressBasics.end-->


<!--custom.basics.s3-directory-buckets_Scenario_ExpressBasics.start-->
<!--custom.basics.s3-directory-buckets_Scenario_ExpressBasics.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [S3 Directory Buckets User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/directory-buckets-overview.html)
- [S3 Directory Buckets API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for PHP S3 Directory Buckets reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.S3-directory-buckets.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
