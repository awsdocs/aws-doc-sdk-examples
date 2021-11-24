# Amazon Route 53 Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Route 53.

## Running the Amazon Route 53 Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a hosted zone. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateHealthCheck** - Demonstrates how to create a new health check.
- **CreateHostedZone** - Demonstrates how to create a hosted zone.
- **DeleteHealthCheck** - Demonstrates how to delete a health check.
- **DeleteHostedZone** - Demonstrates how to delete a hosted zone.
- **GetHealthCheckStatus** - Demonstrates how to get the status of a specific health check.
- **ListHealthChecks** - Demonstrates how to list health checks.
- **ListHostedZones** - Demonstrates how to list hosted zones.
- **UpdateHealthCheck** - Demonstrates how to update a health check.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
