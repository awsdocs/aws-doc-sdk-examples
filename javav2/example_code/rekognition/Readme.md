# Amazon Rekognition code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Rekognition.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Rekognition makes it easy to add image and video analysis to your applications._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

- [Compare faces in an image against a reference image](src/main/java/com/example/rekognition/CompareFaces.java#L11) (`CompareFaces`)
- [Create a collection](src/main/java/com/example/rekognition/CreateCollection.java#L11) (`CreateCollection`)
- [Delete a collection](src/main/java/com/example/rekognition/DeleteCollection.java#L12) (`DeleteCollection`)
- [Delete faces from a collection](src/main/java/com/example/rekognition/DeleteFacesFromCollection.java#L12) (`DeleteFaces`)
- [Describe a collection](src/main/java/com/example/rekognition/DescribeCollection.java#L12) (`DescribeCollection`)
- [Detect faces in an image](src/main/java/com/example/rekognition/DetectFaces.java#L12) (`DetectFaces`)
- [Detect labels in an image](src/main/java/com/example/rekognition/DetectLabels.java#L12) (`DetectLabels`)
- [Detect moderation labels in an image](src/main/java/com/example/rekognition/DetectModerationLabels.java#L12) (`DetectModerationLabels`)
- [Detect text in an image](src/main/java/com/example/rekognition/DetectText.java#L11) (`DetectText`)
- [Index faces to a collection](src/main/java/com/example/rekognition/AddFacesToCollection.java#L11) (`IndexFaces`)
- [List collections](src/main/java/com/example/rekognition/ListCollections.java#L12) (`ListCollections`)
- [List faces in a collection](src/main/java/com/example/rekognition/ListFacesInCollection.java#L12) (`ListFaces`)
- [Recognize celebrities in an image](src/main/java/com/example/rekognition/RecognizeCelebrities.java#L12) (`RecognizeCelebrities`)
- [Search for faces in a collection](src/main/java/com/example/rekognition/SearchFaceMatchingImageCollection.java#L12) (`SearchFaces`)
- [Search for faces in a collection compared to a reference image](src/main/java/com/example/rekognition/SearchFaceMatchingIdCollection.java#L11) (`SearchFacesByImage`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Detect information in videos](src/main/java/com/example/rekognition/VideoCelebrityDetection.java)


<!--custom.examples.start-->
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
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon Rekognition API Reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon Rekognition reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/rekognition/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0