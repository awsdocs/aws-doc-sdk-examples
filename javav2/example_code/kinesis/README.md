# Kinesis code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Kinesis.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Kinesis](src/main/java/com/example/kinesis/HelloKinesis.java#L15) (`ListStreams`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/kinesis/basics/KinesisScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddTagsToStream](src/main/java/com/example/kinesis/basics/KinesisActions.java#L376)
- [CreateStream](src/main/java/com/example/kinesis/CreateDataStream.java#L6)
- [DeleteStream](src/main/java/com/example/kinesis/DeleteDataStream.java#L6)
- [DeregisterStreamConsumer](src/main/java/com/example/kinesis/basics/KinesisActions.java#L516)
- [DescribeStreamSummary](src/main/java/com/example/kinesis/basics/KinesisActions.java#L202)
- [GetRecords](src/main/java/com/example/kinesis/GetRecords.java#L6)
- [GetShardIterator](src/main/java/com/example/kinesis/basics/KinesisActions.java#L326)
- [ListShards](src/main/java/com/example/kinesis/basics/KinesisActions.java#L227)
- [ListStreams](src/main/java/com/example/kinesis/basics/KinesisActions.java#L172)
- [ListTagsForStream](src/main/java/com/example/kinesis/basics/KinesisActions.java#L403)
- [PutRecord](src/main/java/com/example/kinesis/StockTradesWriter.java#L6)
- [PutRecords](src/main/java/com/example/kinesis/basics/KinesisActions.java#L298)
- [RegisterStreamConsumer](src/main/java/com/example/kinesis/basics/KinesisActions.java#L469)
- [RemoveTagsFromStream](src/main/java/com/example/kinesis/basics/KinesisActions.java#L442)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Kinesis

This example shows you how to get started using Kinesis.


#### Learn the basics

This example shows you how to do the following:

- Create a stream and wait for it to become ACTIVE.
- List streams and verify creation.
- Describe stream summary to get metadata.
- List shards in the stream.
- Batch put records using PutRecords.
- Get a shard iterator and read records.
- Add tags to the stream.
- List tags for the stream.
- Remove tags from the stream.
- Register an enhanced fan-out consumer.
- Deregister the consumer.
- Clean up all resources.

<!--custom.basic_prereqs.kinesis_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.kinesis_Scenario_GettingStarted.end-->


<!--custom.basics.kinesis_Scenario_GettingStarted.start-->
<!--custom.basics.kinesis_Scenario_GettingStarted.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Kinesis Developer Guide](https://docs.aws.amazon.com/streams/latest/dev/introduction.html)
- [Kinesis API Reference](https://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Kinesis reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/kinesis/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
