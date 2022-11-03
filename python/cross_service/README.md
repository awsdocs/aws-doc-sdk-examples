# AWS SDK for Python cross-service examples 

## Overview

This README lists the cross-service examples available for the AWS SDK for 
Python (Boto3). Each folder in this directory contains the following cross-service 
examples. A README in each folder describes how to run the example.

A cross-service example is an application that works across multiple AWS services 
using the AWS SDK for Python.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Cross-service examples

* [AWS Chalice and AWS Lambda REST API example](apigateway_covid-19_tracker/README.md)
    
    Shows how to use AWS Chalice to create a serverless REST API that uses 
    Amazon API Gateway, AWS Lambda, and Amazon DynamoDB. The REST API simulates a 
    system that tracks daily cases of COVID-19 in the United States, using fictional 
    data.

    * Amazon API Gateway
    * AWS CloudFormation
    * Amazon DynamoDB
    * AWS Lambda

* [Amazon API Gateway websocket chat example](apigateway_websocket_chat/README.md)

    Shows how to use Amazon API Gateway V2 to create a websocket API that integrates 
    with AWS Lambda and Amazon DynamoDB.

    * Amazon API Gateway
    * Amazon DynamoDB
    * AWS Lambda

* [Track work items in an Aurora Serverless database](aurora_item_tracker/README.md)

    Shows how to create a REST service that lets you store work items in an 
    Amazon Aurora Serverless database and use Amazon Simple Email Service (Amazon SES) 
    to send email reports of work items.
    
    * Aurora
    * Amazon SES

* [Amazon Aurora Serverless REST API lending library example](aurora_rest_lending_library/README.md)

    Shows how to use the Amazon Relational Database Service (Amazon RDS) API and 
    AWS Chalice to create a REST API backed by an Amazon Aurora database. The web 
    service is fully serverless and represents a simple lending library where patrons 
    can borrow and return books.

    * Amazon API Gateway
    * AWS Lambda
    * Amazon RDS
    * AWS Secrets Manager

* [Track work items in a DynamoDB table](dynamodb_item_tracker/README.md)

    Shows how to create a REST service that lets you store work items in an 
    Amazon DynamoDB table and use Amazon Simple Email Service (Amazon SES) 
    to send email reports of work items.
    
    * Amazon DynamoDB
    * Amazon SES

* [Analyzing photos using Amazon Rekognition](photo_analyzer/README.md)

    Shows you how to create a web application that lets you upload photos to an 
    Amazon Simple Storage Service (Amazon S3) bucket, use Amazon Rekognition to analyze 
    and label the photos, and use Amazon Simple Email Service (Amazon SES) to send 
    email reports of image analysis.  

    * Amazon Rekognition
    * Amazon S3
    * Amazon SES

* [Moderate content using Amazon Rekognition with URL support](rekognition_content_moderation/README.md)

    Shows you how to create a Lambda function that analyzes images with Amazon 
    Rekognition to moderate content. The Lambda function is invoked by API Gateway
    so that you can POST content to the API Gateway URL to receive moderation data.
    
    * Amazon API Gateway
    * AWS CloudFormation
    * AWS Lambda
    * Amazon Rekognition

* [AWS Step Functions messenger example](stepfunctions_messenger/README.md)

    Shows how to use AWS Step Functions to create and run a state machine that 
    retrieves message records from Amazon DynamoDB and sends messages to an 
    Amazon Simple Queue Service (Amazon SQS) queue.

    * AWS CloudFormation
    * Amazon DynamoDB
    * AWS Lambda
    * Amazon SQS
    * AWS Step Functions

* [Detect entities in extracted text using a Jupyter notebook](textract_comprehend_notebook/README.md)

    Shows how to use a Jupyter notebook to detect entities in text that is extracted 
    from an image. This example uses Amazon Textract to extract text from an image 
    stored in Amazon Simple Storage Service (Amazon S3) and Amazon Comprehend to detect 
    entities in the extracted text.
    
    * Amazon Comprehend
    * Amazon S3
    * Amazon Textract 

* [Amazon Textract explorer example](textract_explorer/README.md)

    Shows how to use Amazon Textract to detect text, form, and table elements in a 
    document image. The input image and Textract output are shown in a Tkinter 
    application that lets you explore the detected elements. The application starts
    asynchronous jobs, publishes notifications to an Amazon Simple Notification 
    Service (Amazon SNS) topic when the job completes, and polls an Amazon Simple 
    Queue Service (Amazon SQS) queue for a job completion message and displays the 
    results. 

    * Amazon S3
    * Amazon SNS
    * Amazon SQS    
    * Amazon Textract

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0