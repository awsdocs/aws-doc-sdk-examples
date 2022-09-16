# Amazon Translate code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon Translate.

Amazon Translate is a neural machine translation service for translating text to and from English across a breadth of supported languages.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
- [Start a translation job](BatchTranslateExample/BatchTranslate.cs) (`StartTextTranslationJobAsync`)
- [Describe a translation job](DescribeTextTranslationExample/DescribeTextTranslation.cs) (`DescribeTextTranslationJobAsync`)
- [List translation jobs](ListTranslationJobsExample/ListTranslationJobs.cs) (`ListTextTranslationJobsAsync`)
- [Stop a translation job](StopTextTranslationJobExample/StopTextTranslationJob.cs) (`StopTextTranslationJobAsync`)
- [Translate text](TranslateTextExample/TranslateText.cs) (`TranslateTextAsync`)

### Cross-service examples
Sample applications that work across multiple AWS services.
- [Build a publish and subscription application that translates messages](../cross-service/SubscribePublishTranslate/SubscribePublishTranslate/Program.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then
issue the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon Translate Developer Guide](https://docs.aws.amazon.com/translate/latest/dg/index.html)
* [Amazon Translate API Reference](https://docs.aws.amazon.com/translate/latest/APIReference/index.html)
* [AWS SDK for .NET Amazon Translate](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Translate/NTranslate.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0