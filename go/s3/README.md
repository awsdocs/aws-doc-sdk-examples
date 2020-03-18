# AWS SDK for Go Code Examples for Amazon S3

## Purpose

These examples demonstrates how to perform several Amazon S3 operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

### CustomClient/CustomHTTPClient.go

This example either create a custom HTTP client and uses it to get an S3 bucket object,
or gets the S3 bucket object using a custom timeout of 20 seconds.

`go run CustomHTTPClient.go -b BUCKET -o OBJECT [-s] [-t]`

- *BUCKET* name of the bucket (required).
- *OBJECT* is the name of the object (required).
- **-s** shows the object, as a string (optional).
- **-t** gets the object using a custom timout; otherwise it uses a custom HTTP client.

The unit test accepts similar values from *config.json*.

### TLS/s3SetTls12.go

This example demonstrates how to use the 1.2 version of TLS (transport layer security).
It creates an Amazon S3 client and determines whether it can access an object in a bucket.

`go run s3SetTls12 -b BUCKET -o OBJECT [-r REGION] [-v]`

- *BUCKET* is the name of the Amazon S3 bucket to confirm access.
- *OBJECT* is an object in *BUCKET*.
- If *REGION* is not specified, it defaults to **us-west-2**.
- If **-v** is not specified, it configures the session for Go version 1.13.

The unit test accepts similar values from *config.json*.

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

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0