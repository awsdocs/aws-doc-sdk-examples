# Amazon Comprehend code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Comprehend to manage custom metrics and alarms.

Amazon Comprehend is a monitoring and observability service built for DevOps engineers, developers, site reliability engineers (SREs), IT managers, and product owners.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Detect the dominant language in a document](DetectDominantLanguageExample/DetectDominantLanguageExample/DetectDominantLanguage.cs) (`DetectDominantLanguageAsync`)
* [Detect entities in a document](DetectEntitiesExample/DetectEntitiesExample/DetectEntities.cs) (`DetectEntitiesAsync`)
* [Detect personally identifiable information in a document](DetectingPIIExample/DetectingPIIExample/DetectingPII.cs) (`DetectPiiEntitiesAsync`)
* [Detect syntactical elements of a document](DetectingSyntaxExample/DetectingSyntaxExample/DetectingSyntax.cs) (`DetectSyntaxAsync`)
* [Detect key phrases in a document](DetectKeyPhraseExample/DetectKeyPhraseExample/DetectKeyPhrase.cs) (`DetectKeyPhrasesAsync`)
* [Detect the sentiment of a document](DetectSentimentExample/DetectSentimentExample/DetectSentiment.cs) (`DetectSentimentAsync`)
* [Start a topic modeling job](TopicModelingExample/TopicModelingExample/TopicModeling.cs) (`StartTopicsDetectionJobAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);
```

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon Comprehend Developer Guide](https://docs.aws.amazon.com/comprehend/latest/dg/index.html)
* [Amazon Comprehend API Reference](https://docs.aws.amazon.com/comprehend/latest/dg/API_Reference.html)
* [AWS SDK for .NET Amazon Comprehend](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Comprehend/NComprehend.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0