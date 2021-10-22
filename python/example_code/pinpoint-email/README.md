# Amazon Pinpoint Email examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Pinpoint Email to send 
email messages.

*Amazon Pinpoint helps you engage your customers by sending them email, SMS and voice 
messages, and push notifications.*

## Code examples

### API examples

* [Send an email](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/pinpoint/pinpoint_send_email_message_email_api.py)
(`SendEmail`)

## ⚠ Important

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

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

Each file can be run separately at a command prompt. For example, send an email message
by running the following at a command prompt.

```
python pinpoint_send_email_message_email_api.py
```  

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/pinpoint-email folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Pinpoint Email service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/pinpoint-email.html)
- [Amazon Pinpoint Documentation](https://docs.aws.amazon.com/pinpoint)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
