# AWS Glue code examples for the AWS SDK for .NET (v3)

## Overview

The examples in this folder show how to use AWS Glue with the AWS SDK for .NET.

AWS Glue is a serverless data integration service that makes it easy to 
discover, prepare, and combine data for analytics, machine learning, and
application development. AWS Glue provides all the capabilities needed for data
integration so that you can start analyzing your data and putting it to use in
minutes instead of months.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS
  Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single Actions

- [Create a crawler](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`CreateCrawlerAsync`)
- [Create a database](scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`CreateDatabaseAsync`)
- [Create a job definition](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`CreateJobAsync`)
- [Delete a crawler](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`DeleteCrawlerAsync`)
- [Delete a database from the AWS Glue Data Catalog](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`DeleteDatabaseAsync`)
- [Delete a job definition](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`DeleteJobAsync`)
- [Get all jobs](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`GetJobsAsync`)
- [Get tables from a database](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`GetTablesAsync`)
- [Get runs of a job](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`GetJobRunsAsync`)
- [Get a crawler](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`GetCrawlerAsync`)
- [Get a databasse](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`GetDatabaseAsync`)
- [Start a crawler](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`StartCrawlerAsync`)
- [Start a job run](Scenarios/Glue_Basics_Scenario/Glue_Basics/GlueMethods.cs) (`StartJobRunAsync`)

### Scenario

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

- [Glue basics scenario](scenarios/Glue_Basics_Scenario/)

## Running the examples

After the example is compiled, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then issue
the following command:

```
dotnet run
```

Alternatively, you can execute the example from within your IDE.

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- NET Core 6.0 or later
- AWS SDK for .NET 3.7 or later

### Tests

⚠️ Running the tests might result in charges to your AWS account.

The scenario includes a test project in the solution.

1. Naviate to the folder that contains the scenario and then enter:

```
dotnet test
```

Alternatively, you can open the scenario solution and use the Visual Studio
Test Runner to execute the tests.

## Additional Resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/glue-dg.html)
- [AWS Glue API in the AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [AWS Glue section of the AWS SDK for .NET (v3) API Documentation](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glue/NGlue.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0