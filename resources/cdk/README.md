# About these AWS Cloud Development Kit (AWS CDK) apps

This document describes the resources created by the AWS CDK apps in this directory.

These resources are for the following services:

- AWS Identity and Access Management (IAM)
- Amazon CloudWatch (CloudWatch)
- Amazon Cognito
- Amazon Comprehend
- Amazon DynamoDB (DynamoDB)
- Amazon Lex
- Amazon Simple Nofification Service (Amazon SNS)
- Amazon Simple Queue Service (Amazon SQS)
- Amazon Simple Storage Service (Amazon S3)
- Amazon Translate

For instructions on how to run any of these apps, see [Running a CDK app](#cdk).

## dynamodb_ruby_example_create_movies_table

This AWS CDK app creates the following resources:

- A DynamoDB table.

## dynamodb-ruby-example-create-users-table

This AWS CDK app creates the following resources:

- A DynamoDB table.

## go_example_lambda

This AWS CDK app creates the following resources:

- An Amazon S3 bucket
- A DynamoDB table
- An Amazon SNS topic
- An Amazon SQS queue

In addition, this AWS CDK app creates notifications to
Lambda function that detect changes in those resources,
and handlers, in Go, that print messages to a CloudWatch log.

For instructions on how to run any of these apps, see [.../cfn/go_example_lambda](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cfn/go_example_lambda).

## go-apprunner

This AWS CDK app creates the following resources:

- A Docker container
- IAM roles to enable running a Go app within the Docker container

## iam-ruby-example-add-new-user

This AWS CDK app creates the following resources:

- An IAM user

## javascript_example_code_polly_aws_service

This AWS CDK app creates the following resources:

- An IAM unauthenticated role with full
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

For more information on the example using these resources, see [Build a transcription app with authenticated users](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html).

## javascript_example_lambda_aws_service

This AWS CDK app creates the following resources:

- An Amazon S3 bucket with public access to the bucket's objects
- An IAM unauthenticated role based on an AWS identity
  that has permission to create a DynamoDB table, invoke an Lambda
  function, and create a mobile analytics event. 
- An Amazon Cognito identity pool with the role attached to it. 

For more information on the example using these resources, see [Tutorial: Creating and using Lambda functions](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html).

## kinesis-example-create-stream

This AWS CDK app creates the following resources:

- An Amazon Kinesis stream
- An Amazon Cognito identity pool
- An IAM role

## kinesis-iam-unauthenticated-role

This AWS CDK app creates the following resources:

- An Amazon Cognito identity pool
- An IAM role

## lambda_api_step_functions

This AWS CDK app creates the following resources:

- An Amazon Cognito identity pool
- An IAM role
- An AWS Step Functions instance

## lambda_using_api_gateway

This AWS CDK app creates the following resources:

- A DynamoDB table
- An IAM role with attached permissions to execute Lambda functions
- An Amazon S3 bucket to host Lambda function

For more information on the example using these resources, see [Tutorial: Invoking Lambda with API Gateway](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services/lambda-api-gateway).

## lambda_using_scheduled_events

This AWS CDK app creates the following resources:

- An Amazon S3 bucket
- An IAM role
- A DynamoDB table

## lex_bot_example_iam_unauth_role

This AWS CDK app creates the following resources:

- An IAM unauthenticated role with permissions 
  for Amazon Comprehend, Amazon Translate, and Amazon Lex.

## messaging-app-unauthenticated-role-sqs-fifo-queue

This AWS CDK app creates the following resources:

- An IAM unauthenticated role with permissions for Amazon SQS
- An Amazon SQS First In First Out (FIFO) queue.

## python_example_code_apigateway_aws_service

This AWS CDK app creates the following resources:

- A DynamoDB table with a `username` partition key
- an IAM role that enables Amazon API Gateway to read from and write to the table.
  
For more information on the example using these resources, see [...python/example_code/apigateway/aws_service](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/apigateway/aws_service).

## python_example_code_apigateway_websocket

This AWS CDK app creates the following resources:

- A DynamoDB table with a `connection_id` primary key.
- An IAM role and policy that grants
  Lambda permission to access the DynamoDB table and have basic rights to
  run functions.
- A Lambda function that runs on Python 3.7 and has an environment variable
  that contains the DynamoDB table name. The function code is updated as part
  of the example.

For more information on the example using these resources, see [...python/example_code/apigateway/aws_service](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/apigateway/websocket).

## python_example_code_secretsmanager_demo

This AWS CDK app creates the following resources:

- An Amazon Aurora (Aurora) serverless cluster

For more information on the example using these resources, see [...python/example_code/secretsmanager](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/secretsmanager).

## python_example_code_stepfunctions_demo

This AWS CDK app creates the following resources:

- A DynamoDB table with a `username` partition key
- An IAM role that enables API Gateway to read from and write to the table.

For more information on the example using these resources, see [...python/example_code/stepfunctions](  https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/stepfunctions).

## rekognition-sns-video-analyzer

This AWS CDK app creates the following resources:

- An IAM unauthenticated role with permissions for Amazon SQS, Amazon S3, and Amazon Rekognition
- An Amazon S3 bucket
- An Amazon SNS topic.

## rekognition-unauthrole

This AWS CDK app creates the following resources:

- An IAM role
- An Amazon Cognito identity pool

## s3-ruby-example-create-bucket

This AWS CDK app creates the following resources:

- An Amazon S3 bucket

## submit-data-app-unauthenticated-role

This AWS CDK app creates the following resources:

- A DynamoDB table
- An Amazon Cognito identity pool
- An IAM role

## textract_example_s3_sns_sqs

This AWS CDK app creates the following resources:

- An Amazon S3 bucket that grants Amazon Textract read-write permission
- An Amazon SNS topic
- An IAM role that can be assumed by Amazon Textract and grants permission to publish to the topic
- An Amazon SQS queue that is subscribed to receive messages from the topic

For more information on the example using these resources, see [...python/example_code/textract](  https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/textract).

## textract_example_s3_sns_sqs_cognito

This AWS CDK app creates the following resources:

- An Amazon S3 bucket that grants Amazon Textract read-write permission
- An Amazon SNS topic
- An IAM role that can be assumed by Amazon Textract and grants permission to publish to the topic
- An Amazon SQS queue that is subscribed to receive messages from the topic
- An Amazon Cognito user pool, identity pool, and authenticated user role that
  grants authenticated users permission to access Amazon Textract, the Amazon SQS
  queue, and the Amazon S3 bucket

For more information on the example using these resources, see [...javascriptv3/example_code/cross-services/textract-react](  https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services/textract-react).

## transcribe-streaming-unauth-role

This AWS CDK app creates the following resources:

- An IAM unauthenticated role with the following permissions:
  - Amazon SES: SendEmail
  - Amazon Transcribe: StartStreamTranscriptionWebSocket
  - Amazon Comprehend: DetectDominantLanguage
  - Amazon Translate: TranslateText

# Running a CDK app<a name="cdk"></a>

This section describes how to run any of these AWS CDK apps in this directory.
 
Inputs (replace in code):
- CLOUDFORMATION_TEMPLATE_NAME (For example, 'setup.yaml'.)
- STACK_NAME (For example, 'myDynamoDBStack')

 You can run a CDK app in several ways:

 1. To run this app with the AWS CDK, run the following command:
 
    npm install && cdk synth && cdk deploy

The AWS CDK app displays the names of the resources it creates in the output.

To destroy the generated AWS resources after you are finished using them, run the following command:

    cdk destroy

 **Note**: In some cases, such as when an Amazon S3 bucket is not empty, the AWS CDK app cannot destroy a resource.

     
 2. To run this app with the AWS Command Line Interface (AWS CLI):

    
    a. Run the following command to create an AWS CloudFormation template:

       npm install && cdk synth >CLOUDFORMATION_TEMPLATE_NAME 

    b. Run the following command to create a stack
       based on this AWS CloudFormation template. This stack
       will create the specified AWS resources.

       aws cloudformation create-stack --template-body file: CLOUDFORMATION_TEMPLATE_NAME --stack-name STACK_NAME

    c. To display the names of the generated resources, run the
       following command:

       aws cloudformation describe-stacks --stack-name STACK_NAME --query Stacks[0].Outputs --output text

       Note that the generated resources might not be immediately available.
       You can keep running this command until you see their names.

    d. To destroy the generated AWS resources after you are finished using them,
       run the following command:

       aws cloudformation delete-stack --stack-name STACK_NAME
       
 **Note**: In some cases, such as when an Amazon S3 bucket is not empty, the AWS CDK app cannot destroy a resource.

 3. To run this app with the AWS CloudFormation console:

    a. Run the following command to create an AWS CloudFormation template:

       npm install && cdk synth > CLOUDFORMATION_TEMPLATE_NAME
    b. Sign in to the AWS CloudFormation console, at:

       https:console.aws.amazon.com/cloudformation

    d. Choose Create stack, and then follow
       the on-screen instructions to create a stack based on this 
       AWS CloudFormation template. This stack will create the specified
       AWS resources.

       The names of the generated resources will display on the stack's
       Outputs tab in the console after the stack's status displays as
       CREATE_COMPLETE.

    e. To destroy the generated AWS resources after you are finished using them,
       choose the stack in the console, choose Delete, and then follow
       the on-screen instructions.

 **Note**: In some cases, such as when an Amazon S3 bucket is not empty, the AWS CDK app cannot destroy a resource.
