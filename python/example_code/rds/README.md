# Amazon RDS code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to create and manage Amazon Relational
Database Service (Amazon RDS) DB instances and custom parameter groups.

*Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier 
to set up, operate, and scale a relational database in the cloud.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a DB parameter group](instance_wrapper.py)
(`CreateDbParameterGroup`)
* [Create a snapshot of a DB instance](instance_wrapper.py)
(`CreateDbSnapshot`)
* [Create a DB instance](instance_wrapper.py)
(`CreateDBInstance`)
* [Delete a DB instance](instance_wrapper.py)
(`DeleteDBInstance`)
* [Delete a DB parameter group](instance_wrapper.py)
(`DeleteDbParameterGroup`)
* [Describe DB instances](instance_wrapper.py)
(`DescribeDBInstances`)
* [Describe DB parameter groups](instance_wrapper.py)
(`DescribeDbParameterGroups`)
* [Describe database engine versions](instance_wrapper.py)
(`DescribeDbEngineVersions`)
* [Describe options for DB instances](instance_wrapper.py)
(`DescribeOrderableDbInstanceOptions`)
* [Describe parameters in a DB parameter group](instance_wrapper.py)
(`DescribeDbParameters`)
* [Describe snapshots of DB instances](instance_wrapper.py)
(`DescribeDbSnapshots`)
* [Update parameters in a DB parameter group](instance_wrapper.py)
(`ModifyDbParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with DB instances](scenario_get_started_instances.py)

## Run the examples

### Prerequisites

To find prerequisites for running these examples, see the
[README](../../README.md#Prerequisites) in the Python folder.

## Tests

⚠ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../README.md#Tests)
in the Python folder.

## Additional resources
* [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
* [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
* [AWS SDK for Python Amazon RDS Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
