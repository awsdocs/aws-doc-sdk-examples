# Aurora code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Aurora.

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



Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Aurora](hello_aurora/CMakeLists.txt#L4) (`DescribeDBClusters`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a DB cluster](getting_started_with_db_clusters.cpp#L497) (`CreateDBCluster`)
- [Create a DB cluster parameter group](getting_started_with_db_clusters.cpp#L333) (`CreateDBClusterParameterGroup`)
- [Create a DB cluster snapshot](getting_started_with_db_clusters.cpp#L661) (`CreateDBClusterSnapshot`)
- [Create a DB instance in a DB cluster](getting_started_with_db_clusters.cpp#L588) (`CreateDBInstance`)
- [Delete a DB cluster](getting_started_with_db_clusters.cpp#L1033) (`DeleteDBCluster`)
- [Delete a DB cluster parameter group](getting_started_with_db_clusters.cpp#L1103) (`DeleteDBClusterParameterGroup`)
- [Delete a DB instance](getting_started_with_db_clusters.cpp#L1003) (`DeleteDBInstance`)
- [Describe DB cluster parameter groups](getting_started_with_db_clusters.cpp#L295) (`DescribeDBClusterParameterGroups`)
- [Describe DB cluster snapshots](getting_started_with_db_clusters.cpp#L701) (`DescribeDBClusterSnapshots`)
- [Describe DB clusters](getting_started_with_db_clusters.cpp#L746) (`DescribeDBClusters`)
- [Describe DB instances](getting_started_with_db_clusters.cpp#L883) (`DescribeDBInstances`)
- [Describe database engine versions](getting_started_with_db_clusters.cpp#L845) (`DescribeDBEngineVersions`)
- [Describe options for DB instances](getting_started_with_db_clusters.cpp#L923) (`DescribeOrderableDBInstanceOptions`)
- [Describe parameters from a DB cluster parameter group](getting_started_with_db_clusters.cpp#L786) (`DescribeDBClusterParameters`)
- [Update parameters in a DB cluster parameter group](getting_started_with_db_clusters.cpp#L402) (`ModifyDBClusterParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with DB clusters](getting_started_with_db_clusters.cpp)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create an Aurora Serverless work item tracker](../../example_code/cross-service/serverless-aurora)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file in this folder. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

If the source file is in a different folder, instructions can be found in the README in that
folder.

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



```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest
```


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Aurora API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for C++ Aurora reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-rds/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0