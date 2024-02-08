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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Add a tag to a resource](medical_imaging_basics.py#L488) (`TagResource`)
- [Copy an image set](medical_imaging_basics.py#L412) (`CopyImageSet`)
- [Create a data store](medical_imaging_basics.py#L28) (`CreateDatastore`)
- [Delete a data store](medical_imaging_basics.py#L101) (`DeleteDatastore`)
- [Delete an image set](medical_imaging_basics.py#L463) (`DeleteImageSet`)
- [Get an image frame](medical_imaging_basics.py#L315) (`GetImageFrame`)
- [Get data store properties](medical_imaging_basics.py#L51) (`GetDatastore`)
- [Get image set properties](medical_imaging_basics.py#L238) (`GetImageSet`)
- [Get import job properties](medical_imaging_basics.py#L155) (`GetDICOMImportJob`)
- [Get metadata for an image set](medical_imaging_basics.py#L271) (`GetImageSetMetadata`)
- [Import bulk data into a data store](medical_imaging_basics.py#L121) (`StartDICOMImportJob`)
- [List data stores](medical_imaging_basics.py#L76) (`ListDatastores`)
- [List image set versions](medical_imaging_basics.py#L347) (`ListImageSetVersions`)
- [List import jobs for a data store](medical_imaging_basics.py#L180) (`ListDICOMImportJobs`)
- [List tags for a resource](medical_imaging_basics.py#L530) (`ListTagsForResource`)
- [Remove a tag from a resource](medical_imaging_basics.py#L508) (`UntagResource`)
- [Search image sets](medical_imaging_basics.py#L208) (`SearchImageSets`)
- [Update image set metadata](medical_imaging_basics.py#L378) (`UpdateImageSetMetadata`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Tagging a data store](tagging_data_stores.py)
- [Tagging an image set](tagging_image_sets.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



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