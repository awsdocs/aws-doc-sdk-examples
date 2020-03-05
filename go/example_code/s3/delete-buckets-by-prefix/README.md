# AWS SDK for Go Code Examples for Amazon S3

## Purpose

This example demonstrates how to delete all of the Amazon S3 buckets you own that begin with a given prefix.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

### Syntax

`go run s3_delete_buckets -p` *PREFIX*

- *PREFIX* is the first characters of the names of the Amazon S3 buckets to delete.

For example, if you call `go run s3_delete_buckets.go -p dummy-`,
it first removes all of the objects in the Amazon S3 buckets with names starting with *dummy-*,
then deletes all of those S3 buckets.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum  permissions required to perform the task.
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

The unit test:

1. Creates three S3 buckets,
   with a name starting with *dummy-*,
   followed by a GUID, and then the numbers 0-2.
2. Lists all of the buckets with a name starting with *dummy-*
3. Removes all objects in the S3 buckets with names starting with *dummy-*
4. Deletes all S3 buckets with names starting with *dummy-*
5. Lists all of the buckets with a name starting with *dummy-*

To run the unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any resources it created by running:

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0