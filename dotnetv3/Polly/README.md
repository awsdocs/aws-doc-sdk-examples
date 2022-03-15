# Amazon Polly code examples for .NET

## Overview

The examples in this folder show how to use Amazon Polly to create applications
that convert text to speech using a number of different voices and languages.

## ⚠️ Important

- Running this code might result in charges to your AWS account. 
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
- This code is not tested in all AWS Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single action

- [DeleteLexiconExample](DeleteLexiconExample/) - Deletes an Amazon Polly lexicon. (`DeleteLexiconAsync`)
- [DescribeVoicesExample](DescribeVoicesExample/) - Describes the voices available for Amazon Polly. (`DescribeVoicesAsync`)
- [GetLexiconExample](GetLexiconExample/) - Gets information about an Amazon Polly lexicon. (`GetLexiconAsync`)
- [ListLexiconsExample](ListLexiconsExample/) - Lists the Amazon Polly lexicons available. (`ListLexiconsAsync`)
- [PutLexiconExample](PutLexiconExample/) - Adds a new Amazon Polly lexicon to an account. (`PutLexiconAsync`)
- [SynthesizeSpeech](SynthesizeSpeechExample/) - Converts text to speech and saves the results in a file. (`SynthesizeSpeechAsync`)
- [SynthesizeSpeechMarksExample](SynthesizeSpeechMarksExample/) - Synthesizes speech using speech marks. (`SynthesizeSpeechAsync`)

## Running the examples

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file, and then run the
following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

