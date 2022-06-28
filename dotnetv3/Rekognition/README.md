# Amazon Rekognition code examples for the AWS SDK for .NET v3

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET 3.x to get started analyzing images and videos using Amazon Rekognition.

## ⚠️ Important

- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account.

## Code examples

- [Adding faces to a collection](AddFacesExample/)
- [Detecting celebrities in an image](CelebritiesInImageExample/)
- [Retrieving celebrity information](CelebrityInfoExample/)
- [Comparing faces in two images](CompareFacesExample/)
- [Creating a collection](CreateCollectionExample/)
- [Deleting a collection](DeleteCollectionExample/)
- [Deleting faces from a collection](DeleteFacesExample/)
- [Describing a collection](DescribeCollectionExample/)
- [Detecting faces in an image](DetectFacesExample/)
- [Detecting labels in an image](DetectLabelsExample/)
- [Detect moderation labels in an image](DetectModerationLabelsExample/)
- [Detecting text in an image](DetectTextExample/)
- [Recognizing faces](FaceRekognitionExample/)
- [Finding the image orientation bounding box](ImageOrientationBoundingBoxExample/)
- [Listing collections](ListCollectionsExample/)
- [Listing the faces in a collection](ListFacesExample/)
- [Searching for faces matching an Id](SearchFacesMatchingIdExample/)
- [Searching for faces matching a sample image](SearchFacesMatchingImageExample/)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information
- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
