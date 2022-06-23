# AWS CloudTrail Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS CloudTrail.

## Running the AWS CloudTrail Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a trail. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateTrail** - Demonstrates how to create a trail..
- **DeleteTrail** - Demonstrates how to delete a trail.
- **DescribeTrails** - Demonstrates how to look up information about a trail.
- **GetEventSelectors** - Demonstrates how to get event selectors for a given trail.
- **LookupEvents** - Demonstrates how to look up Cloud Trail events.
- **PutEventSelectors** - Demonstrates how to configure an event selector for your trail.
- **StartLogging** - Demonstrates how to start and stop logging.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
