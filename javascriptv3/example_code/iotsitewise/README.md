# AWS IoT SiteWise code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS IoT SiteWise.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT SiteWise _

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

- [Hello AWS IoT SiteWise](hello.js#L4) (`ListAssetModels`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/iotsitewise-basics.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchPutAssetPropertyValue](actions/batch-put-asset-property-value.js#L4)
- [CreateAsset](actions/create-asset.js#L4)
- [CreateAssetModel](actions/create-asset-model.js#L4)
- [CreateGateway](actions/create-gateway.js#L4)
- [CreatePortal](actions/create-portal.js#L4)
- [DeleteAsset](actions/delete-asset.js#L4)
- [DeleteAssetModel](actions/delete-asset-model.js#L4)
- [DeleteGateway](actions/delete-gateway.js#L4)
- [DeletePortal](actions/delete-portal.js#L4)
- [DescribeAssetModel](actions/describe-asset-model.js#L4)
- [DescribeGateway](actions/describe-gateway.js#L4)
- [DescribePortal](actions/describe-portal.js#L4)
- [GetAssetPropertyValue](actions/get-asset-property-value.js#L4)
- [ListAssetModels](actions/list-asset-models.js#L4)


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

#### Hello AWS IoT SiteWise

This example shows you how to get started using AWS IoT SiteWise.

```bash
node ./hello.js
```

#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT SiteWise Asset Model.
- Create an AWS IoT SiteWise Asset.
- Retrieve the property ID values.
- Send data to an AWS IoT SiteWise Asset.
- Retrieve the value of the AWS IoT SiteWise Asset property.
- Create an AWS IoT SiteWise Portal.
- Create an AWS IoT SiteWise Gateway.
- Describe the AWS IoT SiteWise Gateway.
- Delete the AWS IoT SiteWise Assets.

<!--custom.basic_prereqs.iotsitewise_Scenario.start-->
<!--custom.basic_prereqs.iotsitewise_Scenario.end-->


<!--custom.basics.iotsitewise_Scenario.start-->
<!--custom.basics.iotsitewise_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT SiteWise Developer Guide](https://docs.aws.amazon.com/iot-sitewise/latest/userguide/what-is-sitewise.html)
- [AWS IoT SiteWise API Reference](https://docs.aws.amazon.com/iot-sitewise/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) AWS IoT SiteWise reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/iotsitewise)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
