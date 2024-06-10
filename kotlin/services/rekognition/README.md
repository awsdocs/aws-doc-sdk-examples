# Amazon Rekognition code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Rekognition.

<!--custom.overview.start-->
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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CompareFaces](src/main/kotlin/com/kotlin/rekognition/CompareFaces.kt#L42)
- [CreateCollection](src/main/kotlin/com/kotlin/rekognition/CreateCollection.kt#L37)
- [DeleteCollection](src/main/kotlin/com/kotlin/rekognition/DeleteCollection.kt#L38)
- [DeleteFaces](src/main/kotlin/com/kotlin/rekognition/DeleteFacesFromCollection.kt#L37)
- [DescribeCollection](src/main/kotlin/com/kotlin/rekognition/DescribeCollection.kt#L38)
- [DetectFaces](src/main/kotlin/com/kotlin/rekognition/DetectFaces.kt#L40)
- [DetectLabels](src/main/kotlin/com/kotlin/rekognition/DetectLabels.kt#L39)
- [DetectModerationLabels](src/main/kotlin/com/kotlin/rekognition/DetectModerationLabels.kt#L38)
- [DetectText](src/main/kotlin/com/kotlin/rekognition/DetectText.kt#L39)
- [IndexFaces](src/main/kotlin/com/kotlin/rekognition/AddFacesToCollection.kt#L42)
- [ListCollections](src/main/kotlin/com/kotlin/rekognition/ListCollections.kt#L21)
- [ListFaces](src/main/kotlin/com/kotlin/rekognition/ListFacesInCollection.kt#L36)
- [RecognizeCelebrities](src/main/kotlin/com/kotlin/rekognition/RecognizeCelebrities.kt#L39)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Detect information in videos](src/main/kotlin/com/kotlin/rekognition/VideoDetectFaces.kt)


<!--custom.examples.start-->

### Custom Examples

- **CelebrityInfo** - Demonstrates how to get information about a detected celebrity.
- **DetectPPE** - Demonstrates how to detect Personal Protective Equipment (PPE) worn by people detected in an image.
- **VideoDetectFaces** - Demonstrates how to detect faces in a video stored in an Amazon S3 bucket.
- **VideoDetectInappropriate** - Demonstrates how to detect inappropriate or offensive content in a video stored in an Amazon S3 bucket.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Detect information in videos

This example shows you how to do the following:

- Start Amazon Rekognition jobs to detect elements like people, objects, and text in videos.
- Check job status until jobs finish.
- Output the list of elements detected by each job.

<!--custom.scenario_prereqs.rekognition_VideoDetection.start-->
<!--custom.scenario_prereqs.rekognition_VideoDetection.end-->


<!--custom.scenarios.rekognition_VideoDetection.start-->
<!--custom.scenarios.rekognition_VideoDetection.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon Rekognition API Reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
- [SDK for Kotlin Amazon Rekognition reference](https://sdk.amazonaws.com/kotlin/api/latest/rekognition/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0