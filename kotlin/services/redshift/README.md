# Amazon Redshift code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Redshift.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Redshift is a fast, fully managed, petabyte-scale data warehouse service that makes it simple and cost-effective to efficiently analyze all your data using your existing business intelligence tools._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](src/main/kotlin/com/kotlin/redshift/CreateAndModifyCluster.kt#L56)
- [DeleteCluster](src/main/kotlin/com/kotlin/redshift/DeleteCluster.kt#L38)
- [DescribeClusters](src/main/kotlin/com/kotlin/redshift/DescribeClusters.kt#L22)
- [ModifyCluster](src/main/kotlin/com/kotlin/redshift/CreateAndModifyCluster.kt#L113)


<!--custom.examples.start-->

### Custom Examples

- **FindReservedNodeOffer** - Demonstrates how to find additional Amazon Redshift nodes for purchase.
- **ListEvents** - Demonstrates how to list events for a given Amazon Redshift cluster.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for Kotlin Amazon Redshift reference](https://sdk.amazonaws.com/kotlin/api/latest/redshift/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
