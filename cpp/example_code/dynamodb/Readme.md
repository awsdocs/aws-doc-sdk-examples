# Amazon DynamoDb code examples for the SDK for C++
## Overview
Sample code which demonstrates creation, deletion, modification, and querying of Amazon DynamoDB databases.

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and
predictable performance with seamless scalability.*
## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
## Code examples
### Single actions
Code excerpts that show you how to call individual service functions.
* [Get items in a batch](./batch_get_item.cpp) (BatchGetItem)
* [Create a table](./create_table.cpp) (CreateTable)
* [Create a table with composite key](./create_table_composite_key.cpp) (CreateTable)
* [Delete an item](./delete_item.cpp) (DeleteItem)
* [Delete a table](./delete_table.cpp) (DeleteTable)
* [Describe a table](./describe_table.cpp) (DescribeTable)
* [Get an item](./get_item.cpp) (GetItem)
* [List tables](./list_tables.cpp) (ListTables)
* [Put an item](./put_item.cpp) (PutItem)
* [Query items](./query_items.cpp) (Query)
* [Scan a table](./scan_table.cpp) (Scan)
* [Update an item](./update_item.cpp) (UpdateItem)
* [Update a table](./update_table.cpp) (UpdateTable)
### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Getting started scenario for DynamoDB](./dynamodb_getting_started_scenario.cpp) (CreateTable, PutItem, UpdateItem, BatchWriteItem, GetItem, Query, Scan, DeleteItem, DeleteTable)
## Run the examples

### Prerequisites
Before using the code examples, first complete the installation and setup steps
of [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

## Tests
⚠️ Running the tests might result in charges to your AWS account.


```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
 ```   


## Additional resources
* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
* [Amazon DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
