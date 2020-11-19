# AWS SDK for Go V2 code examples for Amazon SQS

## Purpose

These examples demonstrates how to perform several
Amazon Simple Queue Service (Amazon SQS)
operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region 
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### ChangeMsgVisibility/ChangeMsgVisibilityv2.go

This example sets the visibility timeout for a message in an Amazon SQS queue.

`go run ChangeMsgVisibilityv2.go -q QUEUE-NAME -h RECEIPT-HANDLE -v VISIBILITY`

- _QUEUE-NAME_ is the name of the queue.
- _RECEIPT-HANDLE_ is tThe name of the queue.
- _VISIBILITY_ is the duration, in seconds, that the message is not visible to other consumers.
  The example ensures the value is between 0 and 12 hours;
  the default is 30 seconds.

The unit test accepts similar values in _config.json_.

### ConfigureLPQueue/ConfigureLPQueuev2.go

This example configures an Amazon SQS queue to use long polling.

`go run ConfigureLPQueuev2.go -q QUEUE-NAME [-w WAIT-TIME]`

- _QUEUE-NAME_ is the name of the queue to configure.
- _WAIT-TIME_ is how long, in seconds, to wait.
  The example ensures the value is between 1 and 20;
  the default is 10.

The unit test accepts similar values in _config.json_.

### CreateQueue/CreateQueuev2.go

This example creates an Amazon SQS queue.

`go run CreateQueuev2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue to create.

The unit test accepts a similar value in _config.json_.

### CreateLPQueue/CreateLPQueuev2.go

This example creates a long-polling Amazon SQS queue.

`go run CreateLPQueuev2.go -q QUEUE-NAME [-w WAIT-TIME]`

- _QUEUE-NAME_ is the name of the queue to create.
- _WAIT-TIME_ is how long, in seconds, to wait.
  The example ensures the value is between 1 and 20;
  the default is 10.

The unit test accepts similar values in _config.json_.

### DeadLetterQueue/DeadLetterQueuev2.go

This example configures an Amazon SQS queue for messages 
that could not be delivered to another queue.

`go run DeadLetterQueuev2.go -q QUEUE-NAME -d DEAD-LETTER-QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue from which the dead letters are sent.
- _DEAD-LETTER-QUEUE-NAME_ is the name of the queue to which the dead letters are sent.

The unit test accepts similar values in _config.json_.

### DeleteMessage/DeleteMessagev2.go

This example deletes a message from an Amazon SQS queue.

`go run DeleteMessagev2.go -q QUEUE-NAME -m MESSAGE-HANDLE`

- _QUEUE-NAME_ is the name of the queue from which the message is deleted.
- _MESSAGE-HANDLE_ is the handle of the message to delete.

The unit test accepts similar values in _config.json_.

### DeleteQueue/DeleteQueuev2.go

This example deletes an Amazon SQS queue.

`go run DeleteQueuev2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue from which the message is deleted.

The unit test accepts a similar value in _config.json_.

### GetQueueURL/GetQueueURLv2.go

This example gets the URL of an Amazon SQS queue.

`go run GetQueueURLv2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue or which the URL is retrieved.

The unit test accepts a similar value in _config.json_.

### ListQueues/ListQueuesv2.go

This example retrieves a list of your Amazon SQS queues.

`go run ListQueuesv2.go`

### ReceiveLPMessage/ReceiveLPMessagev2.go

This example gets the most recent message from a long-polling Amazon SQS queue.

`go run ReceiveLPMessagev2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue from which the message is retrieved.

The unit test accepts a similar value in _config.json_.

### ReceiveMessage/ReceiveMessagev2.go

This example gets the most recent message from an Amazon SQS queue.

`go run ReceiveMessagev2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue from which the message is retrieved.

The unit test accepts a similar value in _config.json_.

### SendMessage/SendMessagev2.go

This example sends a message to an Amazon SQS queue.

`go run SendMessagev2.go -q QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue to which the message is sent.

The unit test accepts a similar value in _config.json_.

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
