# AWS Glue code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET to manage AWS Glue resources.

AWS Glue is a serverless data integration service that makes it easy to discover, prepare, and combine data for analytics, machine learning, and application development. AWS Glue provides all the capabilities needed for data integration so that you can start analyzing your data and putting it to use in minutes instead of months.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

  [Hello Glue](Actions/HelloGlue.cs)

### Single actions
Code excerpts that show you how to call individual service functions.

- [Create a crawler](Actions/GlueWrapper.cs) (`CreateCrawlerAsync`)
- [Create a database](Actions/GlueWrapper.cs) (`CreateDatabaseAsync`)
- [Create a job definition](Actions/GlueWrapper.cs) (`CreateJobAsync`)
- [Delete a crawler](Actions/GlueWrapper.cs) (`DeleteCrawlerAsync`)
- [Delete a database from the AWS Glue Data Catalog](Actions/GlueWrapper.cs) (`DeleteDatabaseAsync`)
- [Delete a job definition](Actions/GlueWrapper.cs) (`DeleteJobAsync`)
- [Get all jobs](Actions/GlueWrapper.cs) (`GetJobsAsync`)
- [Get tables from a database](Actions/GlueWrapper.cs) (`GetTablesAsync`)
- [Get runs of a job](Actions/GlueWrapper.cs) (`GetJobRunsAsync`)
- [Get a crawler](Actions/GlueWrapper.cs) (`GetCrawlerAsync`)
- [Get a database](Actions/GlueWrapper.cs) (`GetDatabaseAsync`)
- [Start a crawler](Actions/GlueWrapper.cs) (`StartCrawlerAsync`)
- [Start a job run](Actions/GlueWrapper.cs) (`StartJobRunAsync`)

### Scenario

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

- [Get started running crawlers and jobs](scenarios/Glue_Basics_Scenario/Glue_Basics/GlueBasics.cs)

## Run the examples

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

This scenario requires the following scaffold resources:

* An Amazon S3 bucket that can contain the Python ETL job script and can receive 
output data.
* An AWS Identity and Access Management (IAM) role that can be assumed by AWS Glue. 
The role must grant read-write access to the S3 bucket and standard rights needed by 
AWS Glue.

You can deploy and destroy resources using the AWS Cloud Development Kit
(AWS CDK). To do this, run `cdk deploy` or `cdk destroy` in the
[/resources/cdk/glue_role_bucket](/resources/cdk/glue_role_bucket) folder.

When the CDK script reports the name of the bucket and AWS Identify IAM Role
that was created, open the settings.json file and fill in the BucketName and
RoleName values. You can use whatever you like for the CrawlerName.

Copy the Python script, flight_etl_job_script.py, from
[/aws-doc-sdk-examples/python/example_code/glue/flight_etl_job_script.py](/aws-doc-sdk-examples/python/example_code/glue/flight_etl_job_script.py)
to the new Amazon S3 bucket and Fill in the ScriptURL value using the path to the Python script where you
put it in the newly created bucket.

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example is compiled, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then issue
the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test Runner to run the tests.

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/glue-dg.html)
- [AWS Glue API in the AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [AWS Glue section of the AWS SDK for .NET (v3) API Documentation](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glue/NGlue.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0