# Amazon Personalize code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Personalize.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a batch interface job](src/personalize_createBatchInferenceJob.js#L28) (`CreateBatchInferenceJob`)
- [Create a batch segment job](src/personalize_createBatchSegmentJob.js#L27) (`CreateBatchSegmentJob`)
- [Create a campaign](src/personalize_createCampaign.js#L22) (`CreateCampaign`)
- [Create a dataset](src/personalize_createDataset.js#L23) (`CreateDataset`)
- [Create a dataset export job](src/personalize_createDatasetExportJob.js#L24) (`CreateDatasetExportJob`)
- [Create a dataset group](src/personalize_createDatasetGroup.js#L21) (`CreateDatasetGroup`)
- [Create a dataset import job](src/personalize_createDatasetImportJob.js#L23) (`CreateDatasetImportJob`)
- [Create a filter](src/personalize_createFilter.js#L21) (`CreateFilter`)
- [Create a recommender](src/personalize_createRecommender.js#L22) (`CreateRecommender`)
- [Create a schema](src/personalize_createSchema.js#L21) (`CreateSchema`)
- [Create a solution](src/personalize_createSolution.js#L22) (`CreateSolution`)
- [Create a solution version](src/personalize_createSolutionVersion.js#L20) (`CreateSolutionVersion`)
- [Create an event tracker](src/personalize_createEventTracker.js#L21) (`CreateEventTracker`)


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

<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Personalize Developer Guide](https://docs.aws.amazon.com/personalize/latest/dg/what-is-personalize.html)
- [Amazon Personalize API Reference](https://docs.aws.amazon.com/personalize/latest/dg/API_Reference.html)
- [SDK for JavaScript (v3) Amazon Personalize reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/personalize)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0