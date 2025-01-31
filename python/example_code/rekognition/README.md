# Amazon Rekognition code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Rekognition.

<!--custom.overview.start-->
Also includes a [utility](custom_labels_csv_to_manifest.py) that you can use to create a custom label image-level manifest 
file from a CSV file.
<!--custom.overview.end-->

_Amazon Rekognition makes it easy to add image and video analysis to your applications._

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

- [CompareFaces](rekognition_image_detection.py#L117)
- [CreateCollection](rekognition_collections.py#L323)
- [DeleteCollection](rekognition_collections.py#L111)
- [DeleteFaces](rekognition_collections.py#L280)
- [DescribeCollection](rekognition_collections.py#L84)
- [DetectFaces](rekognition_image_detection.py#L96)
- [DetectLabels](rekognition_image_detection.py#L156)
- [DetectModerationLabels](rekognition_image_detection.py#L178)
- [DetectText](rekognition_image_detection.py#L207)
- [IndexFaces](rekognition_collections.py#L126)
- [ListCollections](rekognition_collections.py#L346)
- [ListFaces](rekognition_collections.py#L167)
- [RecognizeCelebrities](rekognition_image_detection.py#L226)
- [SearchFaces](rekognition_collections.py#L241)
- [SearchFacesByImage](rekognition_collections.py#L193)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build a collection and find faces in it](rekognition_collections.py)
- [Detect and display elements in images](rekognition_image_detection.py)
- [Detect objects in images](../../cross_service/photo_analyzer)
- [Detect people and objects in a video](../../example_code/rekognition)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Build a collection and find faces in it

This example shows you how to do the following:

- Create an Amazon Rekognition collection.
- Add images to the collection and detect faces in it.
- Search the collection for faces that match a reference image.
- Delete a collection.

<!--custom.scenario_prereqs.rekognition_Usage_FindFacesInCollection.start-->
<!--custom.scenario_prereqs.rekognition_Usage_FindFacesInCollection.end-->

Start the example by running the following at a command prompt:

```
python rekognition_collections.py
```


<!--custom.scenarios.rekognition_Usage_FindFacesInCollection.start-->
<!--custom.scenarios.rekognition_Usage_FindFacesInCollection.end-->

#### Detect and display elements in images

This example shows you how to do the following:

- Detect elements in images by using Amazon Rekognition.
- Display images and draw bounding boxes around detected elements.

<!--custom.scenario_prereqs.rekognition_Usage_DetectAndDisplayImage.start-->
<!--custom.scenario_prereqs.rekognition_Usage_DetectAndDisplayImage.end-->

Start the example by running the following at a command prompt:

```
python rekognition_image_detection.py
```


<!--custom.scenarios.rekognition_Usage_DetectAndDisplayImage.start-->
<!--custom.scenarios.rekognition_Usage_DetectAndDisplayImage.end-->

#### Detect objects in images

This example shows you how to build an app that uses Amazon Rekognition to detect objects by category in images.


<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.end-->


<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.end-->

#### Detect people and objects in a video

This example shows you how to detect people and objects in a video with Amazon Rekognition.


<!--custom.scenario_prereqs.cross_RekognitionVideoDetection.start-->
<!--custom.scenario_prereqs.cross_RekognitionVideoDetection.end-->


<!--custom.scenarios.cross_RekognitionVideoDetection.start-->
<!--custom.scenarios.cross_RekognitionVideoDetection.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon Rekognition API Reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
- [SDK for Python Amazon Rekognition reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rekognition.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0