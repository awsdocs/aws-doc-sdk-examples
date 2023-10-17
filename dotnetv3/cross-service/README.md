# AWS SDK for .NET (v3) cross-service examples

This README lists the cross-service examples available for the AWS SDK for 
.NET (v3). Each folder in this directory contains the following cross-service 
examples. A README in each folder describes how to run the example.

A cross-service example is an application that works across multiple AWS services 
using the AWS SDK for .NET.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Cross-service examples

### [Feedback Sentiment Analyzer application](FeedbackSentimentAnalyzer/README.md)

Create a Feedback Sentiment Analyzer (FSA) example app that analyzes and stores customer feedback cards. Specifically,
it fulfills the need of a fictitious hotel in New York City. The hotel receives feedback
from guests in various languages in the form of physical comment cards. That feedback
is uploaded into the app through a web client.

The application uses the following services:

- Amazon Textract
- Amazon Translate
- Amazon Comprehend
- Amazon Simple Storage Service (Amazon S3)
- Amazon Polly
- AWS Lambda
- Amazon Cognito
- Amazon API Gateway

### [Serverless photo asset management application](PhotoAssetManager/README.md)

Create a Photo Asset Management (PAM) example app that uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they are retrieved from Amazon S3, zipped, and the user is sent a link to the zip.

The application uses the following services:

- Amazon Rekognition
- Amazon DynamoDB
- Amazon Simple Storage Service (Amazon S3)
- Amazon Simple Notification Service (Amazon SNS)
- AWS Lambda
- Amazon Cognito
- Amazon API Gateway

### [Amazon Aurora work item tracker web application](AuroraItemTracker/Readme.md)

Shows how to create a web application that tracks work items in an Amazon Aurora database
and emails reports by using Amazon Simple Email Service (Amazon SES).

The application uses the following services:

- Amazon Aurora
- Amazon Relational Database Service (Amazon RDS)
- Amazon Simple Email Service (Amazon SES)

### [Subscribe, publish, and translate example](SubscribePublishTranslate/Readme.md).

Create an ASP .NET application that allows users to subscribe to an Amazon Simple Notification Service (Amazon SNS)
queue and to publish messages with optional translation to French or Spanish.

The application uses the following services:

  - Amazon SNS
  - Amazon Translate

### [Amazon DynamoDB work item tracker web application](DynamodbWebApp/Readme.md)

Shows how to create a web application that tracks work items in DynamoDB and emails 
reports by using Amazon SES.

The application uses the following services:

- DynamoDB
- Amazon SES
- AWS Identity and Access Management (IAM)

### [Amazon Rekognition photo analyzer application](PhotoAnalyzerApp/Readme.md)

Shows how to create a web application that analyzes photos uploaded to an Amazon Simple Storage Service (Amazon S3) bucket, using Amazon Rekognition to label the photos, and Amazon SES to send email reports of the image analysis.

The application uses the following services:

- Amazon Rekognition
- Amazon S3
- Amazon SES

---

Copyright (c) Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0