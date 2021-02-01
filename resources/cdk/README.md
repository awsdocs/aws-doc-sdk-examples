# About these CDK apps

This document describes the resources created by the CDK apps
in this directory.

## go_example_lambda

This CDK app creates the following resources:

- An Amazon S3 bucket
- An Amazon DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, this CDK app creates notifications to
Amazon Lambda function that detect changes in those resources,
and handlers, in Go, that print messages to an AWS CloudWatch log.

## javascript_example_code_polly_aws_service

This CDK app creates the following resources:

- An AWS Identity and Access Management (AWS IAM) unauthenticated role with full access to Amazon Polly. 
- An Amazon Cognito identity pool with the Amazon IAM unauthenticated role attached to it.

## javascript_example_code_transcribe_demo

This CDK app creates the following resources:

- An Amazon Cognito identity pool with an authenticated user role.
- An IAM policy with permissions for the Amazon S3 and Amazon Transcribe is attached to the authenticated user role.
- An Amazon Cognito user pool that enables users to sign up and sign in to the app.
- An Amazon S3 bucket to host the application files.
- An Amazon S3 bucket to to store the transcriptions.

## javascript_example_lambda_aws-service

This CDK app creates the following resources:

- An Amazon S3 bucket with public access to the bucket's objects
- An AWS Identity and Access Management (AWS IAM) unauthenticated role based on an AWS identity
  that has permission to create an AWS DynamoDB table, invoke an AWS Lambda function,
  and create a mobile analytics event. 
- An Amazon Cognito identity pool with the role attached to it. 

## python_example_code_apigateway_aws_service

This CDK app creates the following resources:

- An Amazon DynamoDB table with a
  `username` partition key
- an AWS Identity and Access Management (IAM) role
  that enables Amazon API Gateway to read from and write to the table.

## python_example_code_apigateway_websocket

This CDK app creates the following resources:

- An Amazon DynamoDB table with a `connection_id` primary key.
- An AWS Identity and Access Management (IAM) role and policy that grants
  AWS Lambda permission to access the DynamoDB table and have basic rights to
  run functions.
- A Lambda function that runs on Python 3.7 and has an environment variable
  that contains the DynamoDB table name. The function code is updated as part
  of the example.

## python_example_code_secretsmanager_demo

This CDK app creates the following resources:

- An Amazon Aurora serverless cluster

## python_example_code_stepfunctions_demo

This CDK app creates the following resources:

- An Amazon DynamoDB table with a
  `username` partition key
- An AWS Identity and Access Management (IAM) role
  that enables s Amazon API Gateway to read from and write to the table.