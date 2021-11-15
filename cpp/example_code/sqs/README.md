# Amazon SQS C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Queue Service (Amazon SQS)
using the AWS SDK for C++.

Amazon SQS is a fully managed message queuing service that makes it easy to 
decouple and scale microservices, distributed systems, and serverless applications. 

## Code examples

### API examples
- [Change the visibility timeout of a message in an Amazon SQS queue](./change_message_visibility.cpp) (ReceiveMessage)
- [Create an Amazon SQS standard queue](./create_queue.cpp) (CreateQueue)
- [Delete an Amazon SQS queue](./delete_queue.cpp) (DeleteQueue)
- [Retrieve the URL of an Amazon SQS queue](./get_queue_url.cpp) (GetQueueUrl)
- [Retrieve a list of Amazon SQS queues for an AWS account](./list_queues.cpp) (ListQueues)
- [Change the amount of time an Amazon SQS queue waits for a message to arrive](./long_polling_on_existing_queue.cpp) (SetQueueAttributes)
- [Retrieve messages from an Amazon SQS queue using long-poll support](./long_polling_on_message_receipt.cpp) (ReceiveMessage)
- [Delete the messages in an Amazon SQS queue](./purge_queue.cpp) (PurgeQueue)
- [Deliver a message to an Amazon SQS queue](./send_message.cpp) (SendMessage)

### Usage scenarios
- [Enable the dead-letter functionality of an Amazon SQS queue](./dead_letter_queue.cpp)
- [Create an Amazon SQS queue that waits for a message to arrive](./long_polling_on_create_queue.cpp) 
- [Receive and delete a message from an Amazon SQS queue](./receive_message.cpp) 



## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- Complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 
- See [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

To run these code examples, your AWS user must have permissions to perform these actions with Amazon SQS.  
The AWS managed policy named "AmazonSQSFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon Simple Queue Service Documentation](https://docs.aws.amazon.com/sqs/)
