# Amazon SQS queue and message examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to get started using queue and 
message operations in Amazon Simple Queue Service (Amazon SQS). Learn how to 
create, get, and remove standard, FIFO, and dead-letter queues. Learn how to 
send, receive, and delete messages from a queue.

*Amazon SQS is a fully managed message queuing service that makes it easy to decouple 
and scale microservices, distributed systems, and serverless applications.*

## Code examples

### Scenario examples

* [Manage queues](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/queue_wrapper.py)
* [Send and receive batches of messages](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)

### API examples

* [Create a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/queue_wrapper.py)
(`CreateQueue`)
* [Delete a batch of messages from a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)
(`DeleteMessageBatch`)
* [Delete a message from a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)
(`DeleteMessage`)
* [Delete a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/queue_wrapper.py)
(`DeleteQueue`)
* [Get the URL of a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/queue_wrapper.py)
(`GetQueueUrl`)
* [List queues](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/queue_wrapper.py)
(`ListQueues`)
* [Receive messages from a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)
(`ReceiveMessage`)
* [Send a batch of messages to a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)
(`SendMessageBatch`)
* [Send a message to a queue](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/sqs/message_wrapper.py)
(`SendMessage`)

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
- Python 3.7 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

Both `queue_wrapper.py` and `message_wrapper.py` contain `usage_demo` functions
that demonstrate ways to use the functions in their respective modules. 
For example, to see the queue demonstration, run the module in a command window.

```
python -m queue_wrapper
``` 

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/sqs folder.

```    
python -m pytest
```

## Additional information

- [Boto 3 Amazon SQS documentation](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sqs.html)
- [Amazon SQS Documentation](https://docs.aws.amazon.com/sqs)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
