# Amazon RDS code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon Relational Database Service (Amazon RDS).

Amazon RDS is a web service that makes it easier to set up, operate, and scale a relational database in the cloud.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Create a DB parameter group](Actions/RDSWrapper.ParameterGroups.cs)(`CreateDbParameterGroupAsync`)
* [Create a snapshot of a DB instance](Actions/RDSWrapper.Snaspshots.cs)(`CreateDbSnapshotAsync`)
* [Create a DB instance](Actions/RDSWrapper.Instances.cs)(`CreateDBInstanceAsync`)
* [Delete a DB instance](Actions/RDSWrapper.Instances.cs)(`DeleteDBInstanceAsync`)
* [Delete a DB parameter group](Actions/RDSWrapper.ParameterGroups.cs)(`DeleteDbParameterGroupAsync`)
* [Describe DB instances](Actions/RDSWrapper.Instances.cs)(`DescribeDBInstancesAsync`)
* [Describe DB parameter groups](Actions/RDSWrapper.ParameterGroups.cs)(`DescribeDbParameterGroupsAsync`)
* [Describe database engine versions](Actions/RDSWrapper.Instances.cs)(`DescribeDbEngineVersionsAsync`)
* [Describe options for DB instances](Actions/RDSWrapper.Instances.cs)(`DescribeOrderableDbInstanceOptionsAsync`)
* [Describe parameters in a DB parameter group](Actions/RDSWrapper.ParameterGroups.cs)(`DescribeDbParametersAsync`)
* [Describe snapshots of DB instances](Actions/RDSWrapper.Snaspshots.cs)(`DescribeDbSnapshotsAsync`)
* [Update parameters in a DB parameter group](Actions/RDSWrapper.ParameterGroups.cs)(`ModifyDbParameterGroupAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with DB instances](Scenarios/RDSInstanceScenario/RDSInstanceScenario.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
[README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

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
* [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
* [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon RDS](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/RDS/NRDS.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

