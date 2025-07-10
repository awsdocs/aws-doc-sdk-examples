# AWS Systems Manager code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with AWS Systems Manager.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Systems Manager is a collection of capabilities for configuring and managing your Amazon EC2 instances, on-premises servers, and virtual machines._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

* [Hello AWS Systems Manager](hello/hello.go#L4) (`ListDocuments`)

### Basics

Code examples that show you how to perform the essential operations within a service.

* [Learn the basics](scenarios/ssm_basics.go)

### Single actions

Code excerpts that show you how to call individual service functions.

* [CancelCommand](actions/command_actions.go#L85)
* [CreateDocument](actions/document_actions.go#L55)
* [DeleteDocument](actions/document_actions.go#L85)
* [DeleteParameter](actions/parameter_actions.go#L95)
* [DeleteParameters](actions/parameter_actions.go#L109)
* [DescribeDocument](actions/document_actions.go#L35)
* [DescribeDocumentPermission](actions/document_actions.go#L115)
* [GetCommandInvocation](actions/command_actions.go#L55)
* [GetDocument](actions/document_actions.go#L45)
* [GetParameter](actions/parameter_actions.go#L35)
* [GetParameterHistory](actions/parameter_actions.go#L125)
* [GetParameters](actions/parameter_actions.go#L50)
* [GetParametersByPath](actions/parameter_actions.go#L71)
* [ListCommandInvocations](actions/command_actions.go#L70)
* [ListCommands](actions/command_actions.go#L35)
* [ListDocuments](actions/document_actions.go#L20)
* [ListDocumentVersions](actions/document_actions.go#L100)
* [PutParameter](actions/parameter_actions.go#L20)
* [SendCommand](actions/command_actions.go#L20)
* [UpdateDocument](actions/document_actions.go#L70)

<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Systems Manager

This example shows you how to get started using AWS Systems Manager.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```

#### Learn the basics

This example shows you how to do the following:

* Work with Parameter Store to put, get, and delete parameters.
* List and describe SSM documents.
* Send commands to EC2 instances using Run Command.
* Get command execution results and status.

<!--custom.basic_prereqs.ssm_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.ssm_Scenario_GettingStarted.end-->

<!--custom.basics.ssm_Scenario_GettingStarted.start-->
<!--custom.basics.ssm_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.

<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

* [AWS Systems Manager User Guide](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html)
* [AWS Systems Manager API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html)
* [SDK for Go V2 AWS Systems Manager reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/ssm)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
