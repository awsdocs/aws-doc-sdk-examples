# Amazon Rekognition code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) code examples for Amazon Rekognition.

Amazon Rekognition enables your applications to confirm user identities by comparing their live image with a reference image. Amazon Rekognition also detects content in images and videos, such as face covers, head covers, and hand covers on persons in images.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **RekognitionClient** object:

- [Adding faces to a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/AddFacesToCollection.java) (IndexFaces command)
- [Getting information about a celebrity](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/CelebrityInfo.java) (GetCelebrityInfo command)
- [Comparing two faces](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/CompareFaces.java) (CompareFaces command)
- [Creating a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/CreateCollection.java) (CreateCollection command)
- [Deleting a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DeleteCollection.java) (DeleteCollection command)
- [Deleting faces from a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DeleteFacesFromCollection.java) (DeleteFaces command)
- [Describing a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DescribeCollection.java) (DescribeCollection command)
- [Detecting faces in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectFaces.java) (DetectFaces command)
- [Detecting labels in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectLabels.java) (DetectLabels command)
- [Detecting labels in an Amazon S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectLabelsS3.java) (DetectLabels command)
- [Detecting unsafe content in a given image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectModerationLabels.java) (DetectModerationLabels command)
- [Detecting Personal Protective Equipment in a given image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectPPE.java) (DetectProtectiveEquipment command)
- [Detecting Text in a given image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DetectText.java) (DetectText command)
- [Displaingy a bounding box around faces.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/DisplayFacesFrame.java) (DetectFaces command)
- [Listing the available collections](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/ListCollections.java) (ListCollections command)
- [Listing faces in a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/ListFacesInCollection.java) (ListFaces command)
- [Displaying a green bounding box around a mask](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/PPEBoundingBoxFrame.java) (PPEBoundingBoxFrame command)
- [Recognizing celebrities in a given image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/RecognizeCelebrities.java) (RecognizeCelebrities command)
- [Getting the estimated orientation of an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/RotateImage.java) (BoundingBox commands)
- [Displaying information about a face](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/SearchFaceMatchingIdCollection.java) (SearchFaces command)
- [Getting celebrity results from a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoCelebrityDetection.java) (StartCelebrityRecognition command)
- [Detecting labels in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetect.java) (StartLabelDetection command)
- [Detecting faces in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetectFaces.java) (StartFaceDetection command)
- [Detecting offensive content in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetectInappropriate.java) (StartContentModeration command)
- [Detect technical cue segments in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetectSegment.java) (StartSegmentDetection command)
- [Detect text in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetectText.java) (StartTextDetection command)
- [Detect people in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoPersonDetection.java) (startPersonTracking command)


## Running the Amazon Rekognition Java files

Some of these examples perform *destructive* operations on AWS resources. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).


 ## Testing the Amazon Rekognition files

You can test the Java code examples for Amazon Rekognition by running a test file named **RekognitionTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Amazon Rekognition JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **facesImage** - The path to an image that contains faces (for example, C:\AWS\pic1.png).   
- **faceImage2** – The path to an image that contains faces (for example, C:\AWS\pic1.png).   
- **celebritiesImage** - The path to an image that contains famous people (for example, C:\AWS\pic1.png).
- **celId** - The ID value of the celebrity. You can use the **RecognizeCelebrities** example to get the ID value.
- **moutainImage** - The path to an image that contains mountains (for example, C:\AWS\pic1.png).
- **collectionName** - A string value that represents the collection name (for example, myCollection).
- **ppeImage** - An image that contains a person wearing a mask (for example, masks.png). 
- **textImage** - An image that contains text (for example. myImage.png). 
- **modImage** - An image that contains images that is used in the partental warning test and used in the **DetectModerationLabels** test.
- **bucketName** - The name of the bucket in which the videos used in these tests are located.
- **faceVid** - The name of the video that contains people (for example, people.mp4).
- **modVid** - The name of the video that contains images that is used in the moderation test.
- **textVid** - The name of the video that contains text.
- **celVid** - The name of the video that contains celebrities.
- **topicArn** - An ARN value of a SNS topic (you can obtain this value from the AWS Management Console).
- **topicArn** - An ARN value of an IAM role (you can obtain this value from the AWS Management Console).

**Note**: You must create an IAM role and a valid SNS topic. You need to reference these values in the properties file. If you do not set these values, the tests fail. For information, see [Configuring Amazon Rekognition Video](https://docs.aws.amazon.com/rekognition/latest/dg/api-video-roles.html).

### Command line
To execute the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running RekognitionTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];
	
	
## Additional resources
* [Developer Guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html).
* [Interface RekognitionClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/rekognition/RekognitionClient.html).	
