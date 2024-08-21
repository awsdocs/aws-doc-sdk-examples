# Kinesis code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Kinesis.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Kinesis makes it easy to collect, process, and analyze video and data streams in real time._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](zcl_aws1_kns_scenario.clas.abap)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateStream](zcl_aws1_kns_actions.clas.abap#L62)
- [DeleteStream](zcl_aws1_kns_actions.clas.abap#L88)
- [DescribeStream](zcl_aws1_kns_actions.clas.abap#L111)
- [GetRecords](zcl_aws1_kns_actions.clas.abap#L137)
- [ListStreams](zcl_aws1_kns_actions.clas.abap#L177)
- [PutRecord](zcl_aws1_kns_actions.clas.abap#L200)
- [RegisterStreamConsumer](zcl_aws1_kns_actions.clas.abap#L238)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a stream and put a record in it.
- Create a shard iterator.
- Read the record, then clean up resources.

<!--custom.basic_prereqs.kinesis_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.kinesis_Scenario_GettingStarted.end-->


<!--custom.basics.kinesis_Scenario_GettingStarted.start-->
<!--custom.basics.kinesis_Scenario_GettingStarted.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Kinesis Developer Guide](https://docs.aws.amazon.com/streams/latest/dev/introduction.html)
- [Kinesis API Reference](https://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP Kinesis reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/kns/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0