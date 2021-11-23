# AWS Lambda Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Lambda.

## Running the AWS Lambda Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a delete a Lambda function. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateFunction** - Demonstrates how to create an AWS Lambda function.
- **DeleteFunction** - Demonstrates how to delete an AWS Lambda function.
- **GetAccountSettings** - Demonstrates how to get AWS Lambda account settings.
- **LambdaInvoke** - Demonstrates how to invoke an AWS Lambda function.
- **ListLambda** - Demonstrates how to list your AWS Lambda functions.
 
To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
