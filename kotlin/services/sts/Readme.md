# AWS STS Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Security Token Service (AWS STS).

## Running the AWS STS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

You will find these examples: 

- **AssumeRole** - Demonstrates how to obtain a set of temporary security credentials by using AWS STS.
- **GetAccessKeyInfo** - Demonstrates how to return the account identifier for the specified access key ID by using AWS STS.
- **GetCallerIdentity** - Demonstrates how to obtain details about the IAM user whose credentials are used to call the operation.
- **GetSessionToken** - Demonstrates how to return a set of temporary credentials.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
