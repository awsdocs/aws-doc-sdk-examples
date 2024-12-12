# AWS SDK for .NET 4.x documentation examples

## Overview
The code examples in this topic show you how to use the AWS SDK for .NET 4.x with AWS.

The AWS SDK for .NET 4.x provides a .NET API for AWS infrastructure services. Using the SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, and more.

## Types of code examples
* **Single-service actions** - Code examples that show you how to call individual service functions.

* **Single-service scenarios** - Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* **Cross-service examples** - Sample applications that work across multiple AWS services.

### Find code examples
Single-service actions and scenarios are organized by AWS service in the [*dotnetv4 folder*](/dotnetv4/). A README in each folder lists and describes how to run the examples.


## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

To build and run the code examples for the AWS SDK for .NET, you need the following:

- The appropriate [.NET SDK for .NET](https://dotnet.microsoft.com/en-us/download/visual-studio-sdks). Most examples use .NET 8, but some require .NET 6.

- The AWS SDK for .NET. For more information, see the [AWS SDK for .NET
Developer Guide](https://docs.aws.amazon.com/sdk-for-net/latest/developer-guide/welcome.html).

- AWS credentials, either configured in a local AWS credentials file, or by
setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
For more information, see the authentication topics in the [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/creds-idc.html) and the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/access.html).

## Building and running the code examples

To build and run a code example, 
follow the instructions in the README file for the service.
In general, follow these steps:

1. Navigate to the directory containing a **.sln** file.
2. Build the solution using ```dotnet build SOLUTION.sln```, 
   where SOLUTION.sln is the name of the **.sln** file.
3. Navigate to the directory containing the code example
   and a **.csproj** file.
4. Run the project using the ```dotnet run``` command.

## Linting
We rely on [dotnet-format](https://github.com/dotnet/format) to keep this code consistently formatted and styled.
To contribute .NET code to this project, please refer to the following installation and usage steps.

### Using dotnet-format
We run dotnet-format using [a custom configuration file](.editorconfig) against any changed file or directory. See the [.NET Github Action workflow](../.github/workflows/dotnet-check.yml) for details.

To invoke dotnet-format yourself, first install it with

```
dotnet tool install -g dotnet-format`.
```

Next, run the dotnet-format command in the directory of your solution or project:

```
dotnet format
```


## Tests
⚠️ Running the tests might result in charges to your AWS account.

Most service folders also include a test project and either integration tests, unit tests, or both. 
To run all the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

If you want more information, run:

```
dotnet test -l "console;verbosity=detailed"
```

To specify either unit or integration tests only, use the following category filters with the desired verbosity:
```
dotnet test --filter Category=Unit -l "console;verbosity=detailed"
```
or
```
dotnet test --filter Category=Integration -l "console;verbosity=detailed"
```

## Additional resources

* [*AWS SDK for .NET Version 3 API Reference*](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)
* [*AWS SDK for .NET Developer Guide*](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
