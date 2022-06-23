# Amazon Cognito Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Cognito.

## Running the Amazon Cognito Kotlin examples

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user pool. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateIdentityPool** - Demonstrates how to create a new Amazon Cognito identity pool.
- **CreateUser** - Demonstrates how to add a new user to your user pool.
- **CreateUserPool** - Demonstrates how to create a user pool for Amazon Cognito.
- **DeleteIdentityPool** - Demonstrates how to delete an existing Amazon Cognito identity pool.
- **DeleteUserPool** - Demonstrates how to delete an existing user pool.
- **DescribeUserPool**  - Demonstrates how to obtain information about an existing user pool.
- **ListIdentities** - Demonstrates how to list identities that belong to an Amazon Cognito identity pool.
- **ListIdentityPools** - Demonstrates how to list Amazon Cognito identity pools.
- **ListUserPoolClients** - Demonstrates how to list existing user pool clients that are available in the specified AWS Region in your current AWS account.
- **ListUserPools** - Demonstrates how to to list existing user pools in the given account.
- **ListUsers** - Demonstrates how to list users in the specified user pool.
- **SignUpUser** - Demonstrates how to register a user in the specified Amazon Cognito user pool.


To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
