# About these CDK apps

This document describes the resources created by the
AWS Cloud Development Kit (AWS CDK)
apps in this directory.

## go_example_lambda

This AWS CDK app creates the following resources:

- An Amazon Simple Storage Service (Amazon S3) bucket
- An Amazon DynamoDB (DynamoDB) table
- An Amazon Simple Notification Service (Amazon SNS) topic
- An Amazon Simple Queue Service (Amazon SQS) queue

In addition, this AWS CDK app creates notifications to
Amazon Lambda (Lambda) function that detect changes in those resources,
and handlers, in Go, that print messages to an Amazon CloudWatch (CloudWatch) log.

## javascript_example_code_polly_aws_service

This AWS CDK app creates the following resources:

- An AWS Identity and Access Management (IAM) unauthenticated role with full
  access to Amazon Polly. 
- An Amazon Cognito identity pool with the IAM unauthenticated role attached to
  it.

## javascript_example_code_transcribe_demo

This AWS CDK app creates the following resources:

- An Amazon Cognito identity pool with an authenticated user role.
- An IAM policy with permissions for the Amazon S3 and Amazon Transcribe is
  attached to the authenticated user role.
- An Amazon Cognito user pool that enables users to sign up and sign in to the
  app.
- An Amazon S3 bucket to host the application files.
- An Amazon S3 bucket to to store the transcriptions.

## javascript_example_lambda_aws-service

This AWS CDK app creates the following resources:

- An Amazon S3 bucket with public access to the bucket's objects
- An IAM unauthenticated role based on an AWS identity
  that has permission to create a DynamoDB table, invoke an Lambda
  function,
  and create a mobile analytics event. 
- An Amazon Cognito identity pool with the role attached to it. 

## python_example_code_apigateway_aws_service

This AWS CDK app creates the following resources:

- A DynamoDB table with a
  `username` partition key
- an IAM role
  that enables Amazon API Gateway to read from and write to the table.

## python_example_code_apigateway_websocket

This AWS CDK app creates the following resources:

- A DynamoDB table with a `connection_id` primary key.
- An IAM role and policy that grants
  Lambda permission to access the DynamoDB table and have basic rights to
  run functions.
- A Lambda function that runs on Python 3.7 and has an environment variable
  that contains the DynamoDB table name. The function code is updated as part
  of the example.

## python_example_code_secretsmanager_demo

This AWS CDK app creates the following resources:

- An Amazon Aurora (Aurora) serverless cluster

## python_example_code_stepfunctions_demo

This AWS CDK app creates the following resources:

- A DynamoDB table with a
  `username` partition key
- An IAM role
  that enabless API Gateway to read from and write to the table.
