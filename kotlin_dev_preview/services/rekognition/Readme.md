# Amazon Rekognition Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Rekognition.

## Running the Amazon Rekognition Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a collection. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **AddFacesToCollection** - Demonstrates how to add faces to an Amazon Rekognition collection.
- **CelebrityInfo** - Demonstrates how to get information about a detected celebrity.
- **CompareFaces** - Demonstrates how to compare two faces.
- **CreateCollection** - Demonstrates how to create an Amazon Rekognition collection.
- **DeleteCollection** - Demonstrates how to delete an Amazon Rekognition collection.
- **DeleteFacesFromCollection** - Demonstrates how to delete faces from an Amazon Rekognition collection.
- **DescribeCollection** - Demonstrates how to retrieve the description of an Amazon Rekognition collection.
- **DetectFaces** - Demonstrates how to detect faces in an image.
- **DetectLabels** - Demonstrates how to capture labels (like water and mountains) in a given image.
- **DetectModerationLabels** - Demonstrates how to detect unsafe content in an image.
- **DetectPPE** - Demonstrates how to detect Personal Protective Equipment (PPE) worn by people detected in an image.
- **DetectText** - Demonstrates how to display words that were detected in an image.
- **ListCollections** - Demonstrates how to list the available Amazon Rekognition collections.
- **ListFacesInCollection** - Demonstrates how to list the faces in an Amazon Rekognition collection.
- **RecognizeCelebrities** - Demonstrates how to recognize celebrities in a given image.
- **VideoDetectFaces** - Demonstrates how to detect faces in a video stored in an Amazon S3 bucket.
- **VideoDetectInappropriate** - Demonstrates how to detect inappropriate or offensive content in a video stored in an Amazon S3 bucket.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
