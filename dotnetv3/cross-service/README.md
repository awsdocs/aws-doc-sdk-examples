# AWS SDK for .NET 3.5+ cross-service examples

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

### [Amazon Aurora work item tracker web application](AuroraItemTracker/Readme.md)

Shows how to create a web application that tracks work items in an Amazon Aurora database
and emails reports by using Amazon SES.

The application uses the following services:

- Amazon Aurora
- Amazon RDS
- Amazon SES

### [Subscribe, publish, and translate example](SubscribePublishTranslate/Readme.md).

Create an ASP .NET application that allows users to subscribe to an Amazon SNS
queue and to publish messages with optional translation to French or Spanish.

The application uses the following services:

  - Amazon Simple Notification Service (Amazon SNS)
  - Amazon Translation Service

### [Amazon DynamoDB work item tracker web application](DynamodbWebApp/Readme.md)

Shows how to create a web application that tracks work items in DynamoDB and emails 
reports by using Amazon SES.

The application uses the following services:

- Amazon DynamoDB
- Amazon SES
- AWS Identity and Access Management (IAM)

### [Amazon Rekognition photo analyzer application](PhotoAnalyzerApp/Readme.md)

Shows how to create a web application that analyzes photos uploaded to an Amazon Simple Storage Service (Amazon S3) bucket, using Amazon Rekognition to label the photos, and Amazon Simple Email Service (Amazon SES) to send email reports of the image analysis.

The application sues the following services:

- Amazon Rekognition
- Amazon S3
- Amazon SES

---

Copyright (c) Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0