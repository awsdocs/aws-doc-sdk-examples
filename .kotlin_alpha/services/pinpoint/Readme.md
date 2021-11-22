# Amazon Pinpoint Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Pinpoint.

## Running the Amazon Pinpoint Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon Pinpoint application. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddExampleEndpoint** - Demonstrates how to update an existing endpoint.
- **CreateApp** - Demonstrates how to create an Amazon Pinpoint application.
- **CreateCampaign** - Demonstrates how to create an Amazon Pinpoint campaign.
- **CreateEndpoint** - Demonstrates how to create an endpoint for an application in Amazon Pinpoint.
- **CreateSegment** - Demonstrates how to create a segment for a campaign in Amazon Pinpoint.
- **DeleteApp** -  Demonstrates how to delete an Amazon Pinpoint application.
- **DeleteEndpoint** - Demonstrates how to delete an endpoint.
- **ListEndpointIds** - Demonstrates how to retrieve information about all the endpoints that are associated with a specific user ID.
- **ListSegments**  - Demonstrates how to list segments in an Amazon Pinpoint application.
- **LookUpEndpoint** - Demonstrates how to display information about an existing endpoint in Amazon Pinpoint.
- **SendEmailMessage** - Demonstrates how to send an email message.
- **SendMessage** - Demonstrates how to send an SMS message using Amazon Pinpoint.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
