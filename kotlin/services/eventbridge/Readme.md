# Amazon EventBridge Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon EventBridge.

## Running the Amazon EventBridge Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a rule. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateRule** - Demonstrates how to create an Amazon EventBridge rule.
- **DeleteRule** - Demonstrates how to delete an Amazon EventBridge rule.
- **DescribeRule** - Demonstrates how to describe an Amazon EventBridge rule.
- **ListEventBuses** - Demonstrates how to list your Amazon EventBridge buses.
- **ListRules** - Demonstrates how to list your Amazon EventBridge rules.
- **PutEvents** - Demonstrates how to send custom events to Amazon EventBridge.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
