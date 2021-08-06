# Amazon SNS C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Notification Service 
(Amazon SNS) using the AWS SDK for C++.

Amazon SNS is a fully managed messaging service for both application-to-application (A2A) and application-to-person (A2P) communication. 

## Code examples
This is a workspace where you can find AWS SDK for C++ SNS examples.

- [Creating an Amazon SNS topic to which notifications can be published](./create_topic.cpp) (CreateTopic)
- [Deleting an Amazon SNS topic and all its subscriptions](./delete_topic.cpp) (DeleteTopic)
- [Retrieving the settings for sending Amazon SMS messages](./get_sms_type.cpp) (GetSMSAttributes)
- [Retrieving the properties of an Amazon SNS topic](./get_topic_attributes.cpp) (GetTopicAttributes)
- [Retrieving a list of Amazon SNS subscriptions](./list_subscriptions.cpp) (ListSubscriptions)
- [Retrieving a list of Amazon SNS topics](./list_topics.cpp) (ListTopics)
- [Sending an SMS text message to a phone number](./publish_sms.cpp) (Publish)
- [Sending a message to an Amazon SNS topic](./publish_to_topic.cpp) (Publish)
- [Setting default SMS attributes](./set_sms_type.cpp) (SetSMSAttributes)
- [Subscribing to an Amazon SNS topic with delivery to a mobile app](./subscribe_app.cpp) (Subscribe)
- [Subscribing to an Amazon SNS topic with delivery to an email address](./subscribe_email.cpp) (Subscribe)
- [Subscribing to an Amazon SNS topic with delivery to an AWS Lambda function](./subscribe_lambda.cpp) (Subscribe)
- [Unsubscribing to an Amazon SNS topic](./unsubscribe.cpp) (Unsubscribe)

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

To run these code examples, your AWS user must have permissions to perform these actions with SNS.  
The AWS managed policy named "AmazonSNSFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 

