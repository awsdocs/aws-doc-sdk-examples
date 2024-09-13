# AWS Glue code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with AWS Glue.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Glue is a scalable, serverless data integration service that makes it easy to discover, prepare, and combine data for analytics, machine learning, and application development._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](GettingStartedWithGlue.php)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCrawler](GettingStartedWithGlue.php#L47)
- [CreateJob](GettingStartedWithGlue.php#L110)
- [DeleteCrawler](GettingStartedWithGlue.php#L180)
- [DeleteDatabase](GettingStartedWithGlue.php#L173)
- [DeleteJob](GettingStartedWithGlue.php#L159)
- [DeleteTable](GettingStartedWithGlue.php#L166)
- [GetCrawler](GettingStartedWithGlue.php#L69)
- [GetDatabase](GettingStartedWithGlue.php#L58)
- [GetJobRun](GettingStartedWithGlue.php#L120)
- [GetJobRuns](GettingStartedWithGlue.php#L130)
- [GetTables](GettingStartedWithGlue.php#L58)
- [ListJobs](GettingStartedWithGlue.php#L151)
- [StartCrawler](GettingStartedWithGlue.php#L47)
- [StartJobRun](GettingStartedWithGlue.php#L58)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a crawler that crawls a public Amazon S3 bucket and generates a database of CSV-formatted metadata.
- List information about databases and tables in your AWS Glue Data Catalog.
- Create a job to extract CSV data from the S3 bucket, transform the data, and load JSON-formatted output into another S3 bucket.
- List information about job runs, view transformed data, and clean up resources.

<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.end-->


<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [SDK for PHP AWS Glue reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Glue.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0