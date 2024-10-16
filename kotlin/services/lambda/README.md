# Lambda code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS Lambda.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Lambda allows you to run code without provisioning or managing servers._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/lambda/LambdaScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](src/main/kotlin/com/kotlin/lambda/CreateFunction.kt#L50)
- [DeleteFunction](src/main/kotlin/com/kotlin/lambda/DeleteFunction.kt#L38)
- [Invoke](src/main/kotlin/com/kotlin/lambda/LambdaInvoke.kt#L39)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.end-->


<!--custom.basics.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basics.lambda_Scenario_GettingStartedFunctions.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->

You can test the Kotlin code examples for Lambda by running a test file named **LambdaTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

    Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

#### Properties file

Before running the Lambda JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an function name used for various tests.
If you do not define all values, the JUnit tests fail.

Define the following values to successfully run the JUnit tests:

- **functionNameSc** - The name of a new function name used for the LambdaScenario test (for example, myLambdaSc).
- **functionName** – The name of a new function name (for example, myLambda).
- **bucketName** - The Amazon Simple Storage Service (Amazon S3) bucket name that contains the .zip or .jar file used to update the Lambda function's code.
- **key** - The Amazon S3 key name that represents the .zip or .jar file (for example, LambdaHello-1.0-SNAPSHOT.jar).
- **filePath** - The path to the .zip or .jar file where the code is located.
- **role** - The role Amazon Resource Name (ARN) that has Lambda permissions.
- **handler** - The fully qualifed method name (for example, example.Handler::handleRequest).

**Note**: The **CreateFunction** and **LambdaScenario** tests requires a .zip or .jar file that represents the code of the Lambda function. If you do not have a .zip or .jar file, please refer to the following document:

https://github.com/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_workflows_stepfunctions

#### Command line

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

#### Unsuccessful tests

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

<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Kotlin Lambda reference](https://sdk.amazonaws.com/kotlin/api/latest/lambda/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0