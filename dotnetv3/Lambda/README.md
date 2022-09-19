# Lambda code examples for the SDK for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with AWS
Lambda to manage and invoke Lambda functions.

With AWS Lambda, you can run code without provisioning or managing servers. You
pay only for the compute time that you consume—there's no charge when your code
isn't running. You can run code for virtually any type of application or
backend service—all with zero administration.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Create a function](scenarios/Lambda_Basics/Lambda_Basics/LambdaMethods.cs) (`CreateFunctionAsync`)
- [Create a DynamoDB table](CreateDynamoDBTableExample/CreateDynamoDBTableExample/Function.cs) (`RunAsync`)
- [Create an S3 bucket](CreateDynamoDBTableExample/CreateDynamoDBTableExample/Function.cs) (`RunAsync`)
- [Delete a function](scenarios/Lambda_Basics/Lambda_Basics/LambdaMethods.cs) (`DeleteFunctionAsync`)
- [Get a function](scenarios/Lambda_Basics/Lambda_Basics/LambdaMethods.cs) (`GetFunctionAsync`)
- [Invoke a function](InvokeFunctionExample/InvokeFunctionExample/InvokeFunction.cs) (`InvokeAsync`)
- [List functions](ListFunctionsExample/ListFunctionsExample/ListFunctions.cs) (`ListFunctionsAsync`, `Paginators.ListFunctions`)
- [Update a function](scenarios/Lambda_Basics/Lambda_Basics/LambdaMethods.cs) (`UpdateFunctionCodeAsync`)
- [Set up role](SetuplambdaRoleExample/SetuplambdaRoleExample/SetupLambdaRole.cs)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

- [Get started with functions](/scenarios/Lambda_Basics/Lambda_Basics/LambdaMethods.cs)

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
* [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
* [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
* [AWS SDK for .NET Lambda](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Lambda/NLambda.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
