# AWS Glue code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Glue.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Glue](hello/hello_glue.py#L4) (`ListJobs`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](glue_wrapper.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCrawler](glue_wrapper.py#L57)
- [CreateJob](glue_wrapper.py#L159)
- [DeleteCrawler](glue_wrapper.py#L367)
- [DeleteDatabase](glue_wrapper.py#L347)
- [DeleteJob](glue_wrapper.py#L305)
- [DeleteTable](glue_wrapper.py#L326)
- [GetCrawler](glue_wrapper.py#L30)
- [GetDatabase](glue_wrapper.py#L113)
- [GetJobRun](glue_wrapper.py#L280)
- [GetJobRuns](glue_wrapper.py#L256)
- [GetTables](glue_wrapper.py#L136)
- [ListJobs](glue_wrapper.py#L235)
- [StartCrawler](glue_wrapper.py#L92)
- [StartJobRun](glue_wrapper.py#L196)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Glue

This example shows you how to get started using AWS Glue.

```
python hello/hello_glue.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a crawler that crawls a public Amazon S3 bucket and generates a database of CSV-formatted metadata.
- List information about databases and tables in your AWS Glue Data Catalog.
- Create a job to extract CSV data from the S3 bucket, transform the data, and load JSON-formatted output into another S3 bucket.
- List information about job runs, view transformed data, and clean up resources.

<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.start-->
This example requires the following scaffold resources that are defined in the
accompanying AWS CloudFormation script `setup_scenario_getting_started.yaml`.

* An Amazon Simple Storage Service (Amazon S3) bucket that can contain the Python ETL 
job script and receive output data.
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

If you prefer, you can deploy and destroy scaffold resources by using the AWS Cloud
Development Kit (AWS CDK). To do this, run `cdk deploy` or `cdk destroy` in the
[/resources/cdk/glue_role_bucket](/resources/cdk/glue_role_bucket) folder.
<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.end-->

Start the example by running the following at a command prompt:

```
python glue_wrapper.py
```


<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.start-->
After the example is done, destroy scaffold resources at a command prompt.

```
python scaffold.py destroy
```
<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [SDK for Python AWS Glue reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/glue.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0