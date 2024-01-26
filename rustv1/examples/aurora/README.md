# Aurora code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Aurora.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Aurora](src/bin/hello-world.rs) (`DescribeDBClusters`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a DB cluster](src/aurora_scenario/mod.rs#L352) (`CreateDBCluster`)
- [Create a DB cluster parameter group](src/aurora_scenario/mod.rs#L203) (`CreateDBClusterParameterGroup`)
- [Create a DB cluster snapshot](src/aurora_scenario/mod.rs#L352) (`CreateDBClusterSnapshot`)
- [Create a DB instance in a DB cluster](src/aurora_scenario/mod.rs#L352) (`CreateDBInstance`)
- [Delete a DB cluster](src/aurora_scenario/mod.rs#L512) (`DeleteDBCluster`)
- [Delete a DB cluster parameter group](src/aurora_scenario/mod.rs#L512) (`DeleteDBClusterParameterGroup`)
- [Delete a DB instance](src/aurora_scenario/mod.rs#L512) (`DeleteDBInstance`)
- [Describe DB clusters](src/aurora_scenario/mod.rs#L352) (`DescribeDBClusters`)
- [Describe DB instances](src/aurora_scenario/mod.rs#L512) (`DescribeDBInstances`)
- [Describe database engine versions](src/aurora_scenario/mod.rs#L142) (`DescribeDBEngineVersions`)
- [Describe options for DB instances](src/aurora_scenario/mod.rs#L179) (`DescribeOrderableDBInstanceOptions`)
- [Describe parameters from a DB cluster parameter group](src/aurora_scenario/mod.rs#L286) (`DescribeDBClusterParameters`)
- [Update parameters in a DB cluster parameter group](src/aurora_scenario/mod.rs#L315) (`ModifyDBClusterParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with DB clusters](src/aurora_scenario/mod.rs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Aurora

This example shows you how to get started using Aurora.



#### Get started with DB clusters

This example shows you how to do the following:

- Create a custom Aurora DB cluster parameter group and set parameter values.
- Create a DB cluster that uses the parameter group.
- Create a DB instance that contains a database.
- Take a snapshot of the DB cluster, then clean up resources.

<!--custom.scenario_prereqs.aurora_Scenario_GetStartedClusters.start-->
<!--custom.scenario_prereqs.aurora_Scenario_GetStartedClusters.end-->


<!--custom.scenarios.aurora_Scenario_GetStartedClusters.start-->
<!--custom.scenarios.aurora_Scenario_GetStartedClusters.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Aurora API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Rust Aurora reference](https://docs.rs/aws-sdk-aurora/latest/aws_sdk_aurora/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0