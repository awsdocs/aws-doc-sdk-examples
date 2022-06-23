# Amazon Rekognition code examples in C#

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET 3.x to
get started analyzing images and videos using Amazon Rekognition.

Provide an image or video to the Amazon Rekognition API, to identify objects,
people, text, scenes, and activities. It can be used to
[moderate content](https://docs.aws.amazon.com/rekognition/latest/dg/moderation.html#moderation-api)
in an image or detect [inappropriate content in stored videos](https://docs.aws.amazon.com/rekognition/latest/dg/procedure-moderate-videos.html)
as well. Amazon Rekognition also provides highly accurate facial analysis, face
comparison, and face search capabilities. You can detect, analyze, and compare
faces for a wide variety of use cases, including user verification, cataloging,
people counting, and [public safety](https://docs.aws.amazon.com/rekognition/latest/dg/considerations-public-safety-use-cases.html).

## Code examples

- [AddFacesExample](AddFacesExample/) - Add faces detected in an image to a collection.
- [CelebritiesInImageExample](CelebritiesInImageExample/) - Detect any celebrity faces in an image.
- [CelebrityInfoExample](CelebrityInfoExample/) - Retrieve information for a celebrity based on the celebrity Id.
- [CompareFacesExample](CompareFacesExample/) - Compare faces in two images.
- [CreateCollectionExample](CreateCollectionExample/) - Create a collection to which you can add face data.
- [DeleteCollectionExample](DeleteCollectionExample/) - Delete an existing collection.
- [DeleteFacesExample](DeleteFacesExample/) - Delete one or more faces from a collection.
- [DescribeCollectionExample](DescribeCollectionExample/) - Describe the contents of a collection.
- [DetectFacesExample](DetectFacesExample/) - Detect faces in an image.
- [DetectLabelsExample](DetectLabelsExample/) - Delete labels in an image stored in an Amazon Simple Storage Service (Amazon S3) bucket.
- [DetectModerationLabelsExample](DetectModerationLabelsExample/) - Detect
  [unsafe content](https://docs.aws.amazon.com/rekognition/latest/dg/procedure-moderate-images.html) in an image.
- [DetectTextExample](DetectTextExample/) - Detect text in an image.
- [FaceRekognitionExample](FaceRekognitionExample/) - Scan an image for faces and then for celebrity faces.
- [ImageOrientationBoundingBoxExample](ImageOrientationBoundingBoxExample/) - Determine the orientation of an image based on the proportions of its bounding box.
- [ListCollectionsExample](ListCollectionsExample/) - List collections.
- [ListFacesExample](ListFacesExample/) - List the faces in a collection.
- [SearchFacesMatchingIdExample](SearchFacesMatchingIdExample/) - Search an image for faces matching an Id.
- [SearchFacesMatchingImageExample](SearchFacesMatchingImageExample/) - Search faces that match an example image.

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the examples

The examples in this folder use the default user account. The call to
initialize the Rekognition client does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information
[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
