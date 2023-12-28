# Amazon RDS code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Relational Database Service (Amazon RDS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon RDS is a web service that makes it easier to set up, operate, and scale a relational database in the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

- [Hello Amazon RDS](hello_rds/CMakeLists.txt#L4) (`DescribeDBInstances`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a DB instance](getting_started_with_db_instances.cpp#L483) (`CreateDBInstance`)
- [Create a DB parameter group](getting_started_with_db_instances.cpp#L315) (`CreateDBParameterGroup`)
- [Create a snapshot of a DB instance](getting_started_with_db_instances.cpp#L561) (`CreateDBSnapshot`)
- [Delete a DB instance](getting_started_with_db_instances.cpp#L859) (`DeleteDBInstance`)
- [Delete a DB parameter group](getting_started_with_db_instances.cpp#L913) (`DeleteDBParameterGroup`)
- [Describe DB instances](getting_started_with_db_instances.cpp#L738) (`DescribeDBInstances`)
- [Describe DB parameter groups](getting_started_with_db_instances.cpp#L277) (`DescribeDBParameterGroups`)
- [Describe database engine versions](getting_started_with_db_instances.cpp#L700) (`DescribeDBEngineVersions`)
- [Describe options for DB instances](getting_started_with_db_instances.cpp#L778) (`DescribeOrderableDBInstanceOptions`)
- [Describe parameters in a DB parameter group](getting_started_with_db_instances.cpp#L641) (`DescribeDBParameters`)
- [Describe snapshots of DB instances](getting_started_with_db_instances.cpp#L599) (`DescribeDBSnapshots`)
- [Update parameters in a DB parameter group](getting_started_with_db_instances.cpp#L384) (`ModifyDBParameterGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with DB instances](getting_started_with_db_instances.cpp)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create an Aurora Serverless work item tracker](../../example_code/cross-service/serverless-aurora)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

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



```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest
```


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon RDS API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/Welcome.html)
- [SDK for C++ Amazon RDS reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-rds/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0