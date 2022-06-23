# Amazon Redshift Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Redshift.

## Running the Amazon Redshift Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a cluster. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateAndModifyCluster** - Demonstrates how to create and modify an Amazon Redshift cluster.
- **DeleteCluster** - Demonstrates how to delete an Amazon Redshift cluster.
- **DescribeClusters** - Demonstrates how to describe Amazon Redshift clusters.
- **FindReservedNodeOffer** - Demonstrates how to find additional Amazon Redshift nodes for purchase.
- **ListEvents** - Demonstrates how to list events for a given Amazon Redshift cluster.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
