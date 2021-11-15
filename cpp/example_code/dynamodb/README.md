# Amazon DynamoDB C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon DynamoDB 
using the AWS SDK for C++.

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable 
performance with seamless scalability.

## Code examples

### API examples
- [Add an item to a table](./put_item.cpp) (PutItem)
- [Create a table](./create_table.cpp) (CreateTable)
- [Create a table with a composite key](./create_table_composite_key.cpp)
- [Delete items from a table](./delete_item.cpp) (DeleteItem)
- [Delete a table](./delete_table.cpp) (DeleteTable)
- [Get an item](./get_item.cpp) (GetItem)
- [Get multiple items](./batch_get_item.cpp) (BatchGetItem)
- [List your tables](./list_tables.cpp)  (ListTables)
- [Query a table for items](./query_items.cpp) (Query)
- [Scan a table](./scan_table.cpp) (Scan)
- [Update an item](./update_item.cpp)  (UpdateItem)
- [Update a table](./update_table.cpp)  (UpdateTable)

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- Complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 
- See [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

To run these code examples, your AWS user must have permissions to perform these actions with Amazon DynamoDB.  
The AWS managed policy named "AmazonDynamoDBFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon DynamoDB Documentation](https://docs.aws.amazon.com/dynamodb/)
