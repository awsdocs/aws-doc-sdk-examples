# Amazon Kinesis .NET code examples

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET 3.x to
get started working with Amazon Kinesis features such as streams and consumers.

With Kinesis, you can ingest real-time data such as video, audio, application
logs, website clickstreams, and IoT telemetry data for machine learning,
analytics, and other applications. Kinesis enables you to process and
analyze data as it arrives and respond instantly instead of having to wait
until all your data is collected before the processing can begin.

## Code examples

- [CreateStreamExample](CreateStreamExample/) - Create a new Kinesis stream.
- [DeleteStreamExample](DeleteStreamExample/) - Delete an existing Kinesis stream.
- [DeregisterConsumerExample](DeregisterConsumerExample/) - Deregister consumer from Kinesis stream.
- [ListConsumersExample](ListConsumersExample/) - List the consumers of a Kinesis stream.
- [ListStreamsExample](ListStreamsExample/) - List Kinesis data streams.
- [ListTagsExample](ListTagsExample/) - List the tags associated with a Kinesis stream.
- [RegisterConsumerExample](RegisterConsumerExample/) - Register a consumer to a Kinesis stream.
- [TagStreamExample](TagStreamExample/) - Add tags to a Kinesis stream.

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management (IAM) User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account.

## Running the examples

The examples in this folder use the default user account. The call to
initialize the Kinesis client does not specify the AWS Region. Supply
the AWS Region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonKinesisClient(Amazon.RegionEndpoint.USWest2);
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information
[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
