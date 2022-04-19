# DynamoDB code examples for the AWS SDK for Go

## Overview

Shows how to use the AWS SDK for Go (V2) to create Amazon DynamoDB
tables and move data in and out of them.

*Amazon DynamoDB is a fully managed NoSQL database service that provides fast and
predictable performance with seamless scalability.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Create a table](actions/table_basics.go)
  (`CreateTable`)
* [Delete a table](actions/table_basics.go)
  (`DeleteTable`)
* [Delete an item from a table](actions/table_basics.go)
  (`DeleteItem`)
* [Get an item from a table](actions/table_basics.go)
  (`GetItem`)
* [Get information about a table](actions/table_basics.go)
  (`DescribeTable`)
* [List tables](actions/table_basics.go)
  (`ListTables`)
* [Put an item in a table](actions/table_basics.go)
  (`PutItem`)
* [Query a table](actions/table_basics.go)
  (`Query`)
* [Scan a table](actions/table_basics.go)
  (`Scan`)
* [Update an item in a table](actions/table_basics.go)
  (`UpdateItem`)
* [Write a batch of items](actions/table_basics.go)
  (`BatchWriteItem`)

### Scenario

* [Get started using tables, items, and queries](scenarios/scenario_movie_table.go)
  (`DescribeTable`)

## Running the examples

### Get started using tables, items, and queries

This interactive scenario runs at a command prompt and shows you how to use DynamoDB
to do the following:

- Create a table that can hold movie data.
- Put, get, and update a single movie in the table.
- Write movie data to the table from a sample JSON file.
- Query for movies that were released in a given year.
- Scan for movies that were released in a range of years.
- List tables in your account.
- Delete a movie from the table.
- Delete the table.

Install all required resources and start the example by running the following in the 
`dynamodb` folder at a command prompt.

```
go mod tidy
go run ./cmd
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

## Tests

Instructions for running the tests for this service can be found in the
[README](../README.md#Tests) in the GoV2 folder.

## Additional resources

* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
* [Amazon DyamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
* [AWS SDK for Go DynamoDB Client](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/dynamodb)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
