# Lambda code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to create, deploy, and invoke
AWS Lambda functions. Learn to accomplish the following tasks

* Create, deploy, and invoke Lambda function.
* Update function code or configuration options.
* List all existing functions.

*With AWS Lambda, you can run code without provisioning or managing servers. You pay only for the compute time that 
you consume—there's no charge when your code isn't running.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a function](LambdaService.php)
  (`CreateFunction`)
* [Delete a function](LambdaService.php)
  (`DeleteFunction`)
* [Get a function](LambdaService.php)
  (`GetFunction`)
* [Invoke a function](LambdaService.php)
  (`Invoke`)
* [List functions](LambdaService.php)
  (`ListFunctions`)
* [Update function code](LambdaService.php)
  (`UpdateFunctionCode`)
* [Update function configuration](LambdaService.php)
  (`UpdateFunctionConfiguration`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Get started with functions](GettingStartedWithLambda.php)

## Run the examples

### Get started with functions

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

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured as described in
  the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- PHP 7.1 or later.
- Composer installed.

## Additional resources

* [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
* [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
* [AWS SDK for PHP Lambda Client](https://docs.aws.amazon.com/aws-sdk-php/v3/api/class-Aws.Lambda.LambdaClient.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
