# AWS Glue code examples for the AWS SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to manage AWS Glue resources.

*AWS Glue is a serverless data-preparation service for extract, transform, and load 
(ETL) operations.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Create a crawler](glue_wrapper.py)
(`CreateCrawler`)
* [Create a job definition](glue_wrapper.py)
(`CreateJob`)
* [Delete a crawler](glue_wrapper.py)
(`DeleteJob`)
* [Delete a database from the AWS Glue Data Catalog](glue_wrapper.py)
(`DeleteDatabase`)
* [Delete a job definition](glue_wrapper.py)
(`DeleteJob`)
* [Delete a table from a database](glue_wrapper.py)
(`DeleteTable`)
* [Get a crawler](glue_wrapper.py)
(`GetCrawler`)
* [Get a database from the AWS Glue Data Catalog](glue_wrapper.py)
(`GetDatabase`)
* [Get a job run](glue_wrapper.py)
(`GetJobRun`)
* [Get runs of a job](glue_wrapper.py)
(`GetJobRuns`)
* [Get tables from a database](glue_wrapper.py)
(`GetTables`)
* [List job definitions](glue_wrapper.py)
(`ListJobs`)
* [Start a crawler](glue_wrapper.py)
(`StartCrawler`)
* [Start a job run](glue_wrapper.py)
(`StartJobRun`)

### Scenarios

* [Get started running crawlers and jobs](scenario_getting_started_crawlers_and_jobs.py)

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

You can deploy the scaffold resources at a command prompt.

```
python scaffold.py deploy
```

This outputs a role and bucket name similar to the following.

```
Outputs:
        RoleName: AWSGlueServiceRole-DocExample
        BucketName: doc-example-glue-scenario-docexampleglue6e2f12e5-3zjkuexample
```

Start the scenario at a command prompt, passing the role and bucket name to it.

```
python scenario_getting_started_crawler_and_jobs.py AWSGlueServiceRole-DocExample doc-example-glue-scenario-docexampleglue6e2f12e5-3zjkuexample
```

Destroy scaffold resources at a command prompt.

```
python scaffold.py destroy
```

If you prefer, you can deploy and destroy scaffold resources by using the AWS Cloud
Development Kit (AWS CDK). To do this, run `cdk deploy` or `cdk destroy` in the
[/resources/cdk/glue_role_bucket](/resources/cdk/glue_role_bucket) folder.

### Prerequisites

Prerequisites for running the examples for this service can be found in the 
[README](../../README.md#Prerequisites) in the Python folder.

## Tests

Instructions for running the tests for this service can be found in the
[README](../../README.md#Tests) in the Python folder.

## Additional resources

* [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
* [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
* [AWS SDK for Python Glue Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/glue.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
