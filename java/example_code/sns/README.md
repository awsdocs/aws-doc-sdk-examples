# Amazon Simple Notification Service examples

## Purpose

Shows how to use the AWS SDK for Java with Amazon Simple Notification Service
(Amazon SNS).

*Amazon SNS enables applications, end-users, and devices to instantly send and 
receive notifications from the cloud.*

## Code examples

* [Create a platform endpoint for push notifications](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/sns/CreateMobileEndpoint.java)
* [Create and publish to a FIFO topic](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/sns/FifoTopics.java)
* [Publish a large message](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/sns/PublishLargeFile.java)
* [Set a dead-letter queue for a subscription](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/sns/example_code/RedrivePolicy.java)
* [Publish SMS messages to a topic](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/sns/PublishSmsToTopic.java)

## ⚠ Important

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

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).

## Additional information

- [Amazon Simple Notification Service documentation](https://docs.aws.amazon.com/sns/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
