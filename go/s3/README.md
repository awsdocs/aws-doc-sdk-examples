# AWS SDK for Go Code Example for TLS 1.2

## Purpose

This example demonstrates how to use the 1.2 version of TLS (transport layer security).
It creates an Amazon S3 client and determines whether it can access an item in a bucket.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

### Syntax

`go run s3SetTls12 -b BUCKET -i ITEM [-r REGION] [-v]`

- BUCKET is the name of the Amazon S3 bucket to confirm access
- ITEM is an object in BUCKET
- If REGION is not specified, defaults to **us-west-2**
- If -v is not specified, configures the session for Go version 1.13

For example, if you call `go run s3SetTls12 mygroovybucket -i myitem`,
it attempts to access *myitem* in *mygroovybucket*,
and prints a message with the result.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum  permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account.

The unit test does the following:

1. Loads configuration values from *config.json*.
2. Creates a random bucket name starting with "testbucket-".
3. Creates the item "testitem" with some text.
4. Attempts to access the item in the bucket
   and logs the result.
5. Deletes the bucket and item.

To run the unit test, enter the following:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 5.956s
```

To see any log messages, enter the following:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any resources it created by running:

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0