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

### DeleteItem/DeleteItem.go

This example deletes an item from a DynamoDB table.

`go run DeleteTable.go -t TABLE -m MOVIE -y YEAR`

- _TABLE_ is the name of the table containing the item to delete.
- _MOVIE_ is the name of the movie item to delete.
- _YEAR_ is when the movie was released.

The unit test mocks the DynamoDB service and the `DeleteItem` function.

### GetItem/GetItem.go

This example retrieves an item from a DynamoDB table.

`go run GetItem.go -t TABLE -n NAME -y YEAR`

- _TABLE_ is the name of the table
- _NAME_ is the name of the movie
- _YEAR_ is when the movie was released

The unit test mocks the DynamoDB service and `GetItem` function.

### ListTables/ListTables.go

This example lists your DynamoDB tables.

`go run ListTables.go [-l LIMIT]`

- _LIMIT_ is how many tables to show.
  The default is 100.
  If this value is less than zero,
  it is set to 10.

The unit test accepts a similar value in _config.json_.

### LoadTableItems/LoadTableItems.go

This example adds items from a JSON file to a table.

`go run LoadTableItems.go -j JSON-FILE -d TABLE`

- _JSON-FILE_ is the name of the JSON file containing the items to load into the table.
- _TABLE_ is the name of the table.

The unit test accepts similar values in _config.json_.

### ScanItems/ScanItems.go

This example uses the Expression Builder package to scan a table for items that fit the criteria.

`go run ScanItems.go -t TABLE -r RATING -y YEAR`

- _TABLE_ is the name of the table
- _RATING_ is the minimum rating, from 0.0 to 1.0, given to the movies to retrieve
- _YEAR_ is the year when the movies were released

The unit test mocks the DynamoDB service and `Scan` function.

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
