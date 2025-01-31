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


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/aurora_scenario/mod.rs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](src/rds.rs#L151)
- [CreateDBClusterParameterGroup](src/rds.rs#L88)
- [CreateDBClusterSnapshot](src/rds.rs#L206)
- [CreateDBInstance](src/rds.rs#L174)
- [DeleteDBCluster](src/aurora_scenario/tests.rs#L790)
- [DeleteDBClusterParameterGroup](src/aurora_scenario/tests.rs#L790)
- [DeleteDBInstance](src/aurora_scenario/tests.rs#L790)
- [DescribeDBClusterParameters](src/aurora_scenario/tests.rs#L376)
- [DescribeDBClusters](src/rds.rs#L106)
- [DescribeDBEngineVersions](src/rds.rs#L56)
- [DescribeDBInstances](src/aurora_scenario/tests.rs#L790)
- [DescribeOrderableDBInstanceOptions](src/rds.rs#L69)
- [ModifyDBClusterParameterGroup](src/rds.rs#L135)


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