# Lambda code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Lambda.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lambda](hello/hello_lambda.py#L4) (`ListFunctions`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](lambda_handler_basic.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](lambda_basics.py#L144)
- [DeleteFunction](lambda_basics.py#L185)
- [GetFunction](lambda_basics.py#L118)
- [Invoke](lambda_basics.py#L200)
- [ListFunctions](lambda_basics.py#L280)
- [UpdateFunctionCode](lambda_basics.py#L226)
- [UpdateFunctionConfiguration](lambda_basics.py#L254)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a REST API to track COVID-19 data](../../cross_service/apigateway_covid-19_tracker)
- [Create a lending library REST API](../../cross_service/aurora_rest_lending_library)
- [Create a messenger application](../../cross_service/stepfunctions_messenger)
- [Create a websocket chat application](../../cross_service/apigateway_websocket_chat)
- [Use API Gateway to invoke a Lambda function](../../example_code/lambda)
- [Use scheduled events to invoke a Lambda function](../../example_code/lambda)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Lambda

This example shows you how to get started using Lambda.

```
python hello/hello_lambda.py
```

#### Learn the basics

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basic_prereqs.lambda_Scenario_GettingStartedFunctions.end-->

Start the example by running the following at a command prompt:

```
python lambda_handler_basic.py
```


<!--custom.basics.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basics.lambda_Scenario_GettingStartedFunctions.end-->


#### Create a REST API to track COVID-19 data

This example shows you how to create a REST API that simulates a system to track daily cases of COVID-19 in the United States, using fictional data.


<!--custom.scenario_prereqs.cross_ApiGatewayDataTracker.start-->
<!--custom.scenario_prereqs.cross_ApiGatewayDataTracker.end-->


<!--custom.scenarios.cross_ApiGatewayDataTracker.start-->
<!--custom.scenarios.cross_ApiGatewayDataTracker.end-->

#### Create a lending library REST API

This example shows you how to create a lending library where patrons can borrow and return books by using a REST API backed by an Amazon Aurora database.


<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.end-->


<!--custom.scenarios.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenarios.cross_AuroraRestLendingLibrary.end-->

#### Create a messenger application

This example shows you how to create an AWS Step Functions messenger application that retrieves message records from a database table.


<!--custom.scenario_prereqs.cross_StepFunctionsMessenger.start-->
<!--custom.scenario_prereqs.cross_StepFunctionsMessenger.end-->


<!--custom.scenarios.cross_StepFunctionsMessenger.start-->
<!--custom.scenarios.cross_StepFunctionsMessenger.end-->

#### Create a websocket chat application

This example shows you how to create a chat application that is served by a websocket API built on Amazon API Gateway.


<!--custom.scenario_prereqs.cross_ApiGatewayWebsocketChat.start-->
<!--custom.scenario_prereqs.cross_ApiGatewayWebsocketChat.end-->


<!--custom.scenarios.cross_ApiGatewayWebsocketChat.start-->
<!--custom.scenarios.cross_ApiGatewayWebsocketChat.end-->

#### Use API Gateway to invoke a Lambda function

This example shows you how to create an AWS Lambda function invoked by Amazon API Gateway.


<!--custom.scenario_prereqs.cross_LambdaAPIGateway.start-->
<!--custom.scenario_prereqs.cross_LambdaAPIGateway.end-->


<!--custom.scenarios.cross_LambdaAPIGateway.start-->
<!--custom.scenarios.cross_LambdaAPIGateway.end-->

#### Use scheduled events to invoke a Lambda function

This example shows you how to create an AWS Lambda function invoked by an Amazon EventBridge scheduled event.


<!--custom.scenario_prereqs.cross_LambdaScheduledEvents.start-->
<!--custom.scenario_prereqs.cross_LambdaScheduledEvents.end-->


<!--custom.scenarios.cross_LambdaScheduledEvents.start-->
<!--custom.scenarios.cross_LambdaScheduledEvents.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Python Lambda reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/lambda.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0