# Systems Manager code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Systems Manager.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Systems Manager organizes, monitors, and automates management tasks on your AWS resources._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Systems Manager](src/main/java/com/example/ssm/HelloSSM.java#L6) (`listThings`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDocument](src/main/java/com/example/scenario/SSMActions.java#L481)
- [CreateMaintenanceWindow](src/main/java/com/example/scenario/SSMActions.java#L569)
- [CreateOpsItem](src/main/java/com/example/scenario/SSMActions.java#L314)
- [DeleteDocument](src/main/java/com/example/scenario/SSMActions.java#L92)
- [DeleteMaintenanceWindow](src/main/java/com/example/scenario/SSMActions.java#L131)
- [DescribeOpsItems](src/main/java/com/example/scenario/SSMActions.java#L210)
- [DescribeParameters](src/main/java/com/example/ssm/GetParameter.java#L6)
- [PutParameter](src/main/java/com/example/ssm/PutParameter.java#L6)
- [SendCommand](src/main/java/com/example/scenario/SSMActions.java#L385)
- [UpdateMaintenanceWindow](src/main/java/com/example/scenario/SSMActions.java#L532)
- [UpdateOpsItem](src/main/java/com/example/scenario/SSMActions.java#L170)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with Systems Manager](src/main/java/com/example/scenario/SSMScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### Properties file

Before running the AWS Systems Manager JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a parameter name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **paraName** - A parameter name used in the **GetParameter** test.
- **source** - The origin of the OpsItem, such as Amazon EC2 or Systems Manager.
- **category** - A category to assign to an OpsItem.
- **severity** - A severity value to assign to an OpsItem.
- **title** - The OpsItem title.

<!--custom.instructions.end-->

#### Hello Systems Manager

This example shows you how to get started using Systems Manager.



#### Get started with Systems Manager

This example shows you how to work with Systems Manager maintenance windows, documents, and OpsItems.


<!--custom.scenario_prereqs.ssm_Scenario.start-->
<!--custom.scenario_prereqs.ssm_Scenario.end-->


<!--custom.scenarios.ssm_Scenario.start-->
<!--custom.scenarios.ssm_Scenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Systems Manager User Guide](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html)
- [Systems Manager API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Systems Manager reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ssm/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0