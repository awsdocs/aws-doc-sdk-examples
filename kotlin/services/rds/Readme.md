# Amazon RDS Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Relational Database Service (Amazon RDS).

## Running the Amazon RDS Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a RDS instance. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateDBInstance** - Demonstrates how to create an Amazon RDS instance.
- **CreateDBSnapshot** - Demonstrates how to create an Amazon RDS snapshot.
- **DeleteDBInstance** - Demonstrates how to delete an Amazon RDS snapshot.
- **DescribeAccountAttributes** - Demonstrates how to retrieve attributes that belong to an Amazon RDS account.
- **DescribeDBInstances** - Demonstrates how to describe Amazon RDS instances.
- **ModifyDBInstance** - Demonstrates how to modify an Amazon RDS instance.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
