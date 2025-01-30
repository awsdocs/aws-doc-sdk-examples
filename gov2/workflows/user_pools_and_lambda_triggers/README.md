# Customize Amazon Cognito authentication behavior with Lambda functions

## Overview

This example shows how to use AWS SDKs to customize Amazon Cognito authentication behavior. You can configure
your Amazon Cognito user pool to automatically invoke AWS Lambda functions at various points in the authentication
process, such as before sign-up, during sign-in, and after authentication.

There are three workflows demonstrated by this example:

* Automatically confirm and verify the email of known users by using a pre sign-up trigger.
* Automatically add known users at sign-in by using a migrate user trigger.
* Write custom information to an Amazon DynamoDB table after users are authenticated by using a post authentication trigger.

These workflows are described in more detail in the main [README](../../scenarios/features/user_pools_and_lambda_triggers/README.md) 
for these examples.

## Automatically confirm known users

A [pre sign-up Lambda trigger](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html)
is invoked when a user starts the sign-up process and lets your Lambda function
take action before Amazon Cognito adds the user to the user pool.

## Automatically migrate known users

A [migrate user Lambda trigger](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-migrate-user.html)
is invoked when a user doesn't exist in the user pool at sign-in with a password.
After the Lambda function returns successfully, Amazon Cognito creates the user in the user pool.

## Write custom activity after authentication

A [post authentication Lambda trigger](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-post-authentication.html)
is invoked after signing in a user, so you can add custom logic after Amazon Cognito authenticates the user.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Run the examples

### Prerequisites

For general prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.

### Setup

This example deploys several resources by using an AWS CloudFormation stack. This stack
deploys the following resources:

* An Amazon DynamoDB table named `doc-example-custom-users` that has a `UserEmail` primary key.
  This table functions as an external user store.
* An Amazon Cognito user pool that requires an email, sends an email with verification code
  when a new user is added, does not require MFA, and allows account recovery with a verified email.
* An Amazon Cognito client application. This is required for client calls to sign-up and
  authentication users.
* An AWS Identity and Access Management (IAM) role that can be assumed by Lambda.
  This role grants permission to Lambda to read from and write to the DynamoDB table and
  write to CloudWatch Logs.

### Deploy AWS resources

The AWS resources for this example are deployed by using the AWS Cloud Development Kit (AWS CDK).

To install the AWS CDK, follow the instructions in the
[Developer Guide](https://docs.aws.amazon.com/cdk/v2/guide/home.html).

Deploy resources at a command prompt from the [.cdk](.cdk) folder:

```
npm install
cdk deploy
```

###  Instructions

These scenarios can be run with the `cmd` runner.

```
go run ./cmd -scenario [auto_confirm migrate_user activity_log]
```

### Cleanup

Delete resources deployed for this example by deleting the stack.

Delete the stack at a command prompt from the [.cdk](.cdk) folder:

```
cdk destroy
```

## Additional resources

- [Amazon Cognito Identity Provider Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [SDK for Go V2 Amazon Cognito Identity Provider reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/cognito-identity-provider)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
