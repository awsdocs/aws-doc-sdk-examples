# AWS Glue code examples for the AWS SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to manage AWS Glue resources.

*AWS Glue is a serverless data-preparation service for extract, transform, and load
(ETL) operations.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

# TODO update these links to PHP
* [Create a crawler](GettingStartedWithGlue.php)
  (`CreateCrawler`)
* [Create a job definition](GettingStartedWithGlue.php)
  (`CreateJob`)
* [Delete a crawler](GettingStartedWithGlue.php)
  (`DeleteJob`)
* [Delete a database from the AWS Glue Data Catalog](GettingStartedWithGlue.php)
  (`DeleteDatabase`)
* [Delete a job definition](GettingStartedWithGlue.php)
  (`DeleteJob`)
* [Delete a table from a database](GettingStartedWithGlue.php)
  (`DeleteTable`)
* [Get a crawler](GettingStartedWithGlue.php)
  (`GetCrawler`)
* [Get a database from the AWS Glue Data Catalog](GettingStartedWithGlue.php)
  (`GetDatabase`)
* [Get a job run](GettingStartedWithGlue.php)
  (`GetJobRun`)
* [Get runs of a job](GettingStartedWithGlue.php)
  (`GetJobRuns`)
* [Get tables from a database](GettingStartedWithGlue.php)
  (`GetTables`)
* [List job definitions](GettingStartedWithGlue.php)
  (`ListJobs`)
* [Start a crawler](GettingStartedWithGlue.php)
  (`StartCrawler`)
* [Start a job run](GettingStartedWithGlue.php)
  (`StartJobRun`)

### Scenarios

* [Get started running crawlers and jobs](GettingStartedWithGlue.php)

## Running the examples

### Get started running crawlers and jobs scenario

This interactive scenario runs at a command prompt and shows you how to use
AWS Glue to do the following:

1. Create and run a crawler that crawls a public Amazon Simple Storage
   Service (Amazon S3) bucket and generates a metadata database that describes the
   CSV-formatted data it finds.
2. List information about databases and tables in your AWS Glue Data Catalog.
3. Create and run a job that extracts CSV data from the source Amazon S3 bucket,
   transforms it by removing and renaming fields, and loads JSON-formatted output into
   another S3 bucket.
4. List information about job runs and view some of the transformed data.
5. Delete all resources created by the demo.

This scenario requires the following scaffold resources that are defined in the
accompanying AWS CloudFormation script `setup_scenario_getting_started.yaml`.

* An Amazon S3 bucket that can contain the Python ETL job script and can receive
  output data.
* An AWS Identity and Access Management (IAM) role that can be assumed by AWS Glue.
  The role must grant read-write access to the S3 bucket and standard rights needed by
  AWS Glue.

Install dependencies by using Composer:

```
composer install
```

After your composer dependencies are installed, you can run the interactive getting started file directly with the
following from the `aws-doc-sdk-examples\php\glue\` directory:

```
php Runner.php
```   

Alternatively, you can have the choices automatically selected by running the file as part of a PHPUnit test with the
following:

```
..\..\vendor\bin\phpunit GlueBasicsTests.php
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured as described in
  the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide]
  (https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- PHP 7.1 or later.
- Composer installed.

## Additional resources

* [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
* [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
* [AWS SDK for PHP Glue Client](https://docs.aws.amazon.com/aws-sdk-php/v3/api/class-Aws.Glue.GlueClient.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
