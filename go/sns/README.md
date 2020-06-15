# AWS SDK for Go code examples for Amazon SNS

## Purpose

These examples demonstrate how to perform several Amazon Simple Notification Service (Amazon SNS) operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the _AWS SDK for Go Developer Guide_.

## Running the code

### MakeTopic/MakeTopic.go

This example creates an Amazon SNS topic.

`go run MakeTopic.go -t TOPIC`

- _TOPIC_ is the name of the topic to create.

The unit test mocks the service client and the `CreateTopic` function.

### PublishMessage/PublishMessage.go

This example publishes a message to an Amazon SNS topic.

`go run PublishMessage.go -m MESSAGE -t TOPIC-ARN`

- _MESSAGE_ is the message to publish.
- _TOPIC-ARN_ is the Amazon Resource Name (ARN) of the topic to which the user subscribes.

The unit test mocks the service client and the `Publish` function.

### ShowSubscriptions/ShowSubscriptions.go

This example displays the Amazon Resource Names (ARNs) of your Amazon SNS subscriptions.

`go run ShowSubscriptions.go`

The unit test mocks the service client and the `ListSubscriptions` function.

### ShowTopics/ShowTopics.go

This example displays the Amazon Resource Names (ARNs) of your Amazon SNS topics.

`go run ShowTopics.go`

The unit test mocks the service client and the `ListTopics` function.

### SubscribeTopic/SubscribeTopic.go

This example subscribes a user to a topic by their email address.

`go run SubscribeTopic.go -e EMAIL -t TOPIC-ARN`

- _EMAIL_ is the email address of the user.
- _TOPIC-ARN_ is the Amazon Resource Name (ARN) of the topic.

The unit test mocks the service client and the `Subscribe` function.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the _AWS Identity and Access Management User Guide_.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter the following.

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files.

```sh
PASS
ok      PATH 6.593s
```

To see any log messages, enter the following.

`go test -test.v`

You should see additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
