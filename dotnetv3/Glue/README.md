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