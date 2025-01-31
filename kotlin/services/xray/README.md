# AWS X-Ray Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS X-Ray.

## Running the AWS X-Ray Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an AWS X-Ray group. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateGroup** - Demonstrates how to create an AWS X-Ray group with a filter expression.
- **CreateSamplingRule** - Demonstrates how to create a rule to control sampling behavior for instrumented applications.
- **DeleteGroup** - Demonstrates how to delete an AWS X-Ray group.
- **DeleteSamplingRule** - Demonstrates how to create a rule to control sampling behavior for instrumented applications.
- **GetGroups** - Demonstrates how to retrieve all active group details.
- **GetSamplingRules** - Demonstrates how to retrieve sampling rules.
- **GetServiceGraph** - Demonstrates how to retrieve a document that describes services that process incoming requests.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
