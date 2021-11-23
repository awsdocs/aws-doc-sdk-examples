# Amazon EMR Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon EMR.

## Running the Amazon EMR Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddSteps** - Demonstrates how to add new steps to a running cluster.
- **CreateCluster** - Demonstrates how to create and start running a new cluster (job flow).
- **CreateEmrFleet** - Demonstrates how to create a cluster using instance fleet with spot instances.
- **CreateSparkCluster** - Demonstrates how to create and start running a new cluster (job flow).
- **DescribeCluster** - Demonstrates how to describe a given cluster.
- **ListClusters** - Demonstrates how to list clusters.
- **TerminateJobFlow** - Demonstrates how to terminate a given job flow.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
