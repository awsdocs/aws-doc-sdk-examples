# AWS Secrets Manager Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Secrets Manager.

## Running the AWS Secrets Manager Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a secret. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateSecret** - Ddemonstrates how to create a secret for AWS Secrets Manager.
- **DeleteSecret** - Demonstrates how to delete a secret.
- **DescribeSecret** - Demonstrates how to describe a secret.
- **GetSecretValue** - Demonstrates how to get the value of a secret from AWS Secrets Manager.
- **ListSecrets** - Demonstrates how to list all of the secrets that are stored by Secrets Manager.
- **UpdateSecret** - Demonstrates how to update a secret for AWS Secrets Manager.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
