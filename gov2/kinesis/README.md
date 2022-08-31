# Amazon Kinesis examples for the AWS SDK for Go (v2)

## Overview

These examples demonstrates how to perform several Kinesis operations
using version 2 of the AWS SDK for Go.

Amazon Kinesis makes it easy to collect, process, and analyze real-time,
streaming data so you can get timely insights and react quickly to new
information. Amazon Kinesis offers key capabilities to cost-effectively process
streaming data at any scale, along with the flexibility to choose the tools
that best suit the requirements of your application.

## ⚠️ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website.
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Put data into a stream](PutRecord) (`PutRecord`)

## Run the examples

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

## Tests

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

## Additional Resources

- [AWS SDK for Go V3 Amazon Kinesis](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)
- [Amazon Kinesis documentation](https://docs.aws.amazon.com/kinesis)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
