# Amazon Rekognition Java code examples

This README discusses how to run and test the Java code examples for Amazon Rekognition.

## Running the Amazon Rekognition Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

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

- **facesImage** - The path to an image that contains faces.   
- **faceImage2** – The path to an image that contains faces.   
- **celebritiesImage** - The path to an image that contains famous people.
- **celId** - The ID value of the celebrity. You can use the **RecognizeCelebrities** example to get the ID value.
- **moutainImage** - The path to an image that contains mountains.
- **collectionName** - A string value that represents the collection name.
- **ppeImage** - An image that contains a person wearing a mask. 
- **textImage** - An image that contains text. 
- **modImage** - An image that contains images that is used in the partental warning test and used in the **DetectModerationLabels** test.
- **bucketName** - The name of the bucket in which the video is located.
- **faceVid** - The name of video that contains people (for example, people.mp4).
- **modVid** - The name of a video that contains images that is used in the partental warning test.
- **textVid** - The name of a video that contains text.
- **celVid** - The name of a video that contains celebrities.
- **topicArn** - An ARN value of a SNS topic.
- **topicArn** - An ARN value of an IAM role.

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
