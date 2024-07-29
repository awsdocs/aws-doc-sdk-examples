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

- [Get started with functions](lambda_handler_basic.py)

### Cross-service examples

Sample applications that work across multiple AWS services.

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


#### Get started with functions

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.end-->

Start the example by running the following at a command prompt:

```
python lambda_handler_basic.py
```


<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.end-->

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