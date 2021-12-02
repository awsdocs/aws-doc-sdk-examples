# Amazon DynamoDB C++ code examples

This README discusses how to run the C++ code examples for Amazon DynamoDB.

## Running the Amazon DynamoDB C++ files

**IMPORTANT**

The C++ code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateTable** - Demonstrates how to create an Amazon DynamoDB table.
- **DeleteItem** - Demonstrates how to delete an item from an Amazon DynamoDB table.
- **DeleteTable** - Demonstrates how to delete an Amazon DynamoDB table.
- **DescribeTable** - Demonstrates how to retrieve information about an Amazon DynamoDB table.
- **DynamoDBScanItems** - Demonstrates how to return items from an Amazon DynamoDB table.
- **DynamoDBScanItemsFilter** - Demonstrates how to return items from an Amazon DynamoDB table using a filter expression.
- **GetItem** - Demonstrates how to retrieve an item from an Amazon DynamoDB table.
- **ListTables** - Demonstrates how to list all Amazon DynamoDB tables.
- **PutItem** - Demonstrates how to place an item into an Amazon DynamoDB table.
- **QueryTable** - Demonstrates how to query an Amazon DynamoDB table.
- **UpdateItem** - Demonstrates how to update a value located in an Amazon DynamoDB table.

To run these examples, you can setup your development environment. For more information, 
see [Getting started using the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html). 
