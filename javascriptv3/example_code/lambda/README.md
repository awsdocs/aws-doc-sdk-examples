# Lambda code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS Lambda.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lambda](hello.js#L6) (`ListFunctions`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](../iam/actions/attach-role-policy.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](actions/create-function.js#L15)
- [DeleteFunction](actions/delete-function.js#L5)
- [GetFunction](actions/get-function.js#L5)
- [Invoke](actions/invoke.js#L5)
- [ListFunctions](actions/list-functions.js#L5)
- [UpdateFunctionCode](actions/update-function-code.js#L15)
- [UpdateFunctionConfiguration](actions/update-function-configuration.js#L14)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Automatically confirm known users with a Lambda function](../cross-services/wkflw-pools-triggers/index.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Lambda

This example shows you how to get started using Lambda.

```bash
node ./hello.js
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


<!--custom.basics.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.basics.lambda_Scenario_GettingStartedFunctions.end-->


#### Automatically confirm known users with a Lambda function

This example shows you how to automatically confirm known Amazon Cognito users with a Lambda function.

- Configure a user pool to call a Lambda function for the <code>PreSignUp</code> trigger.
- Sign up a user with Amazon Cognito.
- The Lambda function scans a DynamoDB table and automatically confirms known users.
- Sign in as the new user, then clean up resources.

<!--custom.scenario_prereqs.cross_CognitoAutoConfirmUser.start-->
<!--custom.scenario_prereqs.cross_CognitoAutoConfirmUser.end-->


<!--custom.scenarios.cross_CognitoAutoConfirmUser.start-->
<!--custom.scenarios.cross_CognitoAutoConfirmUser.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for JavaScript (v3) Lambda reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/lambda)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0