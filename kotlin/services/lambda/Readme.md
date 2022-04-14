# AWS Lambda code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for AWS Lambda.

AWS Lambda is a serverless compute service that runs your code in response to events and automatically manages the underlying compute resources for you. These events may include changes in state or an update, such as a user placing an item in a shopping cart on an e-commerce website.

## ⚠️ Important
* The SDK for Kotlin examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **LambdaClient** object:

- [Creating an AWS Lambda function](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/CreateFunction.kt) (CreateFunction command)
- [Deleting an AWS Lambda function](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/DeleteFunction.kt) (DeleteFunction command)
- [Getting information about your account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/GetAccountSettings.kt) (GetAccountSettings command)
- [Invoking an AWS Lambda function]https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/LambdaInvoke.kt) (Invoke command)
- [Performing various operations by using the LambdaClient object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/LambdaScenario.kt) (Multiple commands)
- [Listing AWS Lambda functions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/lambda/src/main/kotlin/com/kotlin/lambda/ListLambda.kt) (ListFunctions command)


## Running the AWS Lambda Kotlin files

Some of these examples perform *destructive* operations on AWS resources, such as deleting a Lambda function. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Setting up the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 

## Testing the AWS Lambda files

You can test the Kotlin code examples for Lambda by running a test file named **LambdaTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Lambda JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an function name used for various tests. 
If you do not define all values, the JUnit tests fail.

Define the following values to successfully run the JUnit tests:

- **functionNameSc** - The name of a new function name used for the LambdaScenario test (for example, myLambdaSc). 
- **functionName** – The name of a new function name (for example, myLambda).
- **bucketName** - The Amazon Simple Storage Service (Amazon S3) bucket name that contains the .zip or .jar file used to update the Lambda function's code.
- **key** - The Amazon S3 key name that represents the .zip or .jar file (for example, LambdaHello-1.0-SNAPSHOT.jar).
- **filePath** - The path to the .zip or .jar file where the code is located.
- **role** - The role ARN that has Lambda permissions.
- **handler** - The fully qualifed method name (for example, example.Handler::handleRequest).

**Note**: The **CreateFunction** and **LambdaScenario** tests requires a .zip or .jar file that represents the code of the Lambda function. If you do not have a .zip or .jar file, please refer to the following document:
 
 https://github.com/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_workflows_stepfunctions

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running LambdaTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
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
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).
* [Developer Guide - AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
