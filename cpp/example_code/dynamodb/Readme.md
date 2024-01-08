# DynamoDB code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon DynamoDB.

<!--custom.overview.start-->
<!--custom.overview.end-->

_DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability._

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

- [Hello DynamoDB](hello_dynamodb/CMakeLists.txt#L4) (`ListTables`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a table](create_table.cpp#L29) (`CreateTable`)
- [Delete a table](delete_table.cpp#L25) (`DeleteTable`)
- [Delete an item from a table](delete_item.cpp#L24) (`DeleteItem`)
- [Get a batch of items](batch_get_item.cpp#L32) (`BatchGetItem`)
- [Get an item from a table](get_item.cpp#L26) (`GetItem`)
- [Get information about a table](describe_table.cpp#L25) (`DescribeTable`)
- [List tables](list_tables.cpp#L27) (`ListTables`)
- [Put an item in a table](put_item.cpp#L27) (`PutItem`)
- [Query a table](query_items.cpp#L24) (`Query`)
- [Run a PartiQL statement](dynamodb_partiql_single_scenario.cpp#L49) (`ExecuteStatement`)
- [Run batches of PartiQL statements](dynamodb_partiql_batch_scenario.cpp#L50) (`BatchExecuteStatement`)
- [Scan a table](scan_table.cpp#L24) (`Scan`)
- [Update an item in a table](update_item.cpp#L26) (`UpdateItem`)
- [Write a batch of items](batch_write_item.cpp#L51) (`BatchWriteItem`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with tables, items, and queries](dynamodb_utils.cpp)
- [Query a table by using batches of PartiQL statements](dynamodb_utils.cpp)
- [Query a table using PartiQL](dynamodb_utils.cpp)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a serverless application to manage photos](../../example_code/cross-service/photo_asset_manager)


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

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.



#### Get started with tables, items, and queries

This example shows you how to do the following:

- Create a table that can hold movie data.
- Put, get, and update a single movie in the table.
- Write movie data to the table from a sample JSON file.
- Query for movies that were released in a given year.
- Scan for movies that were released in a range of years.
- Delete a movie from the table, then delete the table.

<!--custom.scenario_prereqs.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_GettingStartedMovies.end-->


<!--custom.scenarios.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.scenarios.dynamodb_Scenario_GettingStartedMovies.end-->

#### Query a table by using batches of PartiQL statements

This example shows you how to do the following:

- Get a batch of items by running multiple SELECT statements.
- Add a batch of items by running multiple INSERT statements.
- Update a batch of items by running multiple UPDATE statements.
- Delete a batch of items by running multiple DELETE statements.

<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLBatch.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLBatch.end-->


<!--custom.scenarios.dynamodb_Scenario_PartiQLBatch.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLBatch.end-->

#### Query a table using PartiQL

This example shows you how to do the following:

- Get an item by running a SELECT statement.
- Add an item by running an INSERT statement.
- Update an item by running an UPDATE statement.
- Delete an item by running a DELETE statement.

<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLSingle.end-->


<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.end-->

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

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for C++ DynamoDB reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-dynamodb/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0