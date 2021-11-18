# AWS SDK for .NET Comprehend code examples in C#

## Purpose

The examples in this section how to use the AWS SDK for .NET with Amazon
Comprehend to inspect documents and discover information about them.

* Detect elements of a document, such as languages used, key phrases, and personally
identifiable information (PII).
* Detect common themes in a set of documents without the need for prior annotation.

## Code examples

- [DetectDominantLanguageExample](DetectDominantLanguageExampe/) - Examine the supplied text and determine the dominant language used.
- [DetectEntitiesExample](DetectEntitiesExample) - Examines the supplied text and identifies entities such as names of people, organizations, events, dates, products, places, quantities, and titles.
- [DetectingPIIExample](DetectingPIIExample/) - Examines the suppolied text and identifies personally identifiable information (PII) such as name, address, or credit card number.
- [DetectingSyntaxExample](DetectingSyntaxExample/) - Examines the supplied text and identifies parts of speech such as nouns, verbs, and other parts of speech.
- [DetectKeyPhrasesExample](DetectKeyPhrasesExample/) - Examines the supplied text and identifies Key Phrases included in the text. A key phrase is a string containing a noun phrase that describes a particular thing.
- [DetectSentimentExample](DetectSentimentExample/) - Examines the supplied text and determines the overall sentiment expressed by the text. Sentiment can be positive, neutral, negative, or mixed.
- [TopicModelingExample](TopicModelingExample/) - Examines the supplied text, looking for Topics.

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the examples

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);
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

