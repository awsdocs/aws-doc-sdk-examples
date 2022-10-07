# AWS SDK for Rust code examples for Amazon DynamoDB

## Purpose

These examples demonstrate how to perform several Amazon DynamoDB (DynamoDB) operations using the developer preview version of the AWS SDK for Rust.
Most use the schema defined in the **create-table** example.

DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.

## Code examples

- [Add item to table](src/bin/add-item.rs) (PutItem)
- [Are there more tables](src/bin/are-more-tables.rs) (ListTables)
- [Create a table](src/bin/create-table.rs) (CreateTable)
- [Create, read, update, delete table](src/bin/crud.rs) (CreateTable, DeleteItem, DeleteTable, PutItem, Query)
- [Delete table item](src/bin/delete-item.rs) (DeleteItem)
- [Delete a table](src/bin/delete-table.rs) (DeleteTable)
- [List tables and create a table](src/bin/dynamodb-helloworld.rs) (CreateTable, ListTables)
- [List 10 tables](src/bin/list10-tables.rs) (ListTables)
- [List the items in a table](src/bin/list-items.rs) (Scan)
- [List more tables](src/bin/list-more-tables.rs) (ListTables)
- [Lists your tables](src/bin/list-tables.rs) (ListTables)
- [Lists your local tables](src/bin/list-tables-local.rs) (ListTables)
- [Minimal version of listing tables](src/bin/list-tables-main.rs) (ListTables)
- [Create a table, adds some items from a file to the table, queries the table, and deletes the table](src/bin/movies.rs) (CreateTable, DeleteTable, ListTables, PutItem, Query)
  - To run this example, you should replace the `moviedata.json` file with the [DynamoDB Movies](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip) zip database.
  - On a \*nix environment with bash, you can use this command (be sure to run it from the same directory as this README):
    `curl https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip > moviedata.zip ; unzip moviedata.zip ; rm moviedata.zip `

## ⚠ Important

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### add-item

This example adds a new item to the specified table.

`cargo run --bin add-item -- -t TABLE -u USERNAME -p PERMISSION-TYPE -a AGE -f FIRST-NAME -l LAST-NAME [-r REGION] [-v]`

- _TABLE_ is the name of the table to which the item is added.
- _USERNAME_ is the username of the user to add to the table. This is the key index to the table.
- _PERMISSION-TYPE_ is the type of user, either "standard_user" or "admin".
- _AGE_ is the age of the user.
- _FIRST-NAME_ is the first name of the user.
- _LAST-NAME_ is the last name of the user.
- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### are-more-tables

This example lists up to 10 tables,
and if there are more tables, displays "There are more tables".

`cargo run --bin are-more-tables -- [-r REGION] [-v]`

- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### create-table

This example creates a table.
Use **delete-table** to delete the table you've created.

`cargo run --bin create-table -- -t TABLE -k KEY [-r REGION] [-v]`

- _TABLE_ is the name of the table to which the item is added.
- _KEY_ is the primary key for the table.
- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### crud

This example creates a table, adds an item to the table, updates the item, deletes the item, and deletes the table.

`cargo run --bin crud -- [-i] [-r REGION] [-v]`

- **-i** enables interactive mode, which pauses the code between operations.
- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### delete-item

This example deletes an item from a DynamoDB table.

`cargo run --bin delete-item -- -t TABLE -k KEY -v VALUE [-r REGION] [-i]`

- _TABLE_ is the name of the table containing the item to delete.
- _KEY_ is the name of the primary key of the item to delete.
- _VALUE_ is the value of the primary key of the item to delete.
- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-i** displays additional information.

### delete-table

This example deletes a DynamoDB table.

`cargo run --bin delete-table -- -t TABLE [-r REGION] [-v]`

- _TABLE_ is the name of the table to delete.
- _REGION_ is name of the AWS Region, such as **us-east-1**, where the table is located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### dynamodb-helloworld

This example lists your DynamoDB tables and creates the table **test-table**.
Use **delete-table** to delete **test-table**.

`cargo run --bin dynamodb-helloworld`

### list10-tables

This example lists up to 10 of your DynamoDB tables.

`cargo run --bin list10-tables -- [-r REGION] [-v]`

- _REGION_ is name of the AWS Region, such as **us-east-1**, where the tables are located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### list-items

This example lists the items in a DynamoDB table.

`cargo run --bin list-items -- [-r REGION] [-v]`

- _REGION_ is name of the AWS Region, such as **us-east-1**, where the tables are located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### list-more-tables

This example lists your DynamoDB tables,
and every 10 tables displays "-- more --".

`cargo run --bin list-more-tables -- [-r REGION] [-v]`

- _REGION_ is name of the AWS Region, such as **us-east-1**, where the tables are located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### list-tables-local

This example lists your DynamoDB tables using LocalStack.

`cargo run --bin list-tables-local`

See the [Localstack with the AWS SDK for Rust](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/localstack.html) topic in the developer guide for details.

### list-tables-main

This is a minimal example of listing your DynamoDB tables.

`cargo run --bin list-tables-main`

### list-tables

This example lists your DynamoDB tables.

`cargo run --bin list-tables -- [-r REGION] [-v]`

- _REGION_ is name of the AWS Region, such as **us-east-1**, where the tables are located.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### movies

This example creates the DynamoDB table \_dynamo-movies-example** in **us-east-1**, waits for the table to be ready, adds a couple of rows to the table, and queries for those rows.
Use **delete-table** to delete **dynamo-movies-example\_\_.

`cargo run --bin movies`

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for DynamoDB](https://docs.rs/aws-sdk-dynamodb)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html)

## Contributing

To propose a new code example to the AWS documentation team,
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
