# Amazon Comprehend Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Comprehend.

## Running the Amazon Comprehend Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **DetectEntities** - Demonstrates how to retrieve named entities from within specified text.
- **DetectKeyPhrases** - Demonstrates how to detect key phrases.
- **DetectLanguage** - Demonstrates how to detect the language of the text.
- **DetectSentiment** - Demonstrates how to detect sentiments in the text.
- **DetectSyntax** - Demonstrates how to detect syntax in the text.
- **DocumentClassifierDemo** - demonstrates how to train a custom classifier.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
