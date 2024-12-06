# Aurora code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Aurora.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Aurora is a fully managed relational database engine that's built for the cloud and compatible with MySQL and PostgreSQL. Amazon Aurora is part of Amazon Relational Database Service (Amazon RDS)._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Aurora](Actions/HelloAurora.cs#L4) (`DescribeDBClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/AuroraScenario.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](Actions/AuroraWrapper.cs#L199)
- [CreateDBClusterParameterGroup](Actions/AuroraWrapper.cs#L41)
- [CreateDBClusterSnapshot](Actions/AuroraWrapper.cs#L320)
- [CreateDBInstance](Actions/AuroraWrapper.cs#L286)
- [DeleteDBCluster](Actions/AuroraWrapper.cs#L367)
- [DeleteDBClusterParameterGroup](Actions/AuroraWrapper.cs#L181)
- [DeleteDBInstance](Actions/AuroraWrapper.cs#L386)
- [DescribeDBClusterParameterGroups](Actions/AuroraWrapper.cs#L98)
- [DescribeDBClusterParameters](Actions/AuroraWrapper.cs#L66)
- [DescribeDBClusterSnapshots](Actions/AuroraWrapper.cs#L340)
- [DescribeDBClusters](Actions/AuroraWrapper.cs#L259)
- [DescribeDBEngineVersions](Actions/AuroraWrapper.cs#L21)
- [DescribeDBInstances](Actions/AuroraWrapper.cs#L236)
- [DescribeOrderableDBInstanceOptions](Actions/AuroraWrapper.cs#L154)
- [ModifyDBClusterParameterGroup](Actions/AuroraWrapper.cs#L115)


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

#### Hello Aurora

This example shows you how to get started using Aurora.


#### Learn the basics

This example shows you how to do the following:

- Create a custom Aurora DB cluster parameter group and set parameter values.
- Create a DB cluster that uses the parameter group.
- Create a DB instance that contains a database.
- Take a snapshot of the DB cluster, then clean up resources.

<!--custom.basic_prereqs.aurora_Scenario_GetStartedClusters.start-->
<!--custom.basic_prereqs.aurora_Scenario_GetStartedClusters.end-->


<!--custom.basics.aurora_Scenario_GetStartedClusters.start-->
<!--custom.basics.aurora_Scenario_GetStartedClusters.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Aurora API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for .NET Aurora reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/RDS/NRDS.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0