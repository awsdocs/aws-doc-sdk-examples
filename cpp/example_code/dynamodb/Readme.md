# Amazon DynamoDB C++ code examples

This README discusses how to run the C++ code examples for Amazon DynamoDB.

## Running the Amazon DynamoDB C++ files

**IMPORTANT**

The C++ code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **batch_get_item.cpp** - Get batch items from different Amazon DynamoDB tables.
- **create_table.cpp** - Create an Amazon DynamoDB table.
- **create_table_composite_key.cpp** - Create an Amazon DynamoDB table that has a composite key.
- **delete_item.cpp** - Delete an item from an Amazon DynamoDB table.
- **delete_table.cpp** - Delete an Amazon DynamoDB table.
- **describe_table.cpp** - Retrieve information about an Amazon DynamoDB table.
- **get_item.cpp** - Retrieve an item from an Amazon DynamoDB table.
- **list_tables.cpp** - List all Amazon DynamoDB tables.
- **put_item.cpp** - Place an item into an Amazon DynamoDB table.
- **query_items.cpp** - Query an Amazon DynamoDB table.
- **scan_table.cpp** - Scan an Amazon DynamoDB table.
- **update_item.cpp** - Update an item in an Amazon DynamoDB table.
- **update_table.cpp** - Update information about an Amazon DynamoDB table.

To run these examples, you can setup your development environment. For more information, 
see [Getting started using the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html). 
