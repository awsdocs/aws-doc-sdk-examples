# AWS Glue code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS Glue.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Glue](hello.js#L6) (`ListJobs`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](actions/create-crawler.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCrawler](actions/create-crawler.js#L6)
- [CreateJob](actions/create-job.js#L6)
- [DeleteCrawler](actions/delete-crawler.js#L6)
- [DeleteDatabase](actions/delete-database.js#L6)
- [DeleteJob](actions/delete-job.js#L6)
- [DeleteTable](actions/delete-table.js#L6)
- [GetCrawler](actions/get-crawler.js#L6)
- [GetDatabase](actions/get-database.js#L6)
- [GetDatabases](actions/get-databases.js#L6)
- [GetJob](actions/get-job.js#L6)
- [GetJobRun](actions/get-job-run.js#L6)
- [GetJobRuns](actions/get-job-runs.js#L6)
- [GetTables](actions/get-tables.js#L6)
- [ListJobs](actions/list-jobs.js#L6)
- [StartCrawler](actions/start-crawler.js#L6)
- [StartJobRun](actions/start-job-run.js#L6)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Glue

This example shows you how to get started using AWS Glue.

```bash
node ./hello.js
```

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
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [SDK for JavaScript (v3) AWS Glue reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/glue)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0