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


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](getting_started_with_db_clusters.cpp)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](getting_started_with_db_clusters.cpp#L497)
- [CreateDBClusterParameterGroup](getting_started_with_db_clusters.cpp#L333)
- [CreateDBClusterSnapshot](getting_started_with_db_clusters.cpp#L661)
- [CreateDBInstance](getting_started_with_db_clusters.cpp#L588)
- [DeleteDBCluster](getting_started_with_db_clusters.cpp#L1047)
- [DeleteDBClusterParameterGroup](getting_started_with_db_clusters.cpp#L1117)
- [DeleteDBInstance](getting_started_with_db_clusters.cpp#L1017)
- [DescribeDBClusterParameterGroups](getting_started_with_db_clusters.cpp#L276)
- [DescribeDBClusterParameters](getting_started_with_db_clusters.cpp#L786)
- [DescribeDBClusterSnapshots](getting_started_with_db_clusters.cpp#L701)
- [DescribeDBClusters](getting_started_with_db_clusters.cpp#L746)
- [DescribeDBEngineVersions](getting_started_with_db_clusters.cpp#L845)
- [DescribeDBInstances](getting_started_with_db_clusters.cpp#L896)
- [DescribeOrderableDBInstanceOptions](getting_started_with_db_clusters.cpp#L936)
- [ModifyDBClusterParameterGroup](getting_started_with_db_clusters.cpp#L402)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

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


#### Create an Aurora Serverless work item tracker

This example shows you how to create a web application that tracks work items in an Amazon Aurora Serverless database and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_RDSDataTracker.start-->
<!--custom.scenario_prereqs.cross_RDSDataTracker.end-->


<!--custom.scenarios.cross_RDSDataTracker.start-->
<!--custom.scenarios.cross_RDSDataTracker.end-->

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