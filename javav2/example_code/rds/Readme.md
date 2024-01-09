# Amazon RDS code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Relational Database Service (Amazon RDS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon RDS is a web service that makes it easier to set up, operate, and scale a relational database in the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon RDS](src/main/java/com/example/rds/DescribeDBInstances.java#L11) (`DescribeDBInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a DB instance](src/main/java/com/example/rds/CreateDBInstance.java#L12) (`CreateDBInstance`)
- [Create a DB parameter group](src/main/java/com/example/rds/RDSScenario.java#L546) (`CreateDBParameterGroup`)
- [Create a snapshot of a DB instance](src/main/java/com/example/rds/RDSScenario.java#L324) (`CreateDBSnapshot`)
- [Create an authentication token](src/main/java/com/example/rds/GenerateRDSAuthToken.java#L20) (`GenerateRDSAuthToken`)
- [Delete a DB instance](src/main/java/com/example/rds/DeleteDBInstance.java#L12) (`DeleteDBInstance`)
- [Delete a DB parameter group](src/main/java/com/example/rds/RDSScenario.java#L223) (`DeleteDBParameterGroup`)
- [Describe DB instances](src/main/java/com/example/rds/DescribeDBInstances.java#L11) (`DescribeDBInstances`)
- [Describe DB parameter groups](src/main/java/com/example/rds/RDSScenario.java#L524) (`DescribeDBParameterGroups`)
- [Describe database engine versions](src/main/java/com/example/rds/RDSScenario.java#L566) (`DescribeDBEngineVersions`)
- [Describe options for DB instances](src/main/java/com/example/rds/RDSScenario.java#L436) (`DescribeOrderableDBInstanceOptions`)
- [Describe parameters in a DB parameter group](src/main/java/com/example/rds/RDSScenario.java#L486) (`DescribeDBParameters`)
- [Modify a DB instance](src/main/java/com/example/rds/ModifyDBInstance.java#L12) (`ModifyDBInstance`)
- [Reboot a DB instance](src/main/java/com/example/rds/RebootDBInstance.java#L12) (`RebootDBInstance`)
- [Retrieve attributes](src/main/java/com/example/rds/DescribeAccountAttributes.java#L12) (`DescribeAccountAttributes`)
- [Update parameters in a DB parameter group](src/main/java/com/example/rds/RDSScenario.java#L459) (`ModifyDBParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with DB instances](src/main/java/com/example/rds/RDSScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon RDS

This example shows you how to get started using Amazon RDS.



#### Get started with DB instances

This example shows you how to do the following:

- Create a custom DB parameter group and set parameter values.
- Create a DB instance that's configured to use the parameter group. The DB instance also contains a database.
- Take a snapshot of the instance.
- Delete the instance and parameter group.

<!--custom.scenario_prereqs.rds_Scenario_GetStartedInstances.start-->
<!--custom.scenario_prereqs.rds_Scenario_GetStartedInstances.end-->


<!--custom.scenarios.rds_Scenario_GetStartedInstances.start-->
<!--custom.scenarios.rds_Scenario_GetStartedInstances.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon RDS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/rds/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0