# Aurora code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with Amazon Aurora.

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
- [CreateDBClusterSnapshot](Actions/AuroraWrapper.cs#L323)
- [CreateDBInstance](Actions/AuroraWrapper.cs#L289)
- [DeleteDBCluster](Actions/AuroraWrapper.cs#L370)
- [DeleteDBClusterParameterGroup](Actions/AuroraWrapper.cs#L181)
- [DeleteDBInstance](Actions/AuroraWrapper.cs#L389)
- [DescribeDBClusterParameterGroups](Actions/AuroraWrapper.cs#L98)
- [DescribeDBClusterParameters](Actions/AuroraWrapper.cs#L66)
- [DescribeDBClusterSnapshots](Actions/AuroraWrapper.cs#L343)
- [DescribeDBClusters](Actions/AuroraWrapper.cs#L259)
- [DescribeDBEngineVersions](Actions/AuroraWrapper.cs#L21)
- [DescribeDBInstances](Actions/AuroraWrapper.cs#L236)
- [DescribeOrderableDBInstanceOptions](Actions/AuroraWrapper.cs#L154)
- [ModifyDBClusterParameterGroup](Actions/AuroraWrapper.cs#L115)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


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
- [SDK for .NET (v4) Aurora reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Aurora/NAurora.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
