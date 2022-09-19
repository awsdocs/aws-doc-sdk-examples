# Aurora code examples for the SDK for Python
## Overview

Shows how to use the AWS SDK for Python (Boto3) to create and manage Amazon Aurora
clusters, instances, and custom parameter groups.

*Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier 
to set up, operate, and scale a relational database in the cloud. Aurora is a fully 
managed relational database engine that's compatible with MySQL and PostgreSQL.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a DB instance in a cluster](aurora_wrapper.py)
(`CreateDbInstance`)
* [Create a cluster](aurora_wrapper.py)
(`CreateDbCluster`)
* [Create a cluster snapshot](aurora_wrapper.py)
(`CreateDbClusterSnapshot`)
* [Create a cluster parameter group](aurora_wrapper.py)
(`CreateDbClusterParameterGroup`)
* [Delete a cluster](aurora_wrapper.py)
(`DeleteDbCluster`)
* [Delete a cluster parameter group](aurora_wrapper.py)
(`DeleteDbClusterParameterGroup`)
* [Delete a DB instance](aurora_wrapper.py)
(`DeleteDbInstance`)
* [Describe DB instances](aurora_wrapper.py)
(`DescribeDbInstances`)
* [Describe cluster parameter groups](aurora_wrapper.py)
(`DescribeDbClusterParameterGroups`)
* [Describe cluster snapshots](aurora_wrapper.py)
(`DescribeDbClusterSnapshots`)
* [Describe clusters](aurora_wrapper.py)
(`DescribeDbClusters`)
* [Describe database engine versions](aurora_wrapper.py)
(`DescribeDbEngineVersions`)
* [Describe options for DB instances](aurora_wrapper.py)
(`DescribeOrderableDbInstanceOptions`)
* [Get parameters from a cluster parameter group](aurora_wrapper.py)
(`GetDbClusterParameters`)
* [Update parameters in a cluster parameter group](aurora_wrapper.py)
(`ModifyDbClusterParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with clusters](scenario_get_started_aurora.py)

## Run the examples

### Prerequisites

To find prerequisites for running these examples, see the
[README](../../README.md#Prerequisites) in the Python folder.

## Tests

⚠ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../README.md#Tests)
in the Python folder.

## Additional resources

* [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
* [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
* [AWS SDK for Python Amazon RDS Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
