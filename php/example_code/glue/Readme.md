# AWS Glue code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with AWS Glue.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Glue is a scalable, serverless data integration service that makes it easy to discover, prepare, and combine data for analytics, machine learning, and application development._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a crawler](GettingStartedWithGlue.php#L48) (`CreateCrawler`)
- [Create a job definition](GettingStartedWithGlue.php#L55) (`CreateJob`)
- [Delete a crawler](GettingStartedWithGlue.php#L181) (`DeleteCrawler`)
- [Delete a database from the Data Catalog](GettingStartedWithGlue.php#L174) (`DeleteDatabase`)
- [Delete a job definition](GettingStartedWithGlue.php#L160) (`DeleteJob`)
- [Delete a table from a database](GettingStartedWithGlue.php#L167) (`DeleteTable`)
- [Get a crawler](GettingStartedWithGlue.php#L70) (`GetCrawler`)
- [Get a database from the Data Catalog](GettingStartedWithGlue.php#L59) (`GetDatabase`)
- [Get a job run](GettingStartedWithGlue.php#L108) (`GetJobRun`)
- [Get runs of a job](GettingStartedWithGlue.php#L108) (`GetJobRuns`)
- [Get tables from a database](GettingStartedWithGlue.php#L59) (`GetTables`)
- [List job definitions](GettingStartedWithGlue.php#L152) (`ListJobs`)
- [Start a crawler](GettingStartedWithGlue.php#L48) (`StartCrawler`)
- [Start a job run](GettingStartedWithGlue.php#L108) (`StartJobRun`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with crawlers and jobs](GlueService.php)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### Get started running crawlers and jobs scenario

This interactive scenario runs at a command prompt and shows you how to use
AWS Glue to do the following:

1. Create and run a crawler that crawls a public Amazon Simple Storage
   Service (Amazon S3) bucket and generates a metadata database that describes the
   CSV-formatted data it finds.
2. List information about databases and tables in your AWS Glue Data Catalog.
3. Create and run a job that extracts CSV data from the source S3 bucket,
   transforms it by removing and renaming fields, and loads JSON-formatted output into
   another S3 bucket.
4. List information about job runs and view some of the transformed data.
5. Delete all resources created by the demo.

This scenario requires the following scaffold resources that are defined in the
accompanying AWS CloudFormation script `setup_scenario_getting_started.yaml`.

- An S3 bucket that can contain the Python ETL job script and can receive
  output data.
- An AWS Identity and Access Management (IAM) role that can be assumed by AWS Glue.
  The role must grant read-write access to the S3 bucket and standard rights needed by
  AWS Glue.

Install dependencies by using Composer:

```
composer install
```

After your composer dependencies are installed, you can run the interactive getting started file directly by using the
following command from the `aws-doc-sdk-examples\php\glue\` directory:

```
php Runner.php
```

Alternatively, you can have the choices automatically selected by running the file as part of a PHPUnit test with the
following:

```
..\..\vendor\bin\phpunit GlueBasicsTests.php
```

<!--custom.instructions.end-->



#### Get started with crawlers and jobs

This example shows you how to do the following:

- Create a crawler that crawls a public Amazon S3 bucket and generates a database of CSV-formatted metadata.
- List information about databases and tables in your AWS Glue Data Catalog.
- Create a job to extract CSV data from the S3 bucket, transform the data, and load JSON-formatted output into another S3 bucket.
- List information about job runs, view transformed data, and clean up resources.

<!--custom.scenario_prereqs.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.scenario_prereqs.glue_Scenario_GetStartedCrawlersJobs.end-->


<!--custom.scenarios.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.scenarios.glue_Scenario_GetStartedCrawlersJobs.end-->

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