# AWS Support code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with AWS Support.

AWS Support offers a range of plans that provide access to tools and expertise that support the success and operational health of your AWS solutions.

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello AWS Support](Actions/HelloSupport.cs)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Add an attachment to a set](Actions/SupportWrapper.cs)(`AddAttachmentsToSetAsync`)
* [Add a communication to a case](Actions/SupportWrapper.cs)(`AddCommunicationToCaseAsync`)
* [Create a support case](Actions/SupportWrapper.cs)(`CreateCaseAsync`)
* [Describe an attachment](Actions/SupportWrapper.cs)(`DescribeAttachmentAsync`)
* [Describe cases](Actions/SupportWrapper.cs)(`DescribeCasesAsync`)
* [Describe communications](Actions/SupportWrapper.cs)(`DescribeCommunicationsAsync`)
* [Describe services](Actions/SupportWrapper.cs)(`DescribeServicesAsync`)
* [Describe severity levels](Actions/SupportWrapper.cs)(`DescribeSeverityLevelsAsync`)
* [Resolve a support case](Actions/SupportWrapper.cs)(`ResolveCaseAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with AWS Support cases](Scenarios/SupportCaseScenario.cs)

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

## Tests

⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test Runner to run the tests.

## Additional resources
* [AWS Support User Guide](https://docs.aws.amazon.com/awssupport/latest/user/index.html)
* [AWS Support API Reference](https://docs.aws.amazon.com/awssupport/latest/APIReference/index.html)
* [AWS SDK for .NET AWS Support](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/AWSSupport/NAWSSupport.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

