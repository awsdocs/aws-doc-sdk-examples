# Amazon SNS code examples for the AWS SDK for Go (v2)

## Overview

These examples demonstrates how to perform several Amazon Simple Notification Service 
(Amazon SNS) operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateTopic/CreateTopicv2.go

This example creates an Amazon SNS topic.

`go run CreateTopicv2.go -t TOPIC`

- _TOPIC_ is the name of the topic to create.

The unit test accepts a similar value in _config.json_.

### ListSubscriptions/ListSubscriptionsv2.go

This example lists the topic and subscription Amazon Resource Names (ARNs) for your Amazon SNS subscriptions.

`go run ListSubscriptionsv2.go`

### ListTopics/ListTopicsv2.go

This example lists the ARNs for your Amazon SNS topics.

`go run ListTopicsv2.go`

### Publish/Publishv2.go

This example publishes a message to an Amazon SNS topic.

`go run Publishv2.go -m MESSAGE -t TOPIC-ARN`

- _MESSAGE_ is the message to publish.
- _TOPIC-ARN_ is the ARN of the topic to which the message is published.

The unit test accepts similar values in _config.json_.

### Subscribe/Subscribev2.go

This example subscribes a user, by email address, to an Amazon SNS topic.

`go run Subscribev2.go -m EMAIL-ADDRESS -t TOPIC-ARN`

- _EMAIL-ADDRESS_ is the email address of the user subscribing to the topic.
- _TOPIC-ARN_ is the ARN of the topic.

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
