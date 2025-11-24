# Amazon Redshift code examples for the SDK for .NET Framework 4.x

## Overview

This folder contains code examples that demonstrate how to use the AWS SDK for .NET Framework 4.x to interact with Amazon Redshift.

Amazon Redshift is a fast, fully managed, petabyte-scale data warehouse service that makes it simple and cost-effective to efficiently analyze all your data using your existing business intelligence tools.

## ⚠ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Actions

Code excerpts that show you how to call individual service functions:

- [CreateCluster](Actions/RedshiftWrapper.cs#L28) (`CreateCluster`)
- [DeleteCluster](Actions/RedshiftWrapper.cs#L118) (`DeleteCluster`)
- [DescribeClusters](Actions/RedshiftWrapper.cs#L69) (`DescribeClusters`)
- [ModifyCluster](Actions/RedshiftWrapper.cs#L96) (`ModifyCluster`)
- [CreateTable](Actions/RedshiftWrapper.cs#L157) (`ExecuteStatement`)
- [InsertMovie](Actions/RedshiftWrapper.cs#L193) (`ExecuteStatement`)
- [QueryMoviesByYear](Actions/RedshiftWrapper.cs#L227) (`ExecuteStatement`, `GetStatementResult`)
- [DescribeStatement](Actions/RedshiftWrapper.cs#L269) (`DescribeStatement`)
- [GetStatementResult](Actions/RedshiftWrapper.cs#L289) (`GetStatementResult`)
- [ListDatabases](Actions/RedshiftWrapper.cs#L130) (`ListDatabases`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service:

- [Get started with Redshift clusters](Scenarios/RedshiftBasics.cs) - Learn the basics of Amazon Redshift by creating a cluster, adding a table, inserting data, and querying the table.

### Hello

- [Hello Amazon Redshift](Hello/HelloRedshift.cs) - A simple example that shows how to get started with Amazon Redshift by listing existing clusters.

## Run the examples

### Prerequisites

For general prerequisites, see the [README](../../README.md) in the `dotnetv4` folder.

After the example compiles, you can run it from the command line. To do so, navigate to the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

### Hello Amazon Redshift

This example shows you how to get started using Amazon Redshift.

#### Purpose

Shows how to use the AWS SDK for .NET to get started using Amazon Redshift. Lists existing Redshift clusters in your account.

### Redshift Basics Scenario

This scenario demonstrates how to interact with Amazon Redshift using the AWS SDK for .NET. It demonstrates various tasks such as creating a Redshift cluster, verifying its readiness, creating a table, populating the table with data, executing SQL queries, and finally cleaning up resources.

#### Purpose

Demonstrates how to:

1. Create an Amazon Redshift cluster.
2. Wait for the cluster to become available.
3. List databases in the cluster.
4. Create a "Movies" table.
5. Populate the "Movies" table using sample data.
6. Query the "Movies" table by year.
7. Modify the Redshift cluster.
8. Delete the Amazon Redshift cluster.

#### Usage

1. Clone the repository or download the source code files.
2. Open the code in your preferred .NET IDE.
3. Update the following variables in the `RunScenarioAsync()` method if needed:
   - `userName`: The username for the Redshift cluster.
   - `userPassword`: The password for the Redshift cluster.
   - `databaseName`: The name of the database to use ("dev").
4. Run the `RedshiftBasics` class.

The program will guide you through the scenario, prompting you to enter a cluster ID and the number of records to add to the "Movies" table. The program will also display the progress and results of the various operations.

## Tests

### Unit tests

Unit tests in this solution use MSTest. The tests use Moq to mock AWS service client dependencies.

Run unit tests with this command:

```
dotnet test Tests/RedshiftTests.csproj
```

### Integration tests

⚠️ Running the integration tests might result in charges to your AWS account.

The integration tests in this solution require access to AWS services and will create and delete AWS resources. Make sure you have valid AWS credentials configured before running these tests.

Run integration tests with this command:

```
dotnet test IntegrationTests/RedshiftIntegrationTests.csproj
```

Note: The full workflow integration test can take 10-15 minutes to complete due to cluster creation time.

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon Redshift reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Redshift/NRedshift.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.  
SPDX-License-Identifier: Apache-2.0
