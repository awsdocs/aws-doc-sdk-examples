# Systems Manager code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS Systems Manager.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Systems Manager](hello.js#L4) (`ListDocuments`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/ssm-basics.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDocument](actions/create-document.js#L4)
- [CreateMaintenanceWindow](actions/create-maintenance-window.js#L4)
- [CreateOpsItem](actions/create-ops-item.js#L4)
- [DeleteDocument](actions/delete-document.js#L4)
- [DeleteMaintenanceWindow](actions/delete-maintenance-window.js#L4)
- [DescribeOpsItems](actions/describe-ops-items.js#L4)
- [ListCommandInvocations](actions/list-command-invocations.js#L4)
- [SendCommand](actions/send-command.js#L4)
- [UpdateMaintenanceWindow](actions/update-maintenance-window.js#L4)
- [UpdateOpsItem](actions/update-ops-item.js#L4)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Systems Manager

This example shows you how to get started using Systems Manager.

```bash
node ./hello.js
```

#### Learn the basics

This example shows you how to do the following:

- Create a maintenance window.
- Modify the maintenance window schedule.
- Create a document.
- Send a command to a specified EC2 instance.
- Create an OpsItem.
- Update and resolve the OpsItem.
- Delete the maintenance window, OpsItem, and document.

<!--custom.basic_prereqs.ssm_Scenario.start-->
<!--custom.basic_prereqs.ssm_Scenario.end-->


<!--custom.basics.ssm_Scenario.start-->
<!--custom.basics.ssm_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Systems Manager User Guide](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html)
- [Systems Manager API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) Systems Manager reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/ssm)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
