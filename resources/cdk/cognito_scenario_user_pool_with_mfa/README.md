# Cognito user pool with MFA scenario resources

## Overview

Creates AWS resources for Amazon Cognito scenarios. The scripts in this
example create the following resources:
 
* An Amazon Cognito user pool that is configured to:
    * allow self sign-up
    * verify by email
    * require multi-factor authentication (MFA)
    * allow device tracking
* An associated client application that can authenticate with the user pool. 

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Deploying resources

You can use the AWS Cloud Development Kit (AWS CDK) or the AWS Command Line Interface
(AWS CLI) to deploy and destroy the resources for this example.

### Deploying with the AWS CDK

To deploy with the AWS CDK, you must install [Node.js](https://nodejs.org) and the 
[AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html).

This example was built and tested with AWS CDK 2.29.1.

Deploy AWS resources by running the following at a command prompt in the
`resources/cdk/cognito_scenario_user_pool_with_mfa` folder:

```
npm install
cdk deploy
```

The stack takes a few minutes to deploy. When it completes, it prints output like 
the following:

```
Outputs:
doc-example-cognito-scenario-signup-user-with-mfa.ClientId = 123456789eo6cet40krEXAMPLE
doc-example-cognito-scenario-signup-user-with-mfa.UserPoolId = us-west-2_CzEXAMPLE
```

### Deploying with the AWS CLI 

To deploy with the AWS CLI you must first install the 
[AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html).

1. Deploy AWS resources by running the following at a command prompt in the 
    `resources/cdk/cognito_scenario_user_pool_with_mfa` folder:
    
    ```
    aws cloudformation create-stack --template-body file://setup.yaml --stack-name YOUR_STACK_NAME
    ```
    
    *Note:* The stack name must be unique within an AWS Region and AWS account. You can 
    specify up to 128 characters, and numbers and hyphens are allowed.

2. The stack takes less than a minute to deploy. You can check status by running the following:

    ```
    aws cloudformation describe-stacks --stack-name YOUR_STACK_NAME
    ```
    
    When the stack is ready, it shows `StackStatus` of `CREATE_COMPLETE`.

3. You can get the outputs from the stack by running the following:

    ```
    aws cloudformation describe-stacks --stack-name STACK_NAME --query Stacks[0].Outputs --output text
    ```
    
    This results in output like the following: 
    
    ```
    UserPoolId      us-west-2_oBEXAMPLE
    ClientId        123456789unqtb9jq4bEXAMPLE
    ```

## Using the resources

After the user pool is created, you can use the AWS Management Console,
AWS CLI, or an AWS SDK to manage users or change settings.

## Destroying resources

### Destroying with the AWS CDK

You can use the AWS CDK to destroy the resources by running the following:

```
cdk destroy
```

### Destroying with the AWS CLI

You can use the AWS CLI to destroy the resources by running the following:

```
aws cloudformation delete-stack --stack-name YOUR_STACK_NAME
```

## Additional resources

* [AWS CDK V2 Developer Guide](https://docs.aws.amazon.com/cdk/v2/guide/home.html)
* [AWS CLI User Guide for Version 2](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
* [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
