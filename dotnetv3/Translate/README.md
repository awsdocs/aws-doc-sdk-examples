# Amazon Translate code examples for .NET

## Purpose

The following examples show how to use the Amazon Translate service to build real-time and batch translation capabilities in your applications.

## Code examples

- [BatchTranslateExample](BatchTranslateExample/) (`StartTextTranslationJobAsync`)
- [DescribeTextTranslationExample](DescribeTextTranslationExample/) (`DescribeTextTranslationJobAsync`)
- [ListTranslationJobsExample](ListTranslationJobsExample/) (`ListTextTranslationJobsAsync`)
- [StopTextTranslationJobExample](StopTextTranslationJobExample/) (`StopTextTranslationJobAsync`)
- [TranslateTextExample](TranslateTextExample/) (`TranslateTextAsync`)


## ⚠️ Important

- We recommend that grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

The examples in this folder use the default user account. The call to
initialize the Amazon SQS client supplies the region. Change the region to
match your own before running the example.

Once the example has been compiled, you can run it from the commandline by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Resources and documentation

[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

