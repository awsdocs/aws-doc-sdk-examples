# Amazon Rekognition code examples for the SDK for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with Amazon Rekognition to get started analyzing images and videos.

Amazon Rekognition makes it easy to add image and video analysis to your applications. You just provide an image or video to the Amazon Rekognition API, and the service can identify objects, people, text, scenes, and activities.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Compare faces in an image against a reference image](CompareFacesExample/CompareFaces.cs) (`CompareFacesAsync`)
- [Create a collection](CreateCollectionExample/CreateCollection.cs) (`CreateCollectionAsync`)
- [Delete a collection](DeleteCollectionExample/DeleteCollection.cs) (`DeleteCollectionAsync`)
- [Delete faces from a collection](DeleteFacesExample/DeleteFaces.cs) (`DeleteFacesAsync`)
- [Describe a collection](DescribeCollectionExample/DescribeCollection.cs) (`DescribeCollectionAsync`)
- [Detect faces in an image](DetectFacesExample/DetectFaces.cs) (`DetectFacesAsync`)
- [Detect labels in an image](DetectLabelsExample/DetectLabels.cs) (`DetectLabelsAsync`)
- [Detect moderation labels in an image](DetectModerationLabelsExample/DetectModerationLabels.cs) (`DetectModerationLabels`)
- [Detect text in an image](DetectTextExample/DetectText.cs) (`DetectTextAsync`)
- [Determine the orientation of an image](ImageOrientationBoundingBoxExample/ImageOrientationBoundingBox.cs) (`DetectFacesAsync`)
- [Get information about celebrities](CelebrityInfoExample/CelebrityInfo.cs) (`GetCelebrityInfoAsync`)
- [Index faces to a collection](AddFacesExample/AddFaces.cs) (`IndexFacesAsync`)
- [List collections](ListCollectionsExample/ListCollections.cs) (`ListCollectionsAsync`)
- [List faces in a collection](ListFacesExample/ListFaces.cs) (`ListFacesAsync`)
- [Recognize celebrities in an image](CelebritiesInImageExample/CelebritiesInImage.cs) (`RecognizeCelebritiesAsync`)
- [Scan an image for faces and then for celebrity faces](FaceRekognitionExample/FaceRekognitionExample/FaceRekognition.cs) (`RecognizeCelebritiesAsync`)
- [Search for faces in a collection](SearchFacesMatchingIdExample/SearchFacesMatchingId.cs) (`SearchFacesAsync`)
- [Search for faces in a collection compared to a reference image](SearchFacesMatchingImageExample/SearchFacesMatchingImage.cs) (`SearchFacesByImageAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS Region. The following
example shows how to supply the AWS Region to match your own as a
parameter to the client constructor:

```
var client = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);
```

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
* [Amazon Rekognition API Reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon Rekognition](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Rekognition/NRekognition.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
