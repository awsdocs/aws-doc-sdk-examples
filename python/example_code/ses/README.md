# Amazon SES code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Simple Email Service (Amazon SES).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SES is a reliable, scalable, and cost-effective email service._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateReceiptFilter](ses_receipt_handler.py#L36)
- [CreateReceiptRule](ses_receipt_handler.py#L162)
- [CreateReceiptRuleSet](ses_receipt_handler.py#L102)
- [CreateTemplate](ses_templates.py#L72)
- [DeleteIdentity](ses_identities.py#L116)
- [DeleteReceiptFilter](ses_receipt_handler.py#L86)
- [DeleteReceiptRule](ses_receipt_handler.py#L229)
- [DeleteReceiptRuleSet](ses_receipt_handler.py#L250)
- [DeleteTemplate](ses_templates.py#L99)
- [DescribeReceiptRuleSet](ses_receipt_handler.py#L208)
- [GetIdentityVerificationAttributes](ses_identities.py#L91)
- [GetTemplate](ses_templates.py#L117)
- [ListIdentities](ses_identities.py#L132)
- [ListReceiptFilters](ses_receipt_handler.py#L67)
- [ListTemplates](ses_templates.py#L142)
- [SendEmail](ses_email.py#L65)
- [SendTemplatedEmail](ses_email.py#L108)
- [UpdateTemplate](ses_templates.py#L161)
- [VerifyDomainIdentity](ses_identities.py#L30)
- [VerifyEmailIdentity](ses_identities.py#L55)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Copy email and domain identities across Regions](ses_replicate_identities.py)
- [Create a web application to track DynamoDB data](../../cross_service/dynamodb_item_tracker)
- [Create an Aurora Serverless work item tracker](../../cross_service/aurora_item_tracker)
- [Detect objects in images](../../cross_service/photo_analyzer)
- [Generate credentials to connect to an SMTP endpoint](ses_generate_smtp_credentials.py)
- [Verify an email identity and send messages](ses_email.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Copy email and domain identities across Regions

This example shows you how to copy Amazon SES email and domain identities from one AWS Region to another. When domain identities are managed by Route 53, verification records are copied to the domain for the destination Region.


<!--custom.scenario_prereqs.ses_Scenario_ReplicateIdentities.start-->
<!--custom.scenario_prereqs.ses_Scenario_ReplicateIdentities.end-->

Start the example by running the following at a command prompt:

```
python ses_replicate_identities.py
```


<!--custom.scenarios.ses_Scenario_ReplicateIdentities.start-->
<!--custom.scenarios.ses_Scenario_ReplicateIdentities.end-->

#### Create a web application to track DynamoDB data

This example shows you how to create a web application that tracks work items in an Amazon DynamoDB table and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.start-->
<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.end-->


<!--custom.scenarios.cross_DynamoDBDataTracker.start-->
<!--custom.scenarios.cross_DynamoDBDataTracker.end-->

#### Create an Aurora Serverless work item tracker

This example shows you how to create a web application that tracks work items in an Amazon Aurora Serverless database and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_RDSDataTracker.start-->
<!--custom.scenario_prereqs.cross_RDSDataTracker.end-->


<!--custom.scenarios.cross_RDSDataTracker.start-->
<!--custom.scenarios.cross_RDSDataTracker.end-->

#### Detect objects in images

This example shows you how to build an app that uses Amazon Rekognition to detect objects by category in images.


<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.end-->


<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.end-->

#### Generate credentials to connect to an SMTP endpoint

This example shows you how to generate credentials to connect to an Amazon SES SMTP endpoint.


<!--custom.scenario_prereqs.ses_Scenario_GenerateSmtpCredentials.start-->
<!--custom.scenario_prereqs.ses_Scenario_GenerateSmtpCredentials.end-->

Start the example by running the following at a command prompt:

```
python ses_generate_smtp_credentials.py
```


<!--custom.scenarios.ses_Scenario_GenerateSmtpCredentials.start-->
<!--custom.scenarios.ses_Scenario_GenerateSmtpCredentials.end-->

#### Verify an email identity and send messages

This example shows you how to do the following:

- Add and verify an email address with Amazon SES.
- Send a standard email message.
- Create a template and send a templated email message.
- Send a message by using an Amazon SES SMTP server.

<!--custom.scenario_prereqs.ses_Scenario_SendEmail.start-->
<!--custom.scenario_prereqs.ses_Scenario_SendEmail.end-->

Start the example by running the following at a command prompt:

```
python ses_email.py
```


<!--custom.scenarios.ses_Scenario_SendEmail.start-->
<!--custom.scenarios.ses_Scenario_SendEmail.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES API Reference](https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html)
- [SDK for Python Amazon SES reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ses.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0