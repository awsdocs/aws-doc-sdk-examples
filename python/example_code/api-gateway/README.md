# API Gateway code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon API Gateway.

<!--custom.overview.start-->
<!--custom.overview.end-->

_API Gateway enables you to create and deploy your own REST and WebSocket APIs at any scale._

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

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDeployment](aws_service/aws_service.py#L189)
- [CreateResource](aws_service/aws_service.py#L77)
- [CreateRestApi](aws_service/aws_service.py#L43)
- [DeleteRestApi](aws_service/aws_service.py#L260)
- [GetResources](aws_service/aws_service.py#L25)
- [GetRestApis](aws_service/aws_service.py#L231)
- [PutIntegration](aws_service/aws_service.py#L100)
- [PutIntegrationResponse](aws_service/aws_service.py#L25)
- [PutMethod](aws_service/aws_service.py#L25)
- [PutMethodResponse](aws_service/aws_service.py#L25)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a REST API to track COVID-19 data](../../cross_service/apigateway_covid-19_tracker)
- [Create a lending library REST API](../../cross_service/aurora_rest_lending_library)
- [Create a websocket chat application](../../cross_service/apigateway_websocket_chat)
- [Create and deploy a REST API](aws_service/aws_service.py)
- [Use API Gateway to invoke a Lambda function](../../example_code/lambda)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



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

#### Create a websocket chat application

This example shows you how to create a chat application that is served by a websocket API built on Amazon API Gateway.


<!--custom.scenario_prereqs.cross_ApiGatewayWebsocketChat.start-->
<!--custom.scenario_prereqs.cross_ApiGatewayWebsocketChat.end-->


<!--custom.scenarios.cross_ApiGatewayWebsocketChat.start-->
<!--custom.scenarios.cross_ApiGatewayWebsocketChat.end-->

#### Create and deploy a REST API

This example shows you how to do the following:

- Create a REST API served by API Gateway.
- Add resources to the REST API to represent a user profile.
- Add integration methods so that the REST API uses a DynamoDB table to store user profile data.
- Send HTTP requests to the REST API to add and retrieve user profiles.

<!--custom.scenario_prereqs.api-gateway_Usage_CreateDeployRest.start-->
<!--custom.scenario_prereqs.api-gateway_Usage_CreateDeployRest.end-->

Start the example by running the following at a command prompt:

```
python aws_service/aws_service.py
```


<!--custom.scenarios.api-gateway_Usage_CreateDeployRest.start-->
For additional instructions on how to set up and run this example, see the 
[README](aws_service/README.md) in the `aws_service` folder.
<!--custom.scenarios.api-gateway_Usage_CreateDeployRest.end-->

#### Use API Gateway to invoke a Lambda function

This example shows you how to create an AWS Lambda function invoked by Amazon API Gateway.


<!--custom.scenario_prereqs.cross_LambdaAPIGateway.start-->
<!--custom.scenario_prereqs.cross_LambdaAPIGateway.end-->


<!--custom.scenarios.cross_LambdaAPIGateway.start-->
<!--custom.scenarios.cross_LambdaAPIGateway.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [API Gateway Developer Guide](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)
- [API Gateway API Reference](https://docs.aws.amazon.com/apigateway/latest/api/API_Operations.html)
- [SDK for Python API Gateway reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/apigateway.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0