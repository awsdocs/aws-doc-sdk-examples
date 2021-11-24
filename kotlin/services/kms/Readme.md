# AWS KMS Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Key Management Service (AWS KMS).

## Running the AWS KMS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an alias. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateAlias** - Demonstrates how to create an AWS KMS alias.
- **CreateCustomerKey** - Demonstrates how to create an AWS KMS key.
- **CreateGrant** - Demonstrates how to add a grant to an AWS KMS key.
- **DeleteAlias** - Demonstrates how to delete an AWS KMS alias.
- **DescribeKey** - Demonstrates how to obtain information about an AWS KMS key.
- **DisableCustomerKey** - Demonstrates how to disable an AWS KMS key.
- **EnableCustomerKey** - Demonstrates how to enable an AWS KMS key.
- **EncryptDataKey** - Demonstrates how to encrypt and decrypt data by using an AWS KMS key.
- **ListAliases** - Demonstrates how to get a list of AWS KMS aliases.
- **ListGrants** - Demonstrates how to get information about AWS KMS grants related to a key.
- **ListKeys** -  Demonstrates how to get a list of AWS KMS keys.
- **RevokeGrant** - Demonstrates how to revoke a grant for the specified AWS KMS key.
 
To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
