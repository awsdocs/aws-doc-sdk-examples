# HealthImaging code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS HealthImaging.

<!--custom.overview.start-->
<!--custom.overview.end-->

_HealthImaging is a HIPAA-eligible service that helps health care providers and their medical imaging ISV partners store, transform, and apply machine learning to medical images._

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

### Get started

- [Hello HealthImaging](hello.js#L6) (`ListDatastores`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyImageSet](actions/copy-image-set.js#L6)
- [CreateDatastore](actions/create-datastore.js#L6)
- [DeleteDatastore](actions/delete-datastore.js#L6)
- [DeleteImageSet](actions/delete-image-set.js#L6)
- [GetDICOMImportJob](actions/get-dicom-import-job.js#L6)
- [GetDatastore](actions/get-datastore.js#L6)
- [GetImageFrame](actions/get-image-frame.js#L7)
- [GetImageSet](actions/get-image-set.js#L6)
- [GetImageSetMetadata](actions/get-image-set-metadata.js#L6)
- [ListDICOMImportJobs](actions/list-dicom-import-jobs.js#L6)
- [ListDatastores](actions/list-datastores.js#L6)
- [ListImageSetVersions](actions/list-image-set-versions.js#L6)
- [ListTagsForResource](actions/list-tags-for-resource.js#L6)
- [SearchImageSets](actions/search-image-sets.js#L6)
- [StartDICOMImportJob](actions/start-dicom-import-job.js#L6)
- [TagResource](actions/tag-resource.js#L6)
- [UntagResource](actions/untag-resource.js#L6)
- [UpdateImageSetMetadata](actions/update-image-set-metadata.js#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with image sets and image frames](scenarios/health-image-sets/index.js)
- [Tagging a data store](scenarios/tagging-datastores.js)
- [Tagging an image set](scenarios/tagging-imagesets.js)


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

#### Hello HealthImaging

This example shows you how to get started using HealthImaging.

```bash
node ./hello.js
```


#### Get started with image sets and image frames

This example shows you how to import DICOM files and download image frames in HealthImaging.</para>
 <para>The implementation is structured as a command-line
 application.


- Set up resources for a DICOM import.
- Import DICOM files into a data store.
- Retrieve the image set IDs for the import job.
- Retrieve the image frame IDs for the image sets.
- Download, decode and verify the image frames.
- Clean up resources.

<!--custom.scenario_prereqs.medical-imaging_Scenario_ImageSetsAndFrames.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_ImageSetsAndFrames.end-->


<!--custom.scenarios.medical-imaging_Scenario_ImageSetsAndFrames.start-->
<!--custom.scenarios.medical-imaging_Scenario_ImageSetsAndFrames.end-->

#### Tagging a data store

This example shows you how to tag a HealthImaging data store.


<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingDataStores.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingDataStores.end-->


<!--custom.scenarios.medical-imaging_Scenario_TaggingDataStores.start-->
<!--custom.scenarios.medical-imaging_Scenario_TaggingDataStores.end-->

#### Tagging an image set

This example shows you how to tag a HealthImaging image set.


<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingImageSets.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingImageSets.end-->


<!--custom.scenarios.medical-imaging_Scenario_TaggingImageSets.start-->
<!--custom.scenarios.medical-imaging_Scenario_TaggingImageSets.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [HealthImaging Developer Guide](https://docs.aws.amazon.com/healthimaging/latest/devguide/what-is.html)
- [HealthImaging API Reference](https://docs.aws.amazon.com/healthimaging/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) HealthImaging reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/medical-imaging)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
