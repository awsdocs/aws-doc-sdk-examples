# HealthImaging code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS HealthImaging.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello HealthImaging](imaging_set_and_frames_workflow/hello.py#L4) (`ListDatastores`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyImageSet](health_lake_wrapper.py#L417)
- [CreateDatastore](health_lake_wrapper.py#L31)
- [DeleteDatastore](health_lake_wrapper.py#L104)
- [DeleteImageSet](health_lake_wrapper.py#L489)
- [GetDICOMImportJob](health_lake_wrapper.py#L158)
- [GetDatastore](health_lake_wrapper.py#L54)
- [GetImageFrame](health_lake_wrapper.py#L318)
- [GetImageSet](health_lake_wrapper.py#L241)
- [GetImageSetMetadata](health_lake_wrapper.py#L274)
- [ListDICOMImportJobs](health_lake_wrapper.py#L183)
- [ListDatastores](health_lake_wrapper.py#L79)
- [ListImageSetVersions](health_lake_wrapper.py#L350)
- [ListTagsForResource](health_lake_wrapper.py#L556)
- [SearchImageSets](health_lake_wrapper.py#L211)
- [StartDICOMImportJob](health_lake_wrapper.py#L124)
- [TagResource](health_lake_wrapper.py#L514)
- [UntagResource](health_lake_wrapper.py#L534)
- [UpdateImageSetMetadata](health_lake_wrapper.py#L381)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with image sets and image frames](imaging_set_and_frames_workflow/imaging_set_and_frames.py)
- [Tagging a data store](tagging_data_stores.py)
- [Tagging an image set](tagging_image_sets.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello HealthImaging

This example shows you how to get started using HealthImaging.

```
python imaging_set_and_frames_workflow/hello.py
```


#### Get started with image sets and image frames

This example shows you how to import DICOM files and download image frames in HealthImaging.</para>
 <para>The implementation is structured as a workflow command-line
 application.


- Set up resources for a DICOM import.
- Import DICOM files into a data store.
- Retrieve the image set IDs for the import job.
- Retrieve the image frame IDs for the image sets.
- Download, decode and verify the image frames.
- Clean up resources.

<!--custom.scenario_prereqs.medical-imaging_Scenario_ImageSetsAndFrames.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_ImageSetsAndFrames.end-->

Start the example by running the following at a command prompt:

```
python imaging_set_and_frames_workflow/imaging_set_and_frames.py
```


<!--custom.scenarios.medical-imaging_Scenario_ImageSetsAndFrames.start-->
<!--custom.scenarios.medical-imaging_Scenario_ImageSetsAndFrames.end-->

#### Tagging a data store

This example shows you how to tag a HealthImaging data store.


<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingDataStores.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingDataStores.end-->

Start the example by running the following at a command prompt:

```
python tagging_data_stores.py
```


<!--custom.scenarios.medical-imaging_Scenario_TaggingDataStores.start-->
<!--custom.scenarios.medical-imaging_Scenario_TaggingDataStores.end-->

#### Tagging an image set

This example shows you how to tag a HealthImaging image set.


<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingImageSets.start-->
<!--custom.scenario_prereqs.medical-imaging_Scenario_TaggingImageSets.end-->

Start the example by running the following at a command prompt:

```
python tagging_image_sets.py
```


<!--custom.scenarios.medical-imaging_Scenario_TaggingImageSets.start-->
<!--custom.scenarios.medical-imaging_Scenario_TaggingImageSets.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [HealthImaging Developer Guide](https://docs.aws.amazon.com/healthimaging/latest/devguide/what-is.html)
- [HealthImaging API Reference](https://docs.aws.amazon.com/healthimaging/latest/APIReference/Welcome.html)
- [SDK for Python HealthImaging reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/medical-imaging.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0