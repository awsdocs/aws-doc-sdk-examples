# Amazon DynamoDB C++ code examples

This README discusses how to run the C++ code examples for Amazon DynamoDB.

## Running the Amazon DynamoDB C++ files

**IMPORTANT**

The C++ code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **batch_get_item.cpp** - Demonstrates how to batch get items from different Amazon DynamoDB tables.
- **create_table.cpp** - Demonstrates how to create an Amazon DynamoDB table.
- **create_table_composite_key.cpp** - Demonstrates how to create an Amazon DynamoDB table that has a composite key.
- **delete_item.cpp** - Demonstrates how to delete an item from an Amazon DynamoDB table.
- **delete_table.cpp** - Demonstrates how to delete an Amazon DynamoDB table.
- **describe_table.cpp** - Demonstrates how to retrieve information about an Amazon DynamoDB table.
- **get_item.cpp** - Demonstrates how to retrieve an item from an Amazon DynamoDB table.
- **list_tables.cpp** - Demonstrates how to list all Amazon DynamoDB tables.
- **put_item.cpp** - Demonstrates how to place an item into an Amazon DynamoDB table.
- **query_items.cpp** - Demonstrates how to query an Amazon DynamoDB table.
- **scan_table.cpp** - Demonstrates how to scan an Amazon DynamoDB table.
- **update_item.cpp** - Demonstrates how to update an item in an Amazon DynamoDB table.
- **update_table.cpp** - Demonstrates how to update information about an Amazon DynamoDB table.

To run these examples, you can setup your development environment. For more information, 
see [Getting started using the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html). 
