# Amazon DynamoDB code examples for AWS SDK for JavaScript (v3)
The code examples in this directory demonstrate how to work with Amazon DynamoDB using the AWS SDK for JavaScript version 3 (v3).

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability. You can use Amazon DynamoDB to create a database table that can store and retrieve any amount of data, and serve any level of request traffic. Amazon DynamoDB automatically spreads the data and traffic for the table over a sufficient number of servers to handle the request capacity specified by the customer and the amount of data stored, while maintaining consistent and fast performance.
###Table of contents
- [Important information](#-important-information)
- [Code examples](#code-examples)
  - [Single actions](#single-actions)
  - [Scenarios](#scenarios)
- [Run the examples](#run-the-examples)
  - [Prerequisites](#prerequisites)
  - [Steps](#steps)
- [Unit tests](#unit-tests)
- [Additional resources](#-A)

## ⚠️ Important information
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code the least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## The code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Create a DynamoDB table](src/ddb_createtable.js) (CreateTableCommand)
- [Create a DynamoDB table - TV example](src/QueryExample/ddb_createtable_tv.js)
- [Describe DynamoDB tables](src/ddb_describetable.js) (DescribeTableCommand)
- [Delete a DynamoDB table](src/ddb_deletetable.js) (DeleteTableCommand)
- [Delete an item](src/ddb_deleteitem.js) (DeleteItemCommand)
- [Delete items using Document Client](src/ddbdoc_delete_item.js) (DeleteCommand)
- [Get items](src/ddb_getitem.js) (GetItemCommand)
- [Get batch items](src/ddb_batchgetitem.js) (BatchGetItemCommand)
- [Get items using Document Client](src/movies/getItem.js) (GetCommand)
- [List DynamoDB tables](src/ddb_listtables.js) (ListTablesCommand)
- [Put items using Document Client](src/movies/putItem.js) {(PutCommand)
- [Query a DynamoDB table using Document Client](src/movies/queryTable.js) (QueryCommand)
- [Scan a DynamoDB table using Document Client](src/movies/scanTable.js) (ScanCommand)
- [Update a DynamoDB table using Document Client](src/movies/updateItem.js) (UpdateCommand)
- [Write batch items using Document Client](src/movies/writeData.js) (BatchGetWriteCommand)
- [Write batch items - TV example](src/QueryExample/ddb_batchwriteitem_tv.js)
- [Add an item to a table using PartiQL](src/partiQL_examples/src/partiql_putItem.js) (ExecuteStatementCommand)
- [Add items to a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_putItems.js) (BatchExecuteStatementCommand)
- [Delete an item from a table using PartiQL](src/partiQL_examples/src/partiql_deleteItem.js) (ExecuteStatementCommand)
- [Delete items from a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_deleteItems.js) (BatchExecuteStatementCommand)
- [Get an item from a table using PartiQL](src/partiQL_examples/src/partiql_getItem.js) (ExecuteStatementCommand)
- [Get items from a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_getItems.js) (BatchExecuteStatementCommand)
- [Update an item in a table using PartiQL](src/partiQL_examples/src/partiql_updateItem.js) (ExecuteStatementCommand)
- [Update items in a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_updateItems.js) (BatchExecuteStatementCommand)


### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [DynamoDB basics](scenarios/dynamodb_basics/src/dynamodb_basics.js)
- [PartiQL basics](scenarios/partiQL_basics/src/partiQL_basics.js)
- [PartiQL batch_basics](scenarios/partiQL_basics/src/partiQL_batch_basics.js)

## Run the examples

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.md#prerequisites)

### Instructions

1. Install the dependencies.

```
cd javascriptv3/example_code/dynamodb
npm install
```
2. Follow the instructions at the top of the example.

## Tests
For more information, see the [README](../README.md).

## Additional resources
- [AWS SDK for JavaScript (v3)](https://github.com/aws/aws-sdk-js-v3) is available. 
- [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples.html) 
- [AWS SDK for JavaScript (v3) API Reference Guide - Amazon DynamoDB client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-dynamodb/index.html) 

