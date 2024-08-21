# Amazon RDS code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with Amazon Relational Database Service (Amazon RDS).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon RDS](hello/hello.go#L4) (`DescribeDBInstances`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/get_started_instances.go)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBInstance](actions/instances.go#L175)
- [CreateDBParameterGroup](actions/instances.go#L49)
- [CreateDBSnapshot](actions/instances.go#L138)
- [DeleteDBInstance](actions/instances.go#L229)
- [DeleteDBParameterGroup](actions/instances.go#L73)
- [DescribeDBEngineVersions](actions/instances.go#L248)
- [DescribeDBInstances](actions/instances.go#L204)
- [DescribeDBParameterGroups](actions/instances.go#L24)
- [DescribeDBParameters](actions/instances.go#L91)
- [DescribeDBSnapshots](actions/instances.go#L157)
- [DescribeOrderableDBInstanceOptions](actions/instances.go#L269)
- [ModifyDBParameterGroup](actions/instances.go#L119)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon RDS

This example shows you how to get started using Amazon RDS.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```
#### Learn the basics

This example shows you how to do the following:

- Create a custom DB parameter group and set parameter values.
- Create a DB instance that's configured to use the parameter group. The DB instance also contains a database.
- Take a snapshot of the instance.
- Delete the instance and parameter group.

<!--custom.basic_prereqs.rds_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.rds_Scenario_GetStartedInstances.end-->


<!--custom.basics.rds_Scenario_GetStartedInstances.start-->
<!--custom.basics.rds_Scenario_GetStartedInstances.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Go V2 Amazon RDS reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/rds)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0