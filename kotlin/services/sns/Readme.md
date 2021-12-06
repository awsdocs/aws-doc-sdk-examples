# Amazon SNS Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Simple Notification Service (Amazon SNS).

## Running the Amazon SNS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an SNS topic. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddTags** - Demonstrates how to add tags to an Amazon SNS topic.
- **CreateTopic** - Demonstrates how to create an Amazon SNS topic.
- **DeleteTag** - Demonstrates how to delete tags from an Amazon SNS topic.
- **DeleteTopic** - Demonstrates how to delete an Amazon SNS topic.
- **GetTopicAttributes** - Demonstrates how to retrieve the defaults for an Amazon SNS topic.
- **ListSubscriptions** - Demonstrates how to list existing Amazon SNS subscriptions.
- **ListTags** - Demonstrates how to retrieve tags from an Amazon SNS topic.
- **ListTopics** - Demonstrates how to get a list of existing Amazon SNS topics.
- **PublishTextSMS** - Demonstrates how to send an Amazon SNS text message.
- **PublishTopic** - Demonstrates how to publish an Amazon SNS topic.
- **SetTopicAttributes** - Demonstrates how to set attributes for an Amazon SNS topic.
- **SubscribeEmail** - Demonstrates how to subscribe to an Amazon SNS email endpoint.
- **SubscribeLambda** - Demonstrates how to subscribe to an Amazon SNS lambda function.
- **SubscribeTextSMS** - Demonstrates how to subscribe to an Amazon SNS text endpoint.
- **SubscribeTextSMS** - Demonstrates how to subscribe to an Amazon SNS text endpoint.
- **Unsubscribe** - Demonstrates how to remove an Amazon SNS subscription.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
