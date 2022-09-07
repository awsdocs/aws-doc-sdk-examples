# AWS STS code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Security Token Service (AWS STS).

AWS STS is a web service that enables you to request temporary, limited-privilege credentials for AWS Identity and Access Management (IAM) users or for users you authenticate (federated users).

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
- [Assume a role](AssumeRole/AssumeRoleExample/AssumeRole.cs) (`AssumeRoleAsync`)

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

## Tests
⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test Runner to run the tests.

## Additional resources
* [AWS STS API Reference](https://docs.aws.amazon.com/STS/latest/APIReference/index.html)
* [AWS SDK for .NET AWS STS](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SecurityToken/NSecurityToken.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0