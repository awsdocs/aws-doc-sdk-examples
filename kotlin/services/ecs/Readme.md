# Amazon Elastic Container Service Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Elastic Container Service (Amazon ECS).

## Running the Amazon ECS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon ECS service. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateCluster** - Demonstrates how to create a cluster for the Amazon ECS service.
- **CreateService** - Demonstrates how to create a service for the Amazon ECS service.
- **DeleteService** - Demonstrates how to delete a service for the Amazon ECS service.
- **DescribeClusters** - Demonstrates how to describe a cluster for the Amazon ECS service.
- **ListClusters** - Demonstrates how to list clusters for the Amazon ECS service.
- **UpdateService** - Demonstrates how to update the task placement strategies and constraints on an Amazon ECS service.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
