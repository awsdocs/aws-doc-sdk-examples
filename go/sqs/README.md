# AWS SDK for Go code examples for Amazon SQS

## Purpose

These examples demonstrate how to perform Amazon SQS operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code examples

All of these code examples perform the operations in the default region
and use your default credentials.

### Operations

The **sqsQueueOps.go** file defines a number of SQS operations.

Use the following command to display the commands that invoke these operations.

`go run sqsQueueOps.go -h`

### Notes

- You should grant these code examples least privilege,
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

The unit test **sqsQueues_test.go**:

- Lists all of the queues
- Creates a queue with a random name that starts with **MyQueue-**
- Gets the URL of the queue
- Creates a dead-letter queue with a random name that starts with **MyDLQueue-** for the previous queue
- Sends a message to the queue
- Receives the message from the queue
- Changes the visibility timeout for the message
- Deletes the message
- Changes the original queue to use long-polling
- Creates a long-polling queue with a random name that starts with **MyLPQueue-**
- Sends a message to the long-polling queue
- Receives the message in the long-polling queue
- Deletes the queues

To run the unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 1.904s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any resources it created by looking at the remaining queues
for any that start with **MyQueue-**, **MyDLQueue-**, or **MyLPQueue-**:

`go run sqsQueueOps.go -l`