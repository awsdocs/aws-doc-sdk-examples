# OpenSearch code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon OpenSearch Service.

<!--custom.overview.start-->
<!--custom.overview.end-->

_OpenSearch is a distributed, community-driven, Apache 2.0-licensed, 100% open-source search and analytics suite used for a broad set of use cases like real-time application monitoring, log analytics, and website search._

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

- [Hello OpenSearch](src/main/java/com/example/search/HelloOpenSearch.java#L6) (`ListVersions`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn OpenSearch core operations](src/main/java/com/example/search/scenario/OpenSearchScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddTags](src/main/java/com/example/search/scenario/OpenSearchActions.java#L265)
- [ChangeProgress](src/main/java/com/example/search/scenario/OpenSearchActions.java#L215)
- [CreateDomain](src/main/java/com/example/search/scenario/OpenSearchActions.java#L69)
- [DeleteDomain](src/main/java/com/example/search/scenario/OpenSearchActions.java#L114)
- [DescribeDomain](src/main/java/com/example/search/scenario/OpenSearchActions.java#L189)
- [ListDomainNames](src/main/java/com/example/search/scenario/OpenSearchActions.java#L167)
- [ListTags](src/main/java/com/example/search/scenario/OpenSearchActions.java#L265)
- [UpdateDomainConfig](src/main/java/com/example/search/scenario/OpenSearchActions.java#L189)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello OpenSearch

This example shows you how to get started using OpenSearch.


#### Learn OpenSearch core operations

This example shows you how to do the following:

- Create an OpenSearch domain.
- Provides detailed information about a specific OpenSearch domain.
- Lists all the OpenSearch domains owned by the account.
- Waits until the OpenSearch domain's change status reaches a completed state.
- Modifies the configuration of an existing OpenSearch domain.
- Add a tag to the OpenSearch domain.
- Lists the tags associated with an OpenSearch domain.
- Removes tags from an OpenSearch domain.
- Deletes the OpenSearch domain.

<!--custom.basic_prereqs.opensearch_Scenario.start-->
<!--custom.basic_prereqs.opensearch_Scenario.end-->


<!--custom.basics.opensearch_Scenario.start-->
<!--custom.basics.opensearch_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [OpenSearch User Guide](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/gsg.html)
- [OpenSearch API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)
- [SDK for Java 2.x OpenSearch reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/opensearch/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0