# Amazon Transcribe code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Transcribe.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Transcribe provides transcription services for your audio files and audio streams._

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

- [CreateVocabulary](Actions/TranscribeWrapper.Vocabularies.cs#L15)
- [DeleteMedicalTranscriptionJob](Actions/TranscribeWrapper.MedicalTranscriptions.cs#L93)
- [DeleteTranscriptionJob](Actions/TranscribeWrapper.cs#L99)
- [DeleteVocabulary](Actions/TranscribeWrapper.Vocabularies.cs#L59)
- [GetTranscriptionJob](Actions/TranscribeWrapper.cs#L61)
- [GetVocabulary](Actions/TranscribeWrapper.Vocabularies.cs#L40)
- [ListMedicalTranscriptionJobs](Actions/TranscribeWrapper.MedicalTranscriptions.cs#L73)
- [ListTranscriptionJobs](Actions/TranscribeWrapper.cs#L80)
- [ListVocabularies](Actions/TranscribeWrapper.Vocabularies.cs#L78)
- [StartMedicalTranscriptionJob](Actions/TranscribeWrapper.MedicalTranscriptions.cs#L15)
- [StartTranscriptionJob](Actions/TranscribeWrapper.cs#L26)
- [UpdateVocabulary](Actions/TranscribeWrapper.Vocabularies.cs#L101)


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



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Transcribe Developer Guide](https://docs.aws.amazon.com/transcribe/latest/dg/what-is.html)
- [Amazon Transcribe API Reference](https://docs.aws.amazon.com/transcribe/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon Transcribe reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/TranscribeService/NTranscribeService.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0