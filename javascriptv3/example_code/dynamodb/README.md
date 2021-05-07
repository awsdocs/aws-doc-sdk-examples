# Amazon DynamoDB examples
Amazon DynamoDB is a key-value and document database that delivers single-digit millisecond performance at any scale. It's a fully managed, multiregion, multimaster, durable database with built-in security, backup and restore, and in-memory caching for internet-scale applications. 

This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon DynamoDB examples: 

- [Get batch items](src/ddb_batchgetitem.ts)
- [Write batch items](src/ddb_batchwriteitem.ts)
- [Create a DyanamoDB table](src/ddb_createtable.ts)
- [Delete an item](src/ddb_deleteitem.ts)
- [Delete a DyanamoDB table](src/ddb_deletetable.ts)
- [Describe DyanamoDB tables](src/ddb_sescribetable.ts)
- [Get items](src/ddb_getitem.ts)
- [List DyanamoDB tables](src/ddb_listtables.ts)
- [Put items](src/ddb_putitem.ts)
- [Query a DyanamoDB table](src/ddb_query.ts)
- [Scan a DyanamoDB table](src/ddb_scan.ts)
- [Delete items using Document Client](src/ddbdoc_delete_item.ts)
- [Get items using Document Client](src/ddbdoc_get_item.ts)
- [Put items using Document Client](src/ddbdoc_put_item.ts)
- [Query a DyanamoDB table using Document Client](src/ddbdoc_query_item.ts)
- [Update a DyanamoDB table using Document Client](src/ddbdoc_update.ts)

**NOTE:** The AWS SDK for JavaScript v3 is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript extends of JavaScript so these examples can also be run as JavaScript. For more information, see [TypeScript homepage](https://www.typescriptlang.org/).

# Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which are *@aws-sdk/client-dynamodb*, *@aws-sdk/lib-dynamodb*.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/dynamodb
npm install
```

3. If you're using JavaScript, change the sample file extension from ```.ts``` to ```.js```.


4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
ts-node [example name].ts // e.g., ts-node ddb_batchgetitem.ts
```

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon DynamoDB client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-dynamodb/index.html) 

