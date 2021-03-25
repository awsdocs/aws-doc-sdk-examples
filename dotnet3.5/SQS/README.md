# Amazon SQS examples

## Purpose

Shows how to use the AWS SDK for .NET 3.x to get started using queue and 
message operations in Amazon Simple Queue Service (Amazon SQS). Learn how to 
create, get, and remove standard, FIFO, and dead-letter queues. Learn how to 
send, receive, and delete messages from a queue.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

## Cautions

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

The examples for SQS include the following:

| Example | Description |
|---------|-------------|
| [CreateSendExample](CreateSendExample/) | Create an SQS queue and sends a sample message. |
| PollReceiveExample | Polls a queue for message and then receives them. |
| DeleteQueue | Deletes an existing queue. |

## Running the tests


## Additional information

- [Put in a link to the AWS SDK for .NET User Guide].

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0