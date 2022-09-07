# AWS Lambda code examples for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with AWS Lambda to manage and invoke Lambda functions.

AWS Lambda is a serverless, event-driven compute service that lets you run code for virtually any type of application or backend service without provisioning or managing servers. You can trigger Lambda from over 200 AWS services and software as a service (SaaS) applications, and only pay for what you use.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Create DynamoDB Table](CreateDynamoDBTableExample/CreateDynamoDBTableExample/Function.cs) (`RunAsync`)
- [Create an S3 Bucket](CreateDynamoDBTableExample/CreateDynamoDBTableExample/Function.cs) (`RunAsync`)
- [Invoke a function](InvokeFunctionExample/InvokeFunctionExample/InvokeFunction.cs) (`CreateFunctionAsync`)
- [List functions](ListFunctionsExample/ListFunctionsExample/ListFunctions.cs) (`ListFunctionsAsync`)
- [Set up Role](SetuplambdaRoleExample/SetuplambdaRoleExample/SetupLambdaRole.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

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
