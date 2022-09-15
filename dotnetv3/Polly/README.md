# Amazon Polly code examples for the SDK for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with Amazon Polly to create applications
that convert text to speech using a number of different voices and languages.

Amazon Polly is a service that turns text into lifelike speech, allowing you to create applications that talk, and build entirely new categories of speech-enabled products.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Delete a lexicon](DeleteLexiconExample/DeleteLexicon.cs) (`DeleteLexiconAsync`)
- [Get a lexicon](GetLexiconExample/GetLexicon.cs) (`GetLexiconAsync`)
- [Get voices available for synthesis](DescribeVoicesExample/DescribeVoices.cs) (`DescribeVoicesAsync`)
- [List pronunciation lexicons](ListLexiconsExample/ListLexicons.cs) (`ListLexiconsAsync`)
- [Store a pronunciation lexicon](PutLexiconExample/PutLexicon.cs) (`PutLexiconAsync`)
- [Synthesize speech from text](SynthesizeSpeechExample/SynthesizeSpeech.cs) (`SynthesizeSpeechAsync`)
- [Synthesize speech from text using marks](SynthesizeSpeechMarksExample/SynthesizeSpeechMarks.cs) (`SynthesizeSpeechAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon Polly Developer Guide](https://docs.aws.amazon.com/polly/latest/dg/what-is.html)
* [Amazon Polly API Reference](https://docs.aws.amazon.com/polly/latest/dg/API_Reference.html)
* [AWS SDK for .NET Amazon Polly](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Polly/NPolly.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
