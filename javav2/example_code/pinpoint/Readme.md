# Amazon Pinpoint Java code examples

This README discusses how to run and test the Java (v2) code examples for Amazon Pinpoint.

Amazon Pinpoint is a flexible, scalable marketing communications service that connects you with customers over email, SMS, push notifications, or voice.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

You will find these examples that use the **PinpointClient** object: 

- [Update Amazon Pinpoint endpoints](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/AddExampleEndpoints.java) (updateEndpointsBatch command)
- [Update a single Amazon Pinpoint endpoint](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/AddExampleUser.java) (updateEndpoint command)
- [Create an Amazon Pinpoint application](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/CreateApp.java) (createApp command)
- [Create a campaign for an application in Amazon Pinpoint](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/CreateCampaign.java) (createCampaign command)
- [Create a segment for a campaign in Amazon Pinpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/CreateSegment.java) (createSegment command)
- [Delete an Amazon Pinpoint application.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/DeleteApp.java) (deleteApp command)
- [Delete an Amazon Pinpoint endpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/DeleteEndpoint.java) (deleteEndpoint command)
- [Export endpoints to an Amazon Simple Storage Service (Amazon S3) bucket.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/ExportEndpoints.java) (createExportJob command)
- [Get a segment using its id value.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/GetSegmentById.java) (getSegment command)
- [Get an Amazon Pinpoint template.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/GetTemplateByName.java) (getEmailTemplate command)
- [Import a segment into Amazon Pinpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/ImportSegment.java) (createImportJob command)
- [Retrieve information about all the endpoints.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/ListEndpointIds.java) (getUserEndpoints command)
- [List Amazon Pinpoint segments.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/ListSegments.java) (getSegments command)
- [Display information about an existing endpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/LookUpEndpoint.java) (getEndpoint command)
- [Send an email message.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/SendEmailMessage.java) (sendMessages command)
- [Send batch SMS messages.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/SendMessageBatch.java) (sendMessages command)
- [Send batch SMS messages.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/SendMessageBatch.java) (sendMessages command)
- [Send a voice message using the PinpointSmsVoiceClient.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/SendVoiceMessage.java) (sendVoiceMessage command)
- [Update a channel for an Amazon Pinpoint application.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/UpdateChannel.java) (getSmsChannel command)
- [Update a channel for an Amazon Pinpoint application.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/pinpoint/src/main/java/com/example/pinpoint/UpdateChannel.java) (getSmsChannel command)


### Cross-service

- [Using AWS Step Functions and the AWS SDK for Java to build workflows that send notifications over multiple channels](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_multiple_channels)


## Running the examples
To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon Pinpoint application. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

You can test the Java code examples for Amazon DynamoDB by running a test file named **DynamoDBTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon DynamoDB resources and might incur charges on your account._

 ### Properties file
Before running the Amazon DynamoDB JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a table name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **tableName** - The name of an Amazon DynamoDB table. For example, **Music3**.
- **fileName** - The path to the JSON document that contains movie data that you can download from the Amazon DynamoDB Developer Guide.
- **enhancedTableName** - the name of the DynamoDB table used with the enhanced client. For example, **Customer**.
- **key** – The name of a key to use. For example, **Artist**.
- **enhancedTableKey** the  name of a key to use for the enhanced client tests. For example, **Id**.
- **keyValue** – The key value. For example, **Famous Band**.
- **albumTitle** – An album title to use. For example, **AlbumTitle**.
- **AlbumTitleValue** – An album title value. For example, **Songs About Life**.
- **Awards** – A value for a column. For example, **Awards**.
- **AwardVal** – The value for the Awards column. For example, **10**.
- **SongTitle** – A value for another column. For example, **SongTitle**.
- **SongTitleVal** – The value for the SongTitle column. For example, **Happy Summer Day**.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running DynamoDBTest
	Running Amazon DynamoDB   Test 1
	Running Amazon DynamoDB  Test 2
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
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
* [Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html).
* [Interface DynamoDbClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/dynamodb/DynamoDbClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
