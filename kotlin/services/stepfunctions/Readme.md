# Step Functions for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for AWS Step Functions.

AWS Step Functions is a visual workflow service that helps developers use AWS services to build distributed applications, automate processes, orchestrate microservices, and create data and machine learning (ML) pipelines.

## ⚠️ Important
* Running this code might result in charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

- [Hello AWS Step Functions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/HelloStepFunctions.kt) (listStateMachines command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Create an activity](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (createActivity command)
- [Create a state machine](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (createStateMachine command)
- [Delete an activity](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (deleteActivity command)
- [Delete a state machine](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (deleteStateMachine command)
- [Describe an execution](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (describeExecution command)
- [Describe a state machine](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (describeStateMachine command)
- [Get task data for an activity](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (getActivityTask command)
- [Retrieve the history of a specific execution](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/GetExecutionHistory.kt) (getExecutionHistory command)
- [Retrieve a list of failed executions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/GetFailedExecutions.kt) (listExecutions command)
- [Send a success response to a task](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (sendTaskSuccess command)
- [Start a state machine](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (startExecution command)

### Scenarios

Code examples that show you how to accomplish specific tasks by calling multiple functions within the same service.

- [Get started with AWS Step Functions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/stepfunctions/src/main/kotlin/com/kotlin/stepfunctions/StepFunctionsScenario.kt) (multiple commands)

## Run the AWS Step Functions Kotlin files

### Prerequisites

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/home.html). 

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

 ## Test the AWS Step Functions Kotlin files
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Kotlin code example for AWS Step Functions by running a test file named **StepFunctionsKotlinTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. If you do not define all values, the JUnit tests fail.

Define the following values to successfully run the JUnit tests:

- **roleNameSc** - The name of the IAM role to create for the Scenario test.
- **activityNameSc** - The name of an activity to create for the Scenario test.
- **stateMachineNameSc** – The name of the state machine to create for the Scenario test.

You can obtain the JSON file to create a state machine in the following GitHub location: 

https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/sample_files

To run the Scenario test, place the **chat_sfn_state_machine.json** file into your project's **resources** folder. If you do not, the test is not successful. 

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/home.html).
* [Developer Guide - AWS Step Functions](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
