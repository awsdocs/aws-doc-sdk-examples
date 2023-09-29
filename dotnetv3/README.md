# AWS SDK for .NET 3.x documentation examples

## Overview
The code examples in this topic show you how to use the AWS SDK for .NET 3.x with AWS.

The AWS SDK for .NET 3.x provides a .NET API for AWS infrastructure services. Using the SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, and more.

## Types of code examples
* **Single-service actions** - Code examples that show you how to call individual service functions.

* **Single-service scenarios** - Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* **Cross-service examples** - Sample applications that work across multiple AWS services.

### Find code examples
Single-service actions and scenarios are organized by AWS service in the [*dotnetv3 folder*](/dotnetv3/). A README in each folder lists and describes how to run the examples.

Cross-service examples are located in the [*cross-services folder*](/dotnetv3/cross-service/). A README in each folder describes how to run the example.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

To build and run the code examples for the AWS SDK for .NET, you need the following:

- The appropriate [.NET SDK for .NET](https://dotnet.microsoft.com/en-us/download/visual-studio-sdks). Most examples use .NET 6, but some require .NET 5.

- The AWS SDK for .NET. For more information, see the [AWS SDK for .NET
Developer Guide](https://docs.aws.amazon.com/sdk-for-net/latest/developer-guide/welcome.html).

- AWS credentials, either configured in a local AWS credentials file, or by
setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
For more information, see the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/overview.html).

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

## Docker image (Beta)
This example code will soon be available in a container image
hosted on [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html). This image will be pre-loaded 
with all .NET examples ready to build and run, so that you can explore
these examples in an isolated environment.

⚠️ As of February 2023, the [SDK for .NET v3 image](https://gallery.ecr.aws/b4v4v1s0/dotnetv3) is available on ECR Public but is still
undergoing active development. Refer to 
[this GitHub issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/4126) 
for more information. 

### Build the Docker image

1. Install and run Docker on your machine.
2. Navigate to the same directory as this README.
3. Run `docker build -t <image_name> .` where `image_name` is a name you provide for the image.

### Launch the Docker container

1. Run `docker run -it -v <your_credentials_folder_path>/.aws/credentials:/root/.aws/credentials <image_name>`. `-it` launches an
   interactive terminal. `-v <your_cred...` is optional but recommended. It will mount your local credentials
   file to the container.
2. The terminal initiates a bash instance at the root of the container. Run `cd dotnetv3`. Then, you
   can run examples and tests by navigating to a service folder and following the README instructions there. 
   For example, navigate to the `dotnetv3/Route53/Scenarios` folder and execute the `dotnet run` command to build and run an interactive scenario for Amazon Route 53.

## Additional resources

* [*AWS SDK for .NET Version 3 API Reference*](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)
* [*AWS SDK for .NET Developer Guide*](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
