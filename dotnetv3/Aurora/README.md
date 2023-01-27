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

* [Create a DB instance in a DB cluster](Actions/AuroraWrapper.cs)(`CreateDBInstanceAsync`)
* [Create a DB cluster](Actions/AuroraWrapper.cs)(`CreateDBClusterAsync`)
* [Create a DB cluster snapshot](Actions/AuroraWrapper.cs)(`CreateDBClusterSnapshotAsync`)
* [Create a DB cluster parameter group](Actions/AuroraWrapper.cs)(`CreateDBClusterParameterGroupAsync`)
* [Delete a DB cluster](Actions/AuroraWrapper.cs)(`DeleteDBClusterAsync`)
* [Delete a DB cluster parameter group](Actions/AuroraWrapper.cs)(`DeleteDBClusterParameterGroupAsync`)
* [Delete a DB instance](Actions/AuroraWrapper.cs)(`DeleteDBInstanceAsync`)
* [Describe DB instances](Actions/AuroraWrapper.cs)(`DescribeDBInstancesAsync`)
* [Describe DB cluster parameter groups](Actions/AuroraWrapper.cs)(`DescribeDBClusterParameterGroupsAsync`)
* [Describe DB cluster snapshots](Actions/AuroraWrapper.cs)(`DescribeDBClusterSnapshotsAsync`)
* [Describe DB clusters](Actions/AuroraWrapper.cs)(`DescribeDBClustersAsync`)
* [Describe database engine versions](Actions/AuroraWrapper.cs)(`DescribeDBEngineVersionsAsync`)
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

### Get started with billing, alarms, and metrics

This interactive scenario runs at a command prompt and shows you how to use
Aurora with the AWS SDK for .NET to do the following:

1.  Return a list of the available DB engine families for Aurora MySql using the DescribeDBEngineVersionsAsync method.
2.  Select an engine family and create a custom DB cluster parameter group using the CreateDBClusterParameterGroupAsync method.
3.  Get the parameter group using the DescribeDBClusterParameterGroupsAsync method.
4.  Get some parameters in the group using the DescribeDBClusterParametersAsync method.
5.  Parse and display some parameters in the group.
6.  Modify the auto_increment_offset and auto_increment_increment parameters
    using the ModifyDBClusterParameterGroupAsync method.
7.  Get and display the updated parameters using the DescribeDBClusterParametersAsync method with a source of "user".
8.  Get a list of allowed engine versions using the DescribeDBEngineVersionsAsync method.
9.  Create an Aurora DB cluster that contains a MySql database and uses the parameter group 
    using the CreateDBClusterAsync method.
10. Wait for the DB cluster to be ready using the DescribeDBClustersAsync method.
11. Display and select from a list of instance classes available for the selected engine and version
    using the paginated DescribeOrderableDBInstanceOptions method.
12. Create a database instance in the cluster using the CreateDBInstanceAsync method.
13. Wait for the DB instance to be ready using the DescribeDBInstances method.
14. Display the connection endpoint string for the new DB cluster.
15. Create a snapshot of the DB cluster using the CreateDBClusterSnapshotAsync method.
16. Wait for DB snapshot to be ready using the DescribeDBClusterSnapshotsAsync method.
17. Delete the DB instance using the DeleteDBInstanceAsync method.
18. Wait for DB instance to be deleted using DescribeDBInstancesAsync methods.
19. Delete the DB cluster using the DeleteDBClusterAsync method.
20. Wait for DB cluster to be deleted using the DescribeDBClustersAsync methods.
21. Delete the cluster parameter group using the DeleteDBClusterParameterGroupAsync.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [AWS Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
* [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
* [Amazon RDS API for .NET API reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/RDS/NRDS.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
