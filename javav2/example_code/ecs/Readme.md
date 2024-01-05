# Amazon ECS code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Elastic Container Service (Amazon ECS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon ECS is a highly scalable, fast, container management service that makes it easy to run, stop, and manage Docker containers on a cluster of Amazon EC2 instances._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a cluster](src/main/java/com/example/ecs/CreateCluster.java#L12) (`CreateCluster`)
- [Create a service](src/main/java/com/example/ecs/CreateService.java#L12) (`CreateService`)
- [Delete a service](src/main/java/com/example/ecs/DeleteService.java#L12) (`DeleteService`)
- [Describe clusters](src/main/java/com/example/ecs/DescribeClusters.java#L12) (`DescribeClusters`)
- [Describe tasks](src/main/java/com/example/ecs/ListTaskDefinitions.java#L11) (`DescribeTasks`)
- [List clusters](src/main/java/com/example/ecs/ListClusters.java#L11) (`ListClusters`)
- [Update a service](src/main/java/com/example/ecs/UpdateService.java#L11) (`UpdateService`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon ECS Developer Guide](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)
- [Amazon ECS API Reference](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon ECS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ecs/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0