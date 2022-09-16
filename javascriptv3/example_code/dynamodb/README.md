# Amazon DynamoDB JavaScript SDK v3 code examples
The code examples in this directory demonstrate how to work with Amazon DynamoDB using the AWS SDK for JavaScript version 3 (v3).

Amazon DynamoDB is a key-value and document database that delivers single-digit millisecond performance at any scale. It's a fully managed, multiregion, multimaster, durable database with built-in security, backup and restore, and in-memory caching for internet-scale applications. 

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon DynamoDB examples: 

### Scenario examples

- [DynamoDB_basics](scenarios/dynamodb_basics/src/dynamodb_basics.js)

#### Scenario examples using PartiQL

- [PartiQL_basics](scenarios/partiQL_basics/src/partiQL_basics.js)
- [PartiQL_batch_basics](scenarios/partiQL_basics/src/partiQL_batch_basics.js)

### API Examples - DynamoDB

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

#### API Examples - DynamoDB using PartiQL
- [Add an item to a table using PartiQL](src/partiQL_examples/src/partiql_putItem.js) (ExecuteStatementCommand)
- [Add items to a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_putItems.js) (BatchExecuteStatementCommand)
- [Delete an item from a table using PartiQL](src/partiQL_examples/src/partiql_deleteItem.js) (ExecuteStatementCommand)
- [Delete items from a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_deleteItems.js) (BatchExecuteStatementCommand)
- [Get an item from a table using PartiQL](src/partiQL_examples/src/partiql_getItem.js) (ExecuteStatementCommand)
- [Get items from a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_getItems.js) (BatchExecuteStatementCommand)
- [Update an item in a table using PartiQL](src/partiQL_examples/src/partiql_updateItem.js) (ExecuteStatementCommand)
- [Update items in a table by batch using PartiQL](src/partiQL_examples/src/partiql_batch_updateItems.js) (BatchExecuteStatementCommand)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which are *@aws-sdk/client-dynamodb*, *@aws-sdk/lib-dynamodb*.
```
npm install node -g
cd javascriptv3/example_code/dynamodb
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node ddb_batchgetitem.js
```

## Unit tests
For more information see, the [README](../README.rst).

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon DynamoDB client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-dynamodb/index.html) 

