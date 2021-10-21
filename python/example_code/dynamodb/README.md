# Amazon DynamoDB examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to create Amazon DynamoDB 
tables and move data in and out of them.
 
* Create a table for storing movies.
* Load movies into the table from a JSON-formatted file.
* Update and query movies in the table.
* Get, write, and delete items in batches.
* Accelerate reads with DynamoDB Accelerator (DAX).

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and 
predictable performance with seamless scalability.*

## Code examples

**API examples**

* [Creating a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesCreateTable.py)
(`create_table`)
* [Deleting a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesDeleteTable.py)
(`delete_table`)
* [Deleting an item from a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps06.py)
(`delete_item`)
* [Getting an item from a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps02.py)
(`get_item`)
* [Listing tables](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesListTables.py)
(`list_tables`)
* [Putting an item into a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps01.py)
(`put_item`)
* [Putting items loaded from a JSON file into a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesLoadData.py)
(`put_item`)
* [Querying items and projecting a subset of data](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesQuery02.py)
(`query`)
* [Querying items by using a key condition expression](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesQuery01.py)
(`query`)
* [Scanning a table for items](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesScan.py)
(`scan`)
* [Updating an item by using a conditional expression](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps05.py)
(`update_item`)
* [Updating an item by using an update expression](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps04.py)
(`update_item`)
* [Updating an item in a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps03.py)
(`update_item`)
* [Updating an item in two steps](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/MoviesItemOps03a.py)
(`get_item`, `put_item`)

**Batch examples**

* [Getting a batch of items from a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/batching/dynamo_batching.py)
(`batch_get_item`)
* [Getting, writing, and deleting batches of items](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/batching/dynamo_batching.py)
* [Writing a batch of items to table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/batching/dynamo_batching.py)
(`batch_write_item`)

**DynamoDB Accelerator (DAX) examples**

* [Accelerate reads with DAX](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/TryDax)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

Find instructions on how to set up and run the code for these examples in their
respective README files.

* [API examples](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/GettingStarted/README.md)
* [Batch examples](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/batching/README.md)
* [DAX examples](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/dynamodb/TryDax/README.md)

## Additional information

- [Boto3 Amazon DynamoDB service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)
- [Amazon DynamoDB documentation](https://docs.aws.amazon.com/dynamodb)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
