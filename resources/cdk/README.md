# About these AWS Cloud Development Kit (AWS CDK) apps

This document describes the resources created by the AWS CDK apps in this directory.

For instructions on how to run any of these apps, see [Running a CDK app](#cdk).

## lex_bot_example_iam_unauth_role
This AWS CDK app creates the following resources:
- An AWS Identity and Access Management (IAM) unauthenticated role with permissions 
  for Amazon Comprehend, Amazon Translate, and Amazon Lex.

## go_example_lambda

This AWS CDK app creates the following resources:

- An Amazon Simple Storage Service (Amazon S3) bucket
- An Amazon DynamoDB (DynamoDB) table
- An Amazon Simple Notification Service (Amazon SNS) topic
- An Amazon Simple Queue Service (Amazon SQS) queue

In addition, this AWS CDK app creates notifications to
Amazon Lambda (Lambda) function that detect changes in those resources,
and handlers, in Go, that print messages to an Amazon CloudWatch (CloudWatch) log.

For instructions on how to run any of these apps, see [.../cfn/go_example_lambda](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cfn/go_example_lambda).

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


## javascript_example_lambda_aws-service

This AWS CDK app creates the following resources:

- An Amazon S3 bucket with public access to the bucket's objects
- An IAM unauthenticated role based on an AWS identity
  that has permission to create a DynamoDB table, invoke an Lambda
  function, and create a mobile analytics event. 
- An Amazon Cognito identity pool with the role attached to it. 

For more information on the example using these resources, see [Tutorial: Creating and using Lambda functions](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html).



## lambda-using-api-gateway

This AWS CDK app creates the following resources:

- An Amazon DynamoDB table.
- An IAM role with attached permissions to execute Lambda functions.
- An Amazon S3 bucket to host Lambda function. 

For more information on the example using these resources, see [Tutorial: Invoking Lambda with API Gateway](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services/lambda-api-gateway).

## python_example_code_apigateway_aws_service

This AWS CDK app creates the following resources:

- A DynamoDB table with a
  `username` partition key
- an IAM role
  that enables Amazon API Gateway to read from and write to the table.
  
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

- A DynamoDB table with a
  `username` partition key
- An IAM role
  that enables API Gateway to read from and write to the table.

For more information on the example using these resources, see [...python/example_code/stepfunctions](  https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/stepfunctions).

## textract_example_s3_sns_sqs

This AWS CDK app creates the following resources:

* An Amazon S3 bucket that grants Amazon Textract read-write permission.
* An Amazon SNS topic.
* An IAM role that can be assumed by Textract and grants permission to publish to 
the topic.
* An Amazon SQS queue that is subscribed to receive messages from the topic.

For more information on the example using these resources, see [...python/example_code/textract](  https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/textract).
  
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

 **Note**: In some cases, such as when an S3 bucket is not empty, the AWS CDK app cannot destroy a resource.

     
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
       
 **Note**: In some cases, such as when an S3 bucket is not empty, the AWS CDK app cannot destroy a resource.

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

 **Note**: In some cases, such as when an S3 bucket is not empty, the AWS CDK app cannot destroy a resource.
