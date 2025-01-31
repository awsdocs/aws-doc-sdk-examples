# Amazon Translate code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Translate.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Translate is a neural machine translation service for translating text to and from English across a breadth of supported languages._

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

- [DescribeTextTranslationJob](DescribeTextTranslationExample/DescribeTextTranslation.cs#L6)
- [ListTextTranslationJobs](ListTranslationJobsExample/ListTranslationJobs.cs#L6)
- [StartTextTranslationJob](BatchTranslateExample/BatchTranslate.cs#L6)
- [StopTextTranslationJob](StopTextTranslationJobExample/StopTextTranslationJob.cs#L6)
- [TranslateText](TranslateTextExample/TranslateText.cs#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Building an Amazon SNS application](../cross-service/SubscribePublishTranslate)
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



#### Building an Amazon SNS application

This example shows you how to create an application that has subscription and publish functionality and translates messages.


<!--custom.scenario_prereqs.cross_SnsPublishSubscription.start-->
<!--custom.scenario_prereqs.cross_SnsPublishSubscription.end-->


<!--custom.scenarios.cross_SnsPublishSubscription.start-->
<!--custom.scenarios.cross_SnsPublishSubscription.end-->

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

- [Amazon Translate Developer Guide](https://docs.aws.amazon.com/translate/latest/dg/what-is.html)
- [Amazon Translate API Reference](https://docs.aws.amazon.com/translate/latest/APIReference/welcome.html)
- [SDK for .NET Amazon Translate reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Translate/NTranslate.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0