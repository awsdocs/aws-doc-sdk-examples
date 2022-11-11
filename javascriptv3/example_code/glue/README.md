# AWS Glue code examples for the SDK for JavaScript in Node.js

## Overview

Shows how to use the AWS SDK for JavaScript in Node.js with AWS Glue.

AWS Glue is a serverless data integration service that makes it easier to discover, prepare, move, and integrate data from multiple sources for analytics, machine learning (ML), and application development.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a crawler](./actions/create-crawler.js)(`CreateCrawler`)
- [Create a job definition](./actions/create-job.js)(`CreateJob`)
- [Delete a crawler](./actions/delete-crawler.js)(`DeleteCrawler`)
- [Delete a database from the Data Catalog](./actions/delete-database.js)(`DeleteDatabase`)
- [Delete a job definition](./actions/delete-job.js)(`DeleteJob`)
- [Delete a table from a database](./actions/delete-table.js)(`DeleteTable`)
- [Get a crawler](./actions/get-crawler.js)(`GetCrawler`)
- [Get a database from the Data Catalog](./actions/get-database.js)(`GetDatabase`)
- [Get a job run](./actions/get-job.js)(`GetJob`)
- [Get runs of a job](./actions/get-job-runs.js)(`GetJobRuns`)
- [Get tables from a database](./actions/get-tables.js)(`GetTables`)
- [List job definitions](./actions/list-jobs.js)(`ListJobs`)
- [Start a crawler](./actions/start-crawler.js)(`StartCrawler`)
- [Start a job run](./actions/start-job-run.js)(`StartJobRun`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started running crawlers and jobs](./scenarios/basic/index.js)

## Run the examples

### Prerequisites

1. [Set up AWS SDK for JavaScript](../README.md).
1. Run `yarn`.

### Instructions

**Run a single action**

1. Create a new file in this directory and run `import { functionName } from "./actions/action-name.js"`.
   Replace `action-name` with the file name of the action to run, and replace `functionName` with the name of
   the exported function in that file.
1. Call the imported function with its required parameters and log the result.

**Run a scenario**

> ### Get started running crawlers and jobs
>
> ---
>
> Prerequisites:
>
> 1. [Install the AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html#getting_started_install).
> 1. Run `cdk bootstrap`.
> 1. Change directories to `resources/cdk/glue_role_bucket`.
> 1. Run `cdk deploy` and save the output.
> 1. Upload `python/example_code/glue/flight_etl_job_script.py` to the S3 bucket created in the preceding step.
> 1. Create a .env file with the following values.
>    Set the value of BUCKET_NAME and ROLE_NAME to the values output from
>    the CDK.
> ```
>    BUCKET_NAME = 
>    ROLE_NAME = 
>    PYTHON_SCRIPT_KEY = flight_etl_job_script.py
>    S3_TARGET_PATH = s3://crawler-public-us-east-1/flight/2016/csv
>    DATABASE_NAME = doc-example-database
>    TABLE_PREFIX = doc-example-
>    TABLE_NAME = doc-example-csv
>    CRAWLER_NAME = s3-flight-data-crawler
>    JOB_NAME = flight_etl_job
>```
> Running the scenario:
>
> 1. Run `node ./scenarios/basic`.
> 2. The scenario will create a crawler, create a job, run the crawler, and run the job. User input will be required
>    for getting more information about job runs, and for clean up options.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

### Unit tests

1. Run `yarn`.
1. Run `yarn test`.

### Integration tests

1. Run `yarn`.
1. Run `yarn integration-test`.

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [AWS Glue client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-glue/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
