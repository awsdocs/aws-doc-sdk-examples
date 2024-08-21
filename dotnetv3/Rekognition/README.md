# Amazon Rekognition code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Rekognition.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CompareFaces](CompareFacesExample/CompareFaces.cs#L6)
- [CreateCollection](CreateCollectionExample/CreateCollection.cs#L6)
- [DeleteCollection](DeleteCollectionExample/DeleteCollection.cs#L6)
- [DeleteFaces](DeleteFacesExample/DeleteFaces.cs#L6)
- [DescribeCollection](DescribeCollectionExample/DescribeCollection.cs#L6)
- [DetectFaces](DetectFacesExample/DetectFaces.cs#L6)
- [DetectLabels](DetectLabelsExample/DetectLabels.cs#L6)
- [DetectModerationLabels](DetectModerationLabelsExample/DetectModerationLabels.cs#L6)
- [DetectText](DetectTextExample/DetectText.cs#L6)
- [GetCelebrityInfo](CelebrityInfoExample/CelebrityInfo.cs#L6)
- [IndexFaces](AddFacesExample/AddFaces.cs#L6)
- [ListCollections](ListCollectionsExample/ListCollections.cs#L6)
- [ListFaces](ListFacesExample/ListFaces.cs#L6)
- [RecognizeCelebrities](CelebritiesInImageExample/CelebritiesInImage.cs#L6)
- [SearchFaces](SearchFacesMatchingIdExample/SearchFacesMatchingId.cs#L6)
- [SearchFacesByImage](SearchFacesMatchingImageExample/SearchFacesMatchingImage.cs#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a serverless application to manage photos](../cross-service/PhotoAssetManager)
- [Detect objects in images](../cross-service/PhotoAnalyzerApp)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Detect objects in images

This example shows you how to build an app that uses Amazon Rekognition to detect objects by category in images.


<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.end-->


<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon Rekognition API Reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon Rekognition reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Rekognition/NRekognition.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0