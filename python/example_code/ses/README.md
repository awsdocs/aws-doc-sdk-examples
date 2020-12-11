# Amazon SES email and identity example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Email Service 
(Amazon SES) to verify identities, send emails, and manage rules and templates.

* Verify email address and domain identities.
* Create and manage email templates that contain replaceable tags.
* Send email by using the Amazon SES API or an Amazon SES SMTP server.
* Create and manage rules to block, allow, or handle incoming emails. 
* Copy email and domain identity configuration from one AWS Region to another.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto3 1.15.4 or later
- PyTest 6.0.2 or later (to run unit tests)

## Cautions

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

There are six demonstrations in this set of examples.

**Send email**

This example shows how to send email with the Amazon SES API and through an 
Amazon SES SMTP server. Run it at a command prompt with the following command.

```
python ses_email.py
``` 

**Generate SMTP credentials**

This example shows how to generate SMTP credentials from AWS credentials. Run it 
at a command prompt with the following command.

```
python ses_generate_smtp_credentials.py SECRET REGION
``` 

**Verify identities**

This example shows how to verify and manage email and domain identities. Run it 
at a command prompt with the following command.

```
python ses_identities.py
``` 

**Handle incoming email**

This example shows how to create and manage filters and rules to block, allow,
and handle incoming email. Run it at a command prompt with the following command.

```
python ses_receipt_handler.py
``` 

**Replicate identities**

This example shows how to copy email and domain identity configuration from one
AWS Region to another. Run it at a command prompt with the following command.

```
python ses_replicate_identities.py SOURCE_REGION DESTINATION_REGION
``` 

**Email templates**

This example shows how to create and manage email templates that contain replaceable
tags. Run it at a command prompt with the following command.

```
python ses_templates.py
``` 

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/ses 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Simple Email Service (Amazon SES) service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ses.html)
- [Boto3 Amazon Route 53 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/route53.html)
- [Amazon SES documentation](https://docs.aws.amazon.com/ses/)
- [Amazon Route 53 documentation](https://docs.aws.amazon.com/route53/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
