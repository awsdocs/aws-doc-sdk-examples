# Amazon Comprehend code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Comprehend.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Comprehend uses natural language processing (NLP) to extract insights about the content of documents without the need of any special preprocessing._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [DetectDominantLanguage](DetectDominantLanguageExample/DetectDominantLanguageExample/DetectDominantLanguage.cs#L6)
- [DetectEntities](DetectEntitiesExample/DetectEntitiesExample/DetectEntities.cs#L6)
- [DetectKeyPhrases](DetectKeyPhraseExample/DetectKeyPhraseExample/DetectKeyPhrase.cs#L6)
- [DetectPiiEntities](DetectingPIIExample/DetectingPIIExample/DetectingPII.cs#L6)
- [DetectSentiment](DetectSentimentExample/DetectSentimentExample/DetectSentiment.cs#L6)
- [DetectSyntax](DetectingSyntaxExample/DetectingSyntaxExample/DetectingSyntax.cs#L6)
- [StartTopicsDetectionJob](TopicModelingExample/TopicModelingExample/TopicModeling.cs#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create an application to analyze customer feedback](../cross-service/FeedbackSentimentAnalyzer)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create an application to analyze customer feedback

This example shows you how to create an application that analyzes customer comment cards, translates them from their original language, determines their sentiment, and generates an audio file from the translated text.


<!--custom.scenario_prereqs.cross_FSA.start-->
<!--custom.scenario_prereqs.cross_FSA.end-->


<!--custom.scenarios.cross_FSA.start-->
<!--custom.scenarios.cross_FSA.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Comprehend Developer Guide](https://docs.aws.amazon.com/comprehend/latest/dg/what-is.html)
- [Amazon Comprehend API Reference](https://docs.aws.amazon.com/comprehend/latest/APIReference/welcome.html)
- [SDK for .NET Amazon Comprehend reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Comprehend/NComprehend.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0