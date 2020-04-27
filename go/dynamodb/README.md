# AWS SDK for Go Code Examples for Amazon DynamoDB

## Purpose

These examples demonstrates how to perform several DynamoDB operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

### CreateTable/CreateTable.go

This example creates a DynamoDB table.

`go run CreateTable.go -t TABLE`

- _TABLE_ is the name of the table.

The unit test accepts a similar value in _config.json_.

The table has two attributes:

- **Year** is an integer
- **Title** is a string

### CreateTableItem/CreateTableItem.go

Creates a new item in a DynamoDB table.

`go run CreateTableItem -d TABLE -y YEAR -t TITLE -r RATING`

- _TABLE_ is the name of the table.
- _YEAR_ is the year that the movie was released.
- _TITLE_ is the title of the movie.
- _RATING_ is the rating, from 0.0 to 10.0, of the movie.

The unit test accepts similar values in _config.json_.

### ListTables/ListTables.go

This example lists your DynamoDB tables.

`go run ListTables.go [-l LIMIT]`

- _LIMIT_ is how many tables to show.
  The default is 100.
  If this value is less than zero,
  it is set to 10.

The unit test accepts a similar value in _config.json_.

### UpdateItem/UpdateItem.go

This example updates the year and rating of a movie in a table.

`go run UpdateItem.go -t TABLE -m MOVIE -y YEAR -r RATING`

- _TABLE_ is the name of the table.
- _MOVIE_ is the name of the movie.
- _YEAR_ is the year the movie was released.
- _RATING_ is the rating, from 0.0 to 1.0.

The unit test accepts similar values from _config.json_.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the Unit Tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run the unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
