# AWS SDK for Go V2 code examples for Amazon Kinesis

## Purpose

These examples demonstrates how to perform several Kinesis operations
using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### PutRecord/PutRecordv2.go

This example produces a new data record in the stream.

`go run PutRecordv2.go -s STREAM -k PARTITION-KEY -p PAYLOAD`

- _STREAM_ is the Kinesis stream name.
- _PARTITION-KEY_ is the partition ID.
- _PAYLOAD_ is the content to be published.

The unit test accepts similar values in _config.json_.

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
