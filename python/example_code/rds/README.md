# Amazon RDS code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Relational Database Service (Amazon RDS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon RDS](hello/hello_rds.py#L4) (`DescribeDBInstances`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_get_started_instances.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBInstance](instance_wrapper.py#L333)
- [CreateDBParameterGroup](instance_wrapper.py#L69)
- [CreateDBSnapshot](instance_wrapper.py#L189)
- [DeleteDBInstance](instance_wrapper.py#L389)
- [DeleteDBParameterGroup](instance_wrapper.py#L102)
- [DescribeDBEngineVersions](instance_wrapper.py#L242)
- [DescribeDBInstances](instance_wrapper.py#L304)
- [DescribeDBParameterGroups](instance_wrapper.py#L40)
- [DescribeDBParameters](instance_wrapper.py#L125)
- [DescribeDBSnapshots](instance_wrapper.py#L216)
- [DescribeOrderableDBInstanceOptions](instance_wrapper.py#L273)
- [ModifyDBParameterGroup](instance_wrapper.py#L163)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create an Aurora Serverless work item tracker](../../cross_service/aurora_item_tracker)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon RDS

This example shows you how to get started using Amazon RDS.

```
python hello/hello_rds.py
```

#### Learn the basics

This example shows you how to do the following:

- Create a custom DB parameter group and set parameter values.
- Create a DB instance that's configured to use the parameter group. The DB instance also contains a database.
- Take a snapshot of the instance.
- Delete the instance and parameter group.

<!--custom.basic_prereqs.rds_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.rds_Scenario_GetStartedInstances.end-->

Start the example by running the following at a command prompt:

```
python scenario_get_started_instances.py
```


<!--custom.basics.rds_Scenario_GetStartedInstances.start-->
<!--custom.basics.rds_Scenario_GetStartedInstances.end-->


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

- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for Python Amazon RDS reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0