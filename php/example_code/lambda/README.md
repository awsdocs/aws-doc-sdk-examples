# Lambda code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with AWS Lambda.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](GettingStartedWithLambda.php)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](LambdaService.php#L31)
- [DeleteFunction](LambdaService.php#L108)
- [GetFunction](LambdaService.php#L50)
- [Invoke](LambdaService.php#L75)
- [ListFunctions](LambdaService.php#L59)
- [UpdateFunctionCode](LambdaService.php#L86)
- [UpdateFunctionConfiguration](LambdaService.php#L97)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a serverless application to manage photos](../../applications/photo_asset_manager)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

This interactive scenario runs at a command prompt and shows you how to use
AWS Lambda to do the following:

1. Create an AWS Identity and Access Management (IAM) role that grants Lambda
   permission to write to logs.
1. Create a Lambda function and upload handler code.
1. Invoke the function with a single parameter and get results.
1. Update the function code and configure its Lambda environment with an environment
   variable.
1. Invoke the function with new parameters and get results. Display the execution
   log that's returned from the invocation.
1. List the functions for your account.
1. Delete the IAM role and the Lambda function.

Install dependencies by using Composer:

```
composer install
```

After your composer dependencies are installed, you can run the interactive getting started file directly by using the
following command from the `aws-doc-sdk-examples\php\Lambda\` directory:

```
php Runner.php
```

Alternatively, you can have the choices automatically selected by running the file as part of a PHPUnit test with the
following:

```
..\..\vendor\bin\phpunit LambdaBasicsTests.php
```

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


#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for PHP Lambda reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Lambda.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0