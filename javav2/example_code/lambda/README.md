# Lambda code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Lambda.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lambda](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L219) (`ListFunctions`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/lambda/scenario/LambdaScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L144)
- [DeleteFunction](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L338)
- [GetFunction](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L196)
- [Invoke](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L242)
- [UpdateFunctionCode](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L196)
- [UpdateFunctionConfiguration](src/main/java/com/example/lambda/scenario/LambdaScenario.java#L311)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Lambda

This example shows you how to get started using Lambda.


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
in the `javav2` folder.



<!--custom.tests.start-->

$## Properties file
Before running the AWS Lambda JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an function name used for various tests.
If you do not define all values, the JUnit tests fail.

Define the following values to successfully run the JUnit tests:

- **functionNameSc** - The name of a new function name used for the LambdaScenario test (for example, myLambdaSc).
- **functionName** – The name of a new function name (for example, myLambda).
- **bucketName** - The Amazon S3 bucket name that contains the .zip or .jar file used to update the Lambda function's code.
- **key** - The Amazon S3 key name that represents the .zip or .jar file (for example, LambdaHello-1.0-SNAPSHOT.jar).
- **filePath** - The path to the .zip or .jar file where the code is located.
- **role** - The role's Amazon Resource Name (ARN) that has Lambda permissions.
- **handler** - The fully qualifed method name (for example, example.Handler::handleRequest).

**Note**: The **CreateFunction** and **LambdaScenario** tests requires a .zip or .jar file that represents the code of the Lambda function. If you do not have a .zip or .jar file, please refer to the following document:

https://github.com/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_workflows_stepfunctions

<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Java 2.x Lambda reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/lambda/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0