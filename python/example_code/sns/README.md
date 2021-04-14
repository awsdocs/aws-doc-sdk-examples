# Amazon Simple Notification Service topics and subscriptions example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Notification Service
(Amazon SNS).

* Create a notification topic.
* Subscribe phone numbers and emails to the topic.
* Publish messages to the topic and receive them at your subscribed endpoints.
* Add filtering attributes to a subscription.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.5 or later
- Boto3 1.15.4 or later
- PyTest 5.3.5 or later (to run unit tests)

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

Run this example at a command prompt with the following command.

```
python sns_basics.py
``` 

### Example structure

The example contains the following files.

**sns_basics.py**

Shows how to use Amazon SNS to subscribe to a topic and receive messages.

1. Creates a notification topic.
2. Sends a message directly to a phone number.
3. Subscribes an email and a phone number to the topic.
4. Publishes a multi-format message to the topic. Subscribers receive different
messages depending on their notification protocol.
5. Adds an attribute filter to the phone number subscription and publishes messages
to the topic. Only messages with matching filters are sent to the phone number.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your [GitHub root]/python/example_code/sns
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon SNS service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sns.html)
- [Amazon Simple Notification Service documentation](https://docs.aws.amazon.com/sns/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
