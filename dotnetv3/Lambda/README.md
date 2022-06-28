# Lambda code examples for the AWS SDK for .NET v3

## Overview

This folder contains examples for using AWS Lambda using the AWS SDK for .NET v3.

AWS Lambda code to run virtually any type of application or backend service without provisioning or managing servers.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to
  perform the task. For more information, see Grant least privilege.
- This code is not tested in every AWS Region. For more information, see AWS Regional Services.

## Code examples

### Single actions

- [Creating a function to create an Amazon DynamoDB table](CreateDynamoDBTableExample/)
- [Creating a function to create a Amazon Simple Storage Service (Amazon S3) bucket](CreateS3BucktLambdaExample/)
- [Invoking a function](InvokeFunctionExample/)
- [Listing functions](ListFunctionsExample/)
- [Setting up a lambda](SetuplambdaRoleExample/)

## Running the examples

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
