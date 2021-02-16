#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// * A user in AWS Identity and Access Management (IAM).
//
// You can run this app instead of running equivalent AWS SDK for Ruby
// code examples elsewhere in this repository, such as:
//
// * iam-ruby-example-add-new-user.rb
//
// You can run this app in several ways:
//
// 1. To run this app with the AWS Cloud Development Kit (AWS CDK), run the
//    following command:
// 
//    npm install && cdk synth && cdk deploy --parameters UserName=my-user --parameters InitialPassword=my-!p@55w0rd!
//
//    You can replace the UserName and InitialPassword values with your own.
//
//    The names of the generated AWS resources will display in the output.
//
//    To destroy the generated AWS resources after you are finished using them,
//    run the following command:
//
//    cdk destroy
//
// 2. To run this app with the AWS Command Line Interface (AWS CLI):
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > iam-ruby-example-add-new-user.yaml
//
//    c. Run the following command to create a stack
//       based on this AWS CloudFormation template. This stack
//       will create the specified AWS resources.
//
//       aws cloudformation create-stack --template-body file://iam-ruby-example-add-new-user.yaml --capabilities CAPABILITY_NAMED_IAM --stack-name IamRubyExampleAddNewUserStack --parameters ParameterKey=UserName,ParameterValue=my-user ParameterKey=InitialPassword,ParameterValue=my-!p@55w0rd!
//
//       You can replace the UserName and InitialPassword values with your own.
//
//    d. To display the names of the generated resources, run the
//       following command:
//
//       aws cloudformation describe-stacks --stack-name IamRubyExampleAddNewUserStack --query Stacks[0].Outputs --output text
//
//       Note that the generated resources might not be immediately available.
//       You can keep running this command until you see their names.
//
//    e. To destroy the generated AWS resources after you are finished using them,
//       run the following command:
//
//       aws cloudformation delete-stack --stack-name IamRubyExampleAddNewUserStack
//
// 3. To run this app with the AWS CloudFormation console:
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > iam-ruby-example-add-new-user.yaml
//
//    c. Sign in to the AWS CloudFormation console, at:
//
//       https://console.aws.amazon.com/cloudformation
//
//    d. Choose Create stack, and then follow
//       the on-screen instructions to create a stack based on this 
//       AWS CloudFormation template. This stack will create the specified
//       AWS resources.
//
//       The names of the generated resources will display on the stack's
//       Outputs tab in the console after the stack's status displays as
//       CREATE_COMPLETE.
//
//    e. To destroy the generated AWS resources after you are finished using them,
//       choose the stack in the console, choose Delete, and then follow
//       the on-screen instructions.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as iam from '@aws-cdk/aws-iam' // npm install @aws-cdk/aws-iam

export class IamRubyExampleAddNewUserStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Get the name of the new user from the caller.
    const userName = new cdk.CfnParameter(this, 'UserName', {
      type: 'String',
      description: 'The name of the user to be created.'});

    // Get the initial sign-in password for the new user from the caller.
    const initialPassword = new cdk.CfnParameter(this, 'InitialPassword', {
      type: 'String',
      description: 'The initial sign-in password for the user.'});

    const secretValue = cdk.SecretValue.plainText(initialPassword.valueAsString);

    // Create the new user.
    const user = new iam.User(this, 'user', {
      userName: userName.valueAsString,
      password: secretValue,
      passwordResetRequired: true // Require the new user's password to be reset after initial sign-in.
    });

    // Confirm the new user's name and initial sign-in password by outputting their values.
    new cdk.CfnOutput(this, 'Name', {
      value: user.userName});

    new cdk.CfnOutput(this, 'InitialSignInPassword', {
      value: secretValue.toString()});
  }
}

const app = new cdk.App();
new IamRubyExampleAddNewUserStack(app, 'IamRubyExampleAddNewUserStack');
