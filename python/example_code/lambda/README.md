# AWS Lambda code examples for the AWS SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to create, deploy, and invoke 
AWS Lambda functions. Learn to accomplish the following tasks:

* Create and deploy Lambda functions that can be invoked in different ways:
    * By an invoke call through Boto3
    * By Amazon API Gateway as the target of a REST request
    * By Amazon EventBridge on a schedule
* Create and deploy a REST API on Amazon API Gateway. The REST API targets a 
Lambda function to handle REST requests.
* Create a scheduled rule on Amazon EventBridge that targets a Lambda function.

*Lambda lets you run code without provisioning or managing servers. Upload your code 
and Lambda takes care of everything required to run and scale your code with high 
availability.*

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Create a function](lambda_basics.py)
(`CreateFunction`)
* [Delete a function](lambda_basics.py)
(`DeleteFunction`)
* [Get a function](lambda_basics.py)
(`GetFunction`)
* [Invoke a function](lambda_basics.py)
(`Invoke`)
* [List functions](lambda_basics.py)
(`ListFunctions`)
* [Update function code](lambda_basics.py)
(`UpdateFunctionCode`)
* [Update function configuration](lambda_basics.py)
(`UpdateFunctionConfiguration`)

### Scenario

* [Get started with functions](scenario_getting_started_functions.py)

### Cross-service

* [Use scheduled EventBridge events to invoke a function](scheduled_lambda.py)
* [Use API Gateway to invoke a function](api_gateway_rest.py)

## Running the examples

### Prerequisites

### Get started with functions

This interactive scenario runs at a command prompt and shows you how to use 
Lambda to do the following:

1. Create an IAM role that grants Lambda permission to write to logs.
1. Create a Lambda function and upload handler code.
1. Invoke the function with a single parameter and get results.
1. Update the function code and configure its Lambda environment with an environment
variable.
1. Invoke the function with new parameters and get results. Display the execution
log that's returned from the invocation.
1. List the functions for your account.
1. Delete the IAM role and the Lambda function. 

Start the scenario at a command prompt.

```
python scenario_getting_started_functions.py
```

### Use scheduled EventBridge events to invoke a function

This example creates an Amazon EventBridge rule that invokes a Lambda 
function on a schedule.

Run the example at a command prompt with the following command:

```
python scheduled_lambda.py
``` 

### Use API Gateway to invoke a function

This example creates an Amazon API Gateway REST API and makes a Lambda function the 
target of REST requests.

Run the example at a command prompt with the following command:

```
python api_gateway_rest.py
``` 

### Prerequisites

Prerequisites for running the examples for this service can be found in the 
[README](../../README.md#Prerequisites) in the Python folder.

## Tests

Instructions for running the tests for this service can be found in the
[README](../../README.md#Tests) in the Python folder.

## Additional resources
* [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
* [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
* [AWS SDK for Python Lambda Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/lambda.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
