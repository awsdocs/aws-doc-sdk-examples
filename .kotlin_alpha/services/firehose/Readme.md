# Amazon Kinesis Date Firehose Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Kinesis Date Firehose.

## Running the Amazon Kinesis Date Firehose Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a delivery stream. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateDeliveryStream** - Demonstrates how to create a delivery stream.
- **DeleteStream** - Demonstrates how to delete a delivery stream.
- **ListDeliveryStreams** - Demonstrates how to list all delivery streams.
- **PutBatchRecords** - Demonstrates how to write multiple data records into a delivery stream and check each record using the response object.
- **PutRecord** - Demonstrates how to write a data record into a delivery stream.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
