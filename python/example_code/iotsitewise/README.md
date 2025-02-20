# AWS IoT SiteWise code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS IoT SiteWise.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS IoT SiteWise](hello/hello_iot_sitewise.py#L4) (`ListAssetModels`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](iotsitewise_getting_started.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchPutAssetPropertyValue](iotsitewise_wrapper.py#L155)
- [CreateAsset](iotsitewise_wrapper.py#L75)
- [CreateAssetModel](iotsitewise_wrapper.py#L41)
- [CreateGateway](iotsitewise_wrapper.py#L315)
- [CreatePortal](iotsitewise_wrapper.py#L259)
- [DeleteAsset](iotsitewise_wrapper.py#L415)
- [DeleteAssetModel](iotsitewise_wrapper.py#L434)
- [DeleteGateway](iotsitewise_wrapper.py#L371)
- [DeletePortal](iotsitewise_wrapper.py#L393)
- [DescribeGateway](iotsitewise_wrapper.py#L347)
- [DescribePortal](iotsitewise_wrapper.py#L315)
- [GetAssetPropertyValue](iotsitewise_wrapper.py#L229)
- [ListAssetModels](iotsitewise_wrapper.py#L105)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT SiteWise

This example shows you how to get started using AWS IoT SiteWise.

```
python hello/hello_iot_sitewise.py
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

Start the example by running the following at a command prompt:

```
python iotsitewise_getting_started.py
```


<!--custom.basics.iotsitewise_Scenario.start-->
<!--custom.basics.iotsitewise_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT SiteWise Developer Guide](https://docs.aws.amazon.com/iot-sitewise/latest/userguide/what-is-sitewise.html)
- [AWS IoT SiteWise API Reference](https://docs.aws.amazon.com/iot-sitewise/latest/APIReference/Welcome.html)
- [SDK for Python AWS IoT SiteWise reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iotsitewise.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
