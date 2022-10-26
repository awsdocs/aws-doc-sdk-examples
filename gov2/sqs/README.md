# Amazon SQS code examples for the AWS SDK for Go (v2)

## Overview

These examples demonstrate how to perform several
Amazon Simple Queue Service (Amazon SQS)
actions using the AWS SDK for Go (v2).

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

[Change message timeout visibility](ChangeMsgVisibility/ChangeMsgVisibilityv2.go)
[Configure a queue](ConfigureLPQueue/ConfigureLPQueuev2.go)
[Configure a dead letter queue](DeadLetterQueue/DeadLetterQueuev2.go)
[Create a queue](CreateLPQueue/CreateLPQueuev2.go)
[Delete a message from a queue](DeleteMessage/DeleteMessagev2.go)
[Delete a queue](DeleteQueue/DeleteQueuev2.go)
[Get the URL of a queue](GetQueueURL/GetQueueURLv2.go)
[List queues](ListQueues/ListQueuesv2.go)
[Receive a message from a queue](ReceiveLPMessage/ReceiveLPMessagev2.go)
[Receive messages from a queue](ReceiveLPMessage/ReceiveLPMessagev2.go)
[Send a message to a queue](SendMessage/SendMessagev2.go)

## Running the examples

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region 
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Instructions

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

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

[Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
[Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
[AWS SDK for Go (v2) API Reference Guide](https://docs.aws.amazon.com/sdk-for-go/api/service/sqs/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
