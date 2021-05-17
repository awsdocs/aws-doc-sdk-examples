# Amazon DynamoDB JavaScript SDK v3 code examples
Amazon DynamoDB is a key-value and document database that delivers single-digit millisecond performance at any scale. It's a fully managed, multiregion, multimaster, durable database with built-in security, backup and restore, and in-memory caching for internet-scale applications. 

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon DynamoDB examples: 

- [Get batch items](src/ddb_batchgetitem.js)
- [Write batch items](src/ddb_batchwriteitem.js)
- [Create a DyanamoDB table](src/ddb_createtable.js)
- [Delete an item](src/ddb_deleteitem.js)
- [Delete a DyanamoDB table](src/ddb_deletetable.js)
- [Describe DyanamoDB tables](src/ddb_sescribetable.js)
- [Get items](src/ddb_getitem.js)
- [List DyanamoDB tables](src/ddb_listtables.js)
- [Put items](src/ddb_putitem.js)
- [Query a DyanamoDB table](src/ddb_query.js)
- [Scan a DyanamoDB table](src/ddb_scan.js)
- [Delete items using Document Client](src/ddbdoc_delete_item.js)
- [Get items using Document Client](src/ddbdoc_get_item.js)
- [Put items using Document Client](src/ddbdoc_put_item.js)
- [Query a DyanamoDB table using Document Client](src/ddbdoc_query_item.js)
- [Update a DyanamoDB table using Document Client](src/ddbdoc_update.js)

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

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon DynamoDB client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-dynamodb/index.html) 

