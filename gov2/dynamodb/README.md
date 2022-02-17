# AWS SDK for Go V2 code examples for Amazon DynamoDB

## Purpose

These examples demonstrates how to perform several DynamoDB operations
using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### DescribeTable/DescribeTablev2.go

This example lists the following properties of a DynamoDB table.

- Number of items
- Size, in bytes
- Status, such as Active

`go run DescribeTablev2.go -t TABLE`

- _TABLE_ is the name of the table.

The unit test accepts a similar value in _config.json_.

### ScanItems/ScanItemsv2.go

This example retrieves the Amazon DynamoDB items with a rating above a specified value
in a specified year.

`go run ScanItemsv2.go -t TABLE -r RATING -y YEAR`

- _TABLE_ is the name of the table.
- _RATING_ is the rating of the item, from 0.0 to 10.0.
- _YEAR_ is the year of the item, which must be greater than 1900.

The unit test accepts similar values in _config.json_.

### Using Amazon DynamoDB local

You can test your Go code against a local version of Amazon DynamoDB.
Doing so elimiates the possibility of incurring charges against your Amazon account.

For information on installing the local version of Amazon DynamoDB, see
[Setting Up DynamoDB Local (Downloadable Version)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
in the Amazon DynamoDB Developer Guide.

To run the local version of Amazon DynamoDB, perform the following steps:

1. Navigate to where you've installed the local version of Amazon DynamoDB.
1. Run the following command:
   ```
   java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
   ```
If you don't care whether any information is saved between session, add the **-inMemory** flag.  

In your Go code, change the **config** object you create from:
```
cfg, err := config.LoadDefaultConfig(context.TODO())
```
to:
```
cfg, err := config.LoadDefaultConfig(context.TODO(),
    config.WithEndpointResolver(aws.EndpointResolverFunc(
        func(service, region string) (aws.Endpoint, error) {
            return aws.Endpoint{URL: "https://localhost:8000"}, nil
    })),
)
```

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
