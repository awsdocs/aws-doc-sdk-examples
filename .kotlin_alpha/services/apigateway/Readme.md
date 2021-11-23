# Amazon API Gateway Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon API Gateway.

## Running the Amazon API Gateway files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a RestApi resource. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateDeployment** - Demonstrates how to create a deployment resource.
- **CreateRestApi** - Demonstrates how to create a new RestApi resource.
- **DeleteRestApi** - Demonstrates how to delete an existing RestApi resource.
- **GetAPIKeys** - Demonstrates how to obtain information about the current ApiKeys resource.
- **GetDeployments** - Demonstrates how to get information about a deployment collection.
- **GetMethod** - Demonstrates how to describe an existing method resource.
- **GetStages** - Demonstrates how to get information about stages.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
