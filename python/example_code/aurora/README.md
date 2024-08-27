# Aurora code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Aurora.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Aurora](hello/hello_aurora.py#L4) (`DescribeDBClusters`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_get_started_aurora.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](aurora_wrapper.py#L220)
- [CreateDBClusterParameterGroup](aurora_wrapper.py#L68)
- [CreateDBClusterSnapshot](aurora_wrapper.py#L288)
- [CreateDBInstance](aurora_wrapper.py#L341)
- [DeleteDBCluster](aurora_wrapper.py#L270)
- [DeleteDBClusterParameterGroup](aurora_wrapper.py#L101)
- [DeleteDBInstance](aurora_wrapper.py#L469)
- [DescribeDBClusterParameterGroups](aurora_wrapper.py#L39)
- [DescribeDBClusterParameters](aurora_wrapper.py#L126)
- [DescribeDBClusterSnapshots](aurora_wrapper.py#L315)
- [DescribeDBClusters](aurora_wrapper.py#L191)
- [DescribeDBEngineVersions](aurora_wrapper.py#L378)
- [DescribeDBInstances](aurora_wrapper.py#L440)
- [DescribeOrderableDBInstanceOptions](aurora_wrapper.py#L409)
- [ModifyDBClusterParameterGroup](aurora_wrapper.py#L164)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a lending library REST API](../../cross_service/aurora_rest_lending_library)
- [Create an Aurora Serverless work item tracker](../../cross_service/aurora_item_tracker)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Aurora

This example shows you how to get started using Aurora.

```
python hello/hello_aurora.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a custom Aurora DB cluster parameter group and set parameter values.
- Create a DB cluster that uses the parameter group.
- Create a DB instance that contains a database.
- Take a snapshot of the DB cluster, then clean up resources.

<!--custom.basic_prereqs.aurora_Scenario_GetStartedClusters.start-->
<!--custom.basic_prereqs.aurora_Scenario_GetStartedClusters.end-->

Start the example by running the following at a command prompt:

```
python scenario_get_started_aurora.py
```


<!--custom.basics.aurora_Scenario_GetStartedClusters.start-->
<!--custom.basics.aurora_Scenario_GetStartedClusters.end-->


#### Create a lending library REST API

This example shows you how to create a lending library where patrons can borrow and return books by using a REST API backed by an Amazon Aurora database.


<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.end-->


<!--custom.scenarios.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenarios.cross_AuroraRestLendingLibrary.end-->

#### Create an Aurora Serverless work item tracker

This example shows you how to create a web application that tracks work items in an Amazon Aurora Serverless database and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_RDSDataTracker.start-->
<!--custom.scenario_prereqs.cross_RDSDataTracker.end-->


<!--custom.scenarios.cross_RDSDataTracker.start-->
<!--custom.scenarios.cross_RDSDataTracker.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Aurora API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Python Aurora reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0