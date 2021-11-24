# Amazon SQS Kotlin code examples

This README discusses how to run the Kotlin code examples for the Amazon Simple Queue Service (Amazon SQS).

## Running the Amazon SQS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a message. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddQueueTags** - Demonstrates how to add tags to an Amazon SQS queue.
- **CreateQueue** - Demonstrates how to create an Amazon SQS queue.
- **DeleteMessages** - Demonstrates how to delete Amazon SQS messages and a queue.
- **ListQueues** - Demonstrates how to list Amazon SQS queues.
- **ListQueueTags** - Demonstrates how to retrieve tags from an Amazon SQS queue.
- **RetrieveMessages** - Demonstrates how to retrieve messages from an Amazon SQS queue.
- **RemoveQueueTag** - Demonstrates how to remove a tag from an Amazon SQS queue.
- **SendMessages** - Demonstrates how to send a message to an Amazon SQS queue.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
