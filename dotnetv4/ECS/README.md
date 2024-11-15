# Amazon ECS code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Elastic Container Service (Amazon ECS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon ECS is a highly scalable, fast, container management service that makes it easy to run, stop, and manage Docker containers on a cluster of Amazon EC2 instances._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon ECS](ECSActions/HelloECS.cs#L4) (`ListClusters`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [ListClusters](ECSActions/ECSWrapper.cs#L28)
- [ListServices](ECSActions/ECSWrapper.cs#L68)
- [ListTasks](ECSActions/ECSWrapper.cs#L102)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get ARN information for clusters, services, and tasks](ECSScenario/ECSScenario.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon ECS

This example shows you how to get started using Amazon ECS.



#### Get ARN information for clusters, services, and tasks

This example shows you how to do the following:

- Get a list of all clusters.
- Get services for a cluster.
- Get tasks for a cluster.

<!--custom.scenario_prereqs.ecs_Scenario_GetClustersServicesAndTasks.start-->
<!--custom.scenario_prereqs.ecs_Scenario_GetClustersServicesAndTasks.end-->


<!--custom.scenarios.ecs_Scenario_GetClustersServicesAndTasks.start-->
<!--custom.scenarios.ecs_Scenario_GetClustersServicesAndTasks.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon ECS Developer Guide](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)
- [Amazon ECS API Reference](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon ECS reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/ECS/NECS.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0