# Amazon Athena Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Athena.

## Running the Athena Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a named query example. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateNamedQueryExample** - Demonstrates how to create a named query.
- **DeleteNamedQueryExample** - Demonstrates how to delete a named query by using the named query Id value.
- **ListNamedQueryExample** - Demonstrates how to obtain a list of named query Id values.
- **ListQueryExecutionsExample** - Demonstrates how to obtain a list of query execution Id values.
- **StartQueryExample** - Demonstrates how to submit a query to Amazon Athena for execution, wait until the results are available, and then process the results.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 


