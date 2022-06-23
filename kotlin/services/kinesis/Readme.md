# Amazon Kinesis Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Kinesis.

## Running the Kinesis Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a data stream. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddDataShards** - Demonstrates how to increase shard count in an Amazon Kinesis data stream.
- **CreateDataStream** - Demonstrates how to create an Amazon Kinesis data stream.
- **DeleteDataStream** - Demonstrates how to delete an Amazon Kinesis data stream.
- **DescribeLimits** - Demonstrates how to display the shard limit and usage for a given account.
- **GetRecords** - Demonstrates how to read multiple data records from an Amazon Kinesis data stream.
- **ListShards** - Demonstrates how to list the shards in an Amazon Kinesis data stream.
- **StockTradesWriter** - Demonstrates how to write multiple data records into an Amazon Kinesis data stream.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
