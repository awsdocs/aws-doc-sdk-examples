# AWS Glue Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Glue.

## Running the AWS Glue Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a crawler. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateCrawler** - Demonstrates how to create an AWS Glue crawler.
- **DeleteCrawler** - Demonstrates how to delete an AWS Glue crawler.
- **GetCrawler** - Demonstrates how to get an AWS Glue crawler.
- **GetCrawlers** - Demonstrates how to get AWS Glue crawlers.
- **GetDatabase** - Demonstrates how to get a database.
- **GetDatabases** - Demonstrates how to get databases.
- **GetJobRun** - Demonstrates how to get a job run request.
- **GetJobs** - Demonstrates how to list all AWS Glue jobs.
- **ListWorkflows** - Demonstrates how to list workflows.
- **SearchTables** - Demonstrates how to search a set of tables based on properties.
- **StartCrawler**  - Demonstrates how to start an AWS Glue crawler.
- **StopCrawler** - Demonstrates how to stop an AWS Glue crawler.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
