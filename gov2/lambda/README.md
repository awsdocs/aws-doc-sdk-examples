# Lambda code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with AWS Lambda.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Lambda](hello/hello.go#L4) (`ListFunctions`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/scenario_get_started_functions.go)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateFunction](actions/functions.go#L47)
- [DeleteFunction](actions/functions.go#L155)
- [GetFunction](actions/functions.go#L29)
- [Invoke](actions/functions.go#L169)
- [ListFunctions](actions/functions.go#L134)
- [UpdateFunctionCode](actions/functions.go#L90)
- [UpdateFunctionConfiguration](actions/functions.go#L118)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Automatically confirm known users with a Lambda function](../workflows/user_pools_and_lambda_triggers/workflows/scenario_auto_confirm_trusted_accounts.go)
- [Automatically migrate known users with a Lambda function](../workflows/user_pools_and_lambda_triggers/workflows/scenario_migrate_user.go)
- [Write custom activity data with a Lambda function after Amazon Cognito user authentication](../workflows/user_pools_and_lambda_triggers/workflows/scenario_activity_log.go)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Lambda

This example shows you how to get started using Lambda.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
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

#### Automatically migrate known users with a Lambda function

This example shows you how to automatically migrate known Amazon Cognito users with a Lambda function.

- Configure a user pool to call a Lambda function for the <code>MigrateUser</code> trigger.
- Sign in to Amazon Cognito with a username and email that is not in the user pool.
- The Lambda function scans a DynamoDB table and automatically migrates known users to the user pool.
- Perform the forgot password flow to reset the password for the migrated user.
- Sign in as the new user, then clean up resources.

<!--custom.scenario_prereqs.cross_CognitoAutoMigrateUser.start-->
<!--custom.scenario_prereqs.cross_CognitoAutoMigrateUser.end-->


<!--custom.scenarios.cross_CognitoAutoMigrateUser.start-->
<!--custom.scenarios.cross_CognitoAutoMigrateUser.end-->

#### Write custom activity data with a Lambda function after Amazon Cognito user authentication

This example shows you how to write custom activity data with a Lambda function after Amazon Cognito user authentication.

- Use administrator functions to add a user to a user pool.
- Configure a user pool to call a Lambda function for the <code>PostAuthentication</code> trigger.
- Sign the new user in to Amazon Cognito.
- The Lambda function writes custom information to CloudWatch Logs and to an DynamoDB table.
- Get and display custom data from the DynamoDB table, then clean up resources.

<!--custom.scenario_prereqs.cross_CognitoCustomActivityLog.start-->
<!--custom.scenario_prereqs.cross_CognitoCustomActivityLog.end-->


<!--custom.scenarios.cross_CognitoCustomActivityLog.start-->
<!--custom.scenarios.cross_CognitoCustomActivityLog.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Go V2 Lambda reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/lambda)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0