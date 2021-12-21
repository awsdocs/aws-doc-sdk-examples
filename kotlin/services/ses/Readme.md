# Amazon SES Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Simple Email Service (Amazon SES).

## Running the Amazon SES Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **ListIdentities** - Demonstrates how to obtain a list of identities for your AWS account.
- **SendMessageEmail** - Demonstrates how to send an email message.
- **SendMessageAttachment** - Demonstrates how to send an email message with an attachment.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
