# Lambda code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Lambda.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lambda](Actions/HelloLambda.cs#L4) (`ListFunctions`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](Actions/LambdaWrapper.cs#L26)
- [DeleteFunction](Actions/LambdaWrapper.cs#L72)
- [GetFunction](Actions/LambdaWrapper.cs#L96)
- [Invoke](Actions/LambdaWrapper.cs#L116)
- [ListFunctions](Actions/LambdaWrapper.cs#L143)
- [UpdateFunctionCode](Actions/LambdaWrapper.cs#L164)
- [UpdateFunctionConfiguration](Actions/LambdaWrapper.cs#L192)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a serverless application to manage photos](../cross-service/PhotoAssetManager)
- [Create an application to analyze customer feedback](../cross-service/FeedbackSentimentAnalyzer)
- [Get started with functions](Actions/LambdaWrapper.cs)
- [Transform data with S3 Object Lambda](../cross-service/S3ObjectLambdaFunction)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Lambda

This example shows you how to get started using Lambda.



#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Create an application to analyze customer feedback

This example shows you how to create an application that analyzes customer comment cards, translates them from their original language, determines their sentiment, and generates an audio file from the translated text.


<!--custom.scenario_prereqs.cross_FSA.start-->
<!--custom.scenario_prereqs.cross_FSA.end-->


<!--custom.scenarios.cross_FSA.start-->
<!--custom.scenarios.cross_FSA.end-->

#### Get started with functions

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.end-->


<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.start-->
Before you can run the getting started with Lambda scenario, you must upload
the following two files to an Amazon Simple Storage Service (Amazon S3) bucket that you
own:

 * [LambdaIncrement.zip](LambdaIncrement.zip)
 * [LambdaCalculator.zip](LambdaCalculator.zip)
 
##### Configuration settings

The scenario includes the following settings in `settings.json`:

 * `FunctionName` - A name for the Lambda function.
 * `Handler` - "LambdaIncrement::LambdaIncrement.Function::FunctionHandler"
 * `UpdatedHandler` - "LambdaCalculator::LambdaCalculator.Function::FunctionHandler"
 * `BucketName` - The name of the bucket containing the .zip files for the sample functions.
 * `IncrementKey` - "LambdaIncrement.zip",
 * `CalculatorKey` - "LambdaCalculator.zip",
 * `RoleName` - The name of the IAM role that gives the scenario permissions to access Lambda.
 * `PolicyArn` - The Amazon Resource Name (ARN) of a policy giving the IAM role permissions to access Lambda.
<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.end-->

#### Transform data with S3 Object Lambda

This example shows you how to transform data for your application with S3 Object Lambda.


<!--custom.scenario_prereqs.cross_ServerlessS3DataTransformation.start-->
<!--custom.scenario_prereqs.cross_ServerlessS3DataTransformation.end-->


<!--custom.scenarios.cross_ServerlessS3DataTransformation.start-->
<!--custom.scenarios.cross_ServerlessS3DataTransformation.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for .NET Lambda reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Lambda/NLambda.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0