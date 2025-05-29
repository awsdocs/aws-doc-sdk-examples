# Neptune code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Neptune.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Neptune is a serverless graph database designed for superior scalability and availability._

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

- [Hello Neptune](src/main/java/com/example/neptune/HelloNeptune.java#L14) (`DescribeDBClustersPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/neptune/scenerio/NeptuneScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDBCluster](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L443)
- [CreateDBInstance](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L404)
- [CreateDBSubnetGroup](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L478)
- [DeleteDBCluster](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L143)
- [DeleteDBInstance](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L168)
- [DeleteDBSubnetGroup](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L126)
- [DescribeDBClusters](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L294)
- [DescribeDBInstances](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L345)
- [StartDBCluster](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L262)
- [StopDBCluster](src/main/java/com/example/neptune/scenerio/NeptuneActions.java#L278)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Neptune

This example shows you how to get started using Neptune.


#### Learn the basics

This example shows you how to do the following:

- Create an Amazon Neptune Subnet Group.
- Create an Neptune Cluster.
- Create an Neptune Instance.
- Check the status of the Neptune Instance.
- Show Neptune cluster details.
- Stop the Neptune cluster.
- Start the Neptune cluster.
- Delete the Neptune Assets.

<!--custom.basic_prereqs.neptune_Scenario.start-->
<!--custom.basic_prereqs.neptune_Scenario.end-->


<!--custom.basics.neptune_Scenario.start-->
<!--custom.basics.neptune_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Neptune User Guide](https://docs.aws.amazon.com/neptune/latest/userguide/intro.html)
- [Neptune API Reference](https://docs.aws.amazon.com/neptune/latest/apiref/Welcome.html)
- [SDK for Java 2.x Neptune reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/neptune/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
