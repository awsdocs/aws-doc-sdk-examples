# Amazon DynamoDb code examples for the SDK for C++ using the DynamoDBClient
## Overview
Shows how to use the AWS SDK for C++ to create Amazon DynamoDB
tables and move data in and out of them.

* Create a table for storing movies.
* Load movies into the table from a JSON-formatted file.
* Update and query movies in the table.
* Get, write, and delete items in batches.

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and
predictable performance with seamless scalability.*
## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
## Code examples
### Single actions
Code excerpts that show you how to call individual service functions.
* [batch_get_item](./batch_get_item.cpp) (BatchGetItem)
* [create_table](./create_table.cpp) (CreateTable)
* [create_table_composite_key](./create_table_composite_key.cpp) (CreateTable)
* [delete_item](./delete_item.cpp) (DeleteItem)
* [delete_table](./delete_table.cpp) (DeleteTable)
* [describe_table](./describe_table.cpp) (DescribeTable)
* [get_item](./get_item.cpp) (GetItem)
* [list_tables](./list_tables.cpp) (ListTables)
* [put_item](./put_item.cpp) (PutItem)
* [query_items](./query_items.cpp) (Query)
* [scan_table](./scan_table.cpp) (Scan)
* [update_item](./update_item.cpp) (UpdateItem)
* [update_table](./update_table.cpp) (UpdateTable)
### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [dynamodb_getting_started_scenario](./dynamodb_getting_started_scenario.cpp) (CreateTable, PutItem, UpdateItem, BatchWriteItem, GetItem, Query, Scan, DeleteItem, DeleteTable)
## Run the examples

### Prerequisites
Before using the code examples, first complete the installation and setup steps
of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code examples structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
   ./gtests/dynamodb_gtest 
```   

## Additional resources
* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
* [Amazon DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
