# Aurora code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Aurora
to create and manage Amazon Aurora DB clusters, DB instances, and custom parameter groups.

Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier
to set up, operate, and scale a relational database in the cloud. Aurora is a fully
managed relational database engine that's compatible with MySQL and PostgreSQL.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Aurora](Actions/HelloAurora.cs)

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a DB cluster](Actions/AuroraWrapper.cs)(`CreateDBClusterAsync`)
* [Create a DB cluster parameter group](Actions/AuroraWrapper.cs)(`CreateDBClusterParameterGroupAsync`)
* [Create a DB cluster snapshot](Actions/AuroraWrapper.cs)(`CreateDBClusterSnapshotAsync`)
* [Create a DB instance in a DB cluster](Actions/AuroraWrapper.cs)(`CreateDBInstanceAsync`)
* [Delete a DB cluster](Actions/AuroraWrapper.cs)(`DeleteDBClusterAsync`)
* [Delete a DB cluster parameter group](Actions/AuroraWrapper.cs)(`DeleteDBClusterParameterGroupAsync`)
* [Delete a DB instance](Actions/AuroraWrapper.cs)(`DeleteDBInstanceAsync`)
* [Describe database engine versions](Actions/AuroraWrapper.cs)(`DescribeDBEngineVersionsAsync`)
* [Describe DB cluster parameter groups](Actions/AuroraWrapper.cs)(`DescribeDBClusterParameterGroupsAsync`)
* [Describe DB cluster snapshots](Actions/AuroraWrapper.cs)(`DescribeDBClusterSnapshotsAsync`)
* [Describe DB clusters](Actions/AuroraWrapper.cs)(`DescribeDBClustersAsync`)
* [Describe DB instances](Actions/AuroraWrapper.cs)(`DescribeDBInstancesAsync`)
* [Describe options for DB instances](Actions/AuroraWrapper.cs)(`DescribeOrderableDBInstanceOptionsAsync`)
* [Get parameters from a DB cluster parameter group](Actions/AuroraWrapper.cs)(`GetDBClusterParametersAsync`)
* [Update parameters in a DB cluster parameter group](Actions/AuroraWrapper.cs)(`ModifyDBClusterParameterGroupAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with DB clusters](Scenarios/AuroraScenario.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

### Get started with DB clusters

This interactive scenario runs at a command prompt and shows you how to use
Aurora with the AWS SDK for .NET to do the following:

* Create a custom Aurora DB cluster parameter group and set parameter values.
* Create a DB cluster that is configured to use the parameter group.
* Create a DB instance in the DB cluster that contains a database.
* Take a snapshot of the DB cluster.
* Delete the instance, DB cluster, and parameter group.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests

⚠ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.

## Additional resources
* [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
* [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
* [Amazon RDS API for .NET API reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/RDS/NRDS.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
