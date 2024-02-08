# Amazon Personalize code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Personalize.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Personalize enables real-time personalization and recommendations, based on the same technology used at Amazon.com._

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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a batch interface job](src/main/java/com/example/personalize/CreateBatchInferenceJob.java#L69) (`CreateBatchInferenceJob`)
- [Create a campaign](src/main/java/com/example/personalize/CreateCampaign.java#L54) (`CreateCampaign`)
- [Create a dataset](src/main/java/com/example/personalize/CreateDataset.java#L49) (`CreateDataset`)
- [Create a dataset export job](src/main/java/com/example/personalize/CreateDatasetExportJob.java#L66) (`CreateDatasetExportJob`)
- [Create a dataset group](src/main/java/com/example/personalize/CreateDatasetGroup.java#L49) (`CreateDatasetGroup`)
- [Create a dataset import job](src/main/java/com/example/personalize/CreateDatasetImportJob.java#L57) (`CreateDatasetImportJob`)
- [Create a domain schema](src/main/java/com/example/personalize/CreateDomainSchema.java#L54) (`CreateSchema`)
- [Create a filter](src/main/java/com/example/personalize/CreateFilter.java#L50) (`CreateFilter`)
- [Create a recommender](src/main/java/com/example/personalize/CreateRecommender.java#L60) (`CreateRecommender`)
- [Create a schema](src/main/java/com/example/personalize/CreateSchema.java#L52) (`CreateSchema`)
- [Create a solution](src/main/java/com/example/personalize/CreateSolution.java#L59) (`CreateSolution`)
- [Create a solution version](src/main/java/com/example/personalize/CreateSolutionVersion.java#L47) (`CreateSolutionVersion`)
- [Create an event tracker](src/main/java/com/example/personalize/CreateEventTracker.java#L50) (`CreateEventTracker`)
- [Delete a campaign](src/main/java/com/example/personalize/DeleteCampaign.java#L50) (`DeleteCampaign`)
- [Delete a solution](src/main/java/com/example/personalize/DeleteSolution.java#L49) (`DeleteSolution`)
- [Delete an event tracker](src/main/java/com/example/personalize/DeleteEventTracker.java#L26) (`DeleteEventTracker`)
- [Describe a campaign](src/main/java/com/example/personalize/DescribeCampaign.java#L52) (`DescribeCampaign`)
- [Describe a recipe](src/main/java/com/example/personalize/DescribeRecipe.java#L50) (`DescribeRecipe`)
- [Describe a solution](src/main/java/com/example/personalize/DescribeSolution.java#L51) (`DescribeSolution`)
- [List campaigns](src/main/java/com/example/personalize/ListCampaigns.java#L53) (`ListCampaigns`)
- [List dataset groups](src/main/java/com/example/personalize/ListDatasetGroups.java#L36) (`ListDatasetGroups`)
- [List recipes](src/main/java/com/example/personalize/ListRecipes.java#L36) (`ListRecipes`)
- [List solutions](src/main/java/com/example/personalize/ListSolutions.java#L53) (`ListSolutions`)
- [Update a campaign](src/main/java/com/example/personalize/UpdateCampaign.java#L50) (`UpdateCampaign`)


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

- [Amazon Personalize Developer Guide](https://docs.aws.amazon.com/personalize/latest/dg/what-is-personalize.html)
- [Amazon Personalize API Reference](https://docs.aws.amazon.com/personalize/latest/dg/API_Reference.html)
- [SDK for Java 2.x Amazon Personalize reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/personalize/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0