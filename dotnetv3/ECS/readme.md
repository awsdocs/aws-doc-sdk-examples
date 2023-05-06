# AWS SDK for .NET examples for Amazon Elastic Container Service (ECS)

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Elastic Container Service (ECS) to manage your containerized applications.

Amazon Elastic Container Service (ECS) is a fully-managed container orchestration service that makes it easy to run, stop, and manage Docker containers on a cluster. ECS eliminates the need to install, operate, and scale your own container management infrastructure. It provides a simple API and a console for managing Docker containers and clusters.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello ECS](Actions/HelloECS.cs)

### Single actions
Code excerpts that show you how to call individual service functions.

* [Get cluster arn](Actions/ECSWrapper.cs) (`GetClusterARNSAsync`)
* [Get service arn](Actions/ECSWrapper.cs) (`GetServiceARNSAsync`)
* [Get task arn](Actions/ECSWrapper.cs) (`GetTaskARNsAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get arn informations of cluster, service and task](Scenarios/ECSScenario.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

### Get started with listing arns

This interactive scenario runs at a command prompt and shows you how to use
Amazon Elastic Container Service (ECS) with the AWS SDK for .NET to deploy a .NET Core application to a cluster with a task definition and a service. The steps include:

1. List cluster arns.
2. List service arns.
3. List task arns.

Before you compile the .NET application, you can optionally set configuration values in the `settings.json` file.
These settings include your AWS account and region settings. Alternatively, add a `settings.local.json` file with your local settings, which will be loaded automatically 
when the application runs.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```
Alternatively, you can run the example from within your IDE.
mmand:



Alternatively, you can run the example from within your IDE.
## Additional resources
* [Amazon Elastic Container Service Developer Guide](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)
* [AWS SDK for .NET Amazon Elastic Container Service (ECS)](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/ECS/NECS.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
