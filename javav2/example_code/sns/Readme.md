# Amazon SNS Java code examples

## Overview
This README discusses how to run and test the Java V2 code examples for Amazon Simple Notification Service (Amazon SNS).

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

You will find these examples that use the **SnsClient** object: 

- [Adding tags to an Amazon SNS topic](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/AddTags.java) (TagResource command)
- [Determining whether the user of the phone number has selected to no longer receive future Amazon SNS text messages.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/CheckOptOut.java) (CheckIfPhoneNumberIsOptedOut command)
- [Determining whether the user of the phone number has selected to no longer receive future Amazon SNS text messages.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/CheckOptOut.java) (CheckIfPhoneNumberIsOptedOut command)
- [Confirms a subscription for Amazon SNS.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/ConfirmSubscription.java) (ConfirmSubscription command)
- [Creating an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/CreateTopic.java) (CreateTopic command)
- [Deleting tags from an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/DeleteTag.java) (UntagResource command)
- [Deleting an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/DeleteTopic.java) (DeleteTopic command)
- [Retrieving the default SMS type for Amazon SNS.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/GetSMSAtrributes.java) (GetSubscriptionAttributes command)
- [Retrieve the defaults for an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/GetTopicAttributes.java) (GetTopicAttributes command)
- [Listing the phone numbers for which the users have selected to no longer receive future text messages.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/ListOptOut.java) (ListPhoneNumbersOptedOut command)
- [Listing existing Amazon SNS subscriptions.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/ListSubscriptions.java) (ListSubscriptions command)
- [Retrieving tags from an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/ListTags.java) (ListTagsForResource command)
- [Retrieving a list of existing Amazon SNS topics.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/ListTopics.java) (ListTopics command)
- [Sending an Amazon SNS text message.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/PublishTextSMS.java) (Publish command)
- [Publishing an Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/PublishTopic.java) (Publish command)
- [Setting attributes for Amazon SNS.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SetSMSAttributes.java) (SetSMSAttributes command)
- [Setting attributes for Amazon SNS topic.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SetTopicAttributes.java) (SetTopicAttributes command)
- [Subscribing to an Amazon SNS email endpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SubscribeEmail.java) (Subscribe command) 
- [Subscribing to an Amazon SNS HTTPs endpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SubscribeHTTPS.java) (Subscribe command) 
- [Subscribing to an Amazon SNS lambda function.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SubscribeLambda.java) (Subscribe command) 
- [Subscribing to an Amazon SNS text endpoint.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/SubscribeTextSMS.java) (SubscribeTextSMS command)  
- [Removing an Amazon SNS subscription.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/Unsubscribe.java) (Unsubscribe command)  
- [Initializing and using the example SNSMessageFilterPolicy class.](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sns/src/main/java/com/example/sns/UseMessageFilterPolicy.java) (Apply command)  

### Scenario

- [Performing various Amazon DynamoDB operations](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/Scenario.java) (Multiple commands)

- [Performing various Amazon DynamoDB operations using PartiQL](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/ScenarioPartiQ.java) (Multiple commands)
### Cross-service

- [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_first_project) 
- [Creating the Amazon DynamoDB web application item tracker](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_dynamodb_web_app) 
- [Creating scheduled events to invoke Lambda functions](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_scheduled_events) 
- [Create AWS serverless workflows by using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_workflows_stepfunctions) 


## Running the Amazon SNS Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.   

Some of these examples perform *destructive* operations on AWS resources, such as deleting an SNS topic. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).


 ## Testing the Amazon SNS Java files

You can test the Amazon SNS Java code examples by running a test file named **AWSSNSTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon SNS resources and may incur charges on your account._

 ### Properties file
Before running the Amazon SNS JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define a topic name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **topicName** - The name of the topic to use. For example, **SNSTopic101**.

- **attributeName** – The name of an attribute. For example, **DisplayName**.

- **attributeValue** – The value to assign to the attribute. For example, **DisplayName1**.

- **email** - An email address to use.

- **lambdaarn** – The Amazon Resource Name (ARN) of an AWS Lambda function. You can obtain this value from the AWS Management Console.  
-  **phone**  - The number of a mobile phone. A text message is sent as part of the unit test.  

- **message** - The message to use. For example, **Hello from AWS**.  

### Command line
To execute the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AWSSNSTest
	Running Amazon SNS Test 1
	Running Amazon SNS Test 2
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
