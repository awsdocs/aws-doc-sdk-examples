# AWS SDK for Kotlin code examples 

## Purpose

These examples demonstrate how to perform AWS service operations using the beta 0.9.4 version of the AWS SDK for Kotlin.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [AWS SDK for Kotlin
Developer Guide](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide).

## Running the code

See the individual readme files in each service directory for information about specific code examples for that service.

## Usecases

In the **usecases** folder, find step-by-step development tutorials that use multiple AWS services and the AWS SDK for Kotlin. By following these tutorials, you will gain a deeper understanding of how to create Kotlin applications that use the AWS SDK for Kotlin. These tutorials include:

+ [Create a React and Spring REST application that queries Amazon DynamoDB data](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/usecases/itemtracker_dynamodb) - Discusses how to develop a Spring REST API that queries Amazon DynamoDB data. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data.

+ [Create a React and Spring REST application that queries Amazon Redshift data](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/creating_redshift_application) - Discusses how to develop a Spring REST API that queries Amazon Redshift data. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data.

+ [Create a React and Spring REST application that queries Amazon Aurora data](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/serverless_rds) - Discusses how to develop a Spring REST API that queries Amazon Aurora data. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data.

+ [Create an example photo analyzer application using the AWS SDK for Kotlin](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/creating_photo_analyzer_app) - Discusses using the AWS SDK for Kotlin and various AWS services, such as the Amazon Rekognition service, to analyze images. This application analyzes many images and generates a report that breaks down each image into a series of labels.

+ [Create AWS serverless workflows using the AWS SDK for Kotlin](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/creating_workflows_stepfunctions) - Discusses using the AWS SDK for Kotlin and AWS Step Functions to create a workflow that invokes AWS services. Each workflow step is implemented by using an AWS Lambda function.

+ [Create a Spring Boot Application that has publish-subscribe functionality](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/subpub_app) - Discusses how to create a web application that has subscription and publish functionality. In this tutorial, the Spring Framework along with AWS SDK for Kotlin is used to create the application.

+ [Create a publish-subscribe Android application that translates messages using the AWS SDK for Kotlin](https://github.com/scmacdon/aws-doc-sdk-examples/tree/main/kotlin/usecases/subpub_app_android) - Discusses how to create an Android application that has subscription and publish functionality.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
