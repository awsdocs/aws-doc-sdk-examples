# About these CDK apps

This document describes the resources created by the CDK apps
in this directory.

## go_example_lambda

This CDK app creates the following resource:

- An Amazon S3 bucket
- An Amazon DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, this CDK app creates notifications to
Amazon Lambda function that detect changes in those resources,
and handlers, in Go, that print messages to an AWS CloudWatch log.

## javascript_example_code_polly_aws_service

## javascript_example_code_transcribe_demo

## javascript_example_lambda_aws-service

## python_example_code_apigateway_aws_service

## python_example_code_apigateway_websocket

## python_example_code_stepfunctions_demo