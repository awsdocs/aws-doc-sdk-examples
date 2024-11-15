# AWS Glue code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Glue.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Glue](Actions/HelloGlue.cs#L4) (`ListJobs`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Actions/GlueWrapper.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCrawler](Actions/GlueWrapper.cs#L23)
- [CreateJob](Actions/GlueWrapper.cs#L76)
- [DeleteCrawler](Actions/GlueWrapper.cs#L120)
- [DeleteDatabase](Actions/GlueWrapper.cs#L134)
- [DeleteJob](Actions/GlueWrapper.cs#L148)
- [DeleteTable](Actions/GlueWrapper.cs#L162)
- [GetCrawler](Actions/GlueWrapper.cs#L176)
- [GetDatabase](Actions/GlueWrapper.cs#L218)
- [GetJobRun](Actions/GlueWrapper.cs#L237)
- [GetJobRuns](Actions/GlueWrapper.cs#L252)
- [GetTables](Actions/GlueWrapper.cs#L284)
- [ListJobs](Actions/GlueWrapper.cs#L308)
- [StartCrawler](Actions/GlueWrapper.cs#L328)
- [StartJobRun](Actions/GlueWrapper.cs#L348)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Glue

This example shows you how to get started using AWS Glue.


#### Learn the basics

This example shows you how to do the following:

- Create a crawler that crawls a public Amazon S3 bucket and generates a database of CSV-formatted metadata.
- List information about databases and tables in your AWS Glue Data Catalog.
- Create a job to extract CSV data from the S3 bucket, transform the data, and load JSON-formatted output into another S3 bucket.
- List information about job runs, view transformed data, and clean up resources.

<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.start-->
This scenario requires the following scaffold resources:
* An S3 bucket that can contain the Python ETL job script and receive
  output data.
* An AWS Identity and Access Management (IAM) role that can be assumed by AWS Glue.
  The role must grant read-write access to the S3 bucket and standard rights needed by
  AWS Glue.


You can deploy and destroy these resources by using the AWS Cloud Development Kit
(AWS CDK). To do this, run `cdk deploy` or `cdk destroy` in the
[/resources/cdk/glue_role_bucket](/resources/cdk/glue_role_bucket) folder.

When the AWS CDK script reports the bucket name and the IAM role that was created, open the `settings.json` file and fill in
  the BucketName, RoleName, and ScriptURL values.

Also copy the Python script `flight_etl_job_script.py` from
[/aws-doc-sdk-examples/python/example_code/glue/flight_etl_job_script.py](/aws-doc-sdk-examples/python/example_code/glue/flight_etl_job_script.py)
to the S3 bucket.

Example:

    "BucketName": "bucket-name-from-cdk-script",
    "CrawlerName": "any-name-for-crawler",
    "RoleName": "role-name-from-cdk-script",
    "SourceData": "s3://crawler-public-us-east-1/flight/2016/csv",
    "DbName": "example-flights-db",
    "Cron": "cron(15 12 * * ? *)",
    "ScriptURL": s3://bucket-name-from-cdk-script/flight_etl_job_script.py
    "JobName": "glue-mvp-job"


<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.end-->


<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [SDK for .NET AWS Glue reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glue/NGlue.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0