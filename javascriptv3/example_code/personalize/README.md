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

- [CreateBatchInferenceJob](src/personalize_createBatchInferenceJob.js#L28)
- [CreateBatchSegmentJob](src/personalize_createBatchSegmentJob.js#L27)
- [CreateCampaign](src/personalize_createCampaign.js#L22)
- [CreateDataset](src/personalize_createDataset.js#L23)
- [CreateDatasetExportJob](src/personalize_createDatasetExportJob.js#L24)
- [CreateDatasetGroup](src/personalize_createDatasetGroup.js#L21)
- [CreateDatasetImportJob](src/personalize_createDatasetImportJob.js#L23)
- [CreateEventTracker](src/personalize_createEventTracker.js#L21)
- [CreateFilter](src/personalize_createFilter.js#L21)
- [CreateRecommender](src/personalize_createRecommender.js#L22)
- [CreateSchema](src/personalize_createSchema.js#L21)
- [CreateSolution](src/personalize_createSolution.js#L22)
- [CreateSolutionVersion](src/personalize_createSolutionVersion.js#L20)


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