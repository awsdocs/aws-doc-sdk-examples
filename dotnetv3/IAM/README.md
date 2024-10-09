# IAM code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

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

- [Hello IAM](Actions/HelloIAM.cs#L4) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/IAMBasics/IAMBasics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddUserToGroup](Actions/IAMWrapper.cs#L22)
- [AttachRolePolicy](Actions/IAMWrapper.cs#L42)
- [CreateAccessKey](Actions/IAMWrapper.cs#L62)
- [CreateGroup](Actions/IAMWrapper.cs#L82)
- [CreateInstanceProfile](../cross-service/ResilientService/AutoScalerActions/AutoScalerWrapper.cs#L86)
- [CreatePolicy](Actions/IAMWrapper.cs#L96)
- [CreateRole](Actions/IAMWrapper.cs#L116)
- [CreateServiceLinkedRole](Actions/IAMWrapper.cs#L138)
- [CreateUser](Actions/IAMWrapper.cs#L159)
- [DeleteAccessKey](Actions/IAMWrapper.cs#L173)
- [DeleteGroup](Actions/IAMWrapper.cs#L194)
- [DeleteGroupPolicy](Actions/IAMWrapper.cs#L208)
- [DeleteInstanceProfile](../cross-service/ResilientService/AutoScalerActions/AutoScalerWrapper.cs#L504)
- [DeletePolicy](Actions/IAMWrapper.cs#L230)
- [DeleteRole](Actions/IAMWrapper.cs#L245)
- [DeleteRolePolicy](Actions/IAMWrapper.cs#L259)
- [DeleteUser](Actions/IAMWrapper.cs#L279)
- [DeleteUserPolicy](Actions/IAMWrapper.cs#L294)
- [DetachRolePolicy](Actions/IAMWrapper.cs#L310)
- [GetAccountPasswordPolicy](Actions/IAMWrapper.cs#L330)
- [GetPolicy](Actions/IAMWrapper.cs#L343)
- [GetRole](Actions/IAMWrapper.cs#L358)
- [GetUser](Actions/IAMWrapper.cs#L377)
- [ListAttachedRolePolicies](Actions/IAMWrapper.cs#L391)
- [ListGroups](Actions/IAMWrapper.cs#L412)
- [ListPolicies](Actions/IAMWrapper.cs#L432)
- [ListRolePolicies](Actions/IAMWrapper.cs#L452)
- [ListRoles](Actions/IAMWrapper.cs#L473)
- [ListSAMLProviders](Actions/IAMWrapper.cs#L493)
- [ListUsers](Actions/IAMWrapper.cs#L506)
- [PutGroupPolicy](Actions/IAMWrapper.cs#L548)
- [PutRolePolicy](Actions/IAMWrapper.cs#L571)
- [RemoveUserFromGroup](Actions/IAMWrapper.cs#L526)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../cross-service/ResilientService/ResilientServiceWorkflow/ResilientServiceWorkflow.cs)
- [Create a group and add a user](Scenarios/IamScenariosCommon/S3Wrapper.cs)


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
To run the examples, see the [README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.


#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


<!--custom.basics.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basics.iam_Scenario_CreateUserAssumeRole.end-->


#### Build and manage a resilient service

This example shows you how to create a load-balanced web service that returns book, movie, and song recommendations. The example shows how the service responds to failures, and how to restructure the service for more resilience when failures occur.

- Use an Amazon EC2 Auto Scaling group to create Amazon Elastic Compute Cloud (Amazon EC2) instances based on a launch template and to keep the number of instances in a specified range.
- Handle and distribute HTTP requests with Elastic Load Balancing.
- Monitor the health of instances in an Auto Scaling group and forward requests only to healthy instances.
- Run a Python web server on each EC2 instance to handle HTTP requests. The web server responds with recommendations and health checks.
- Simulate a recommendation service with an Amazon DynamoDB table.
- Control web server response to requests and health checks by updating AWS Systems Manager parameters.

<!--custom.scenario_prereqs.cross_ResilientService.start-->
<!--custom.scenario_prereqs.cross_ResilientService.end-->


<!--custom.scenarios.cross_ResilientService.start-->
<!--custom.scenarios.cross_ResilientService.end-->

#### Create a group and add a user

This example shows you how to do the following:

- Create a group and grant full Amazon S3 access permissions to it.
- Create a new user with no permissions to access Amazon S3.
- Add the user to the group and show that they now have permissions for Amazon S3, then clean up resources.

<!--custom.scenario_prereqs.iam_Scenario_GroupBasics.start-->
<!--custom.scenario_prereqs.iam_Scenario_GroupBasics.end-->


<!--custom.scenarios.iam_Scenario_GroupBasics.start-->
<!--custom.scenarios.iam_Scenario_GroupBasics.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for .NET IAM reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/IAM/NIAM.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0