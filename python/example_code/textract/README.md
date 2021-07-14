# Amazon Textract basics example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Textract to detect text, 
form, and table elements in a document image. 

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.8 or later
- Boto3 1.16.49 or later
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

This example shows how to implement basic Amazon Textract operations. For
an interactive Tkinter application that displays extracted data in a visual form, see 
[python/cross_service/textract_explorer](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/cross_service/textract_explorer). 

### Example structure

The example contains the following file.

**textract_wrapper.py**

Wraps Textract, Amazon S3, Amazon SNS, and Amazon SQS functions.

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your 
[GitHub root]/python/example_code/textract folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Textract reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/textract.html)
- [Amazon Textract Documentation](https://docs.aws.amazon.com/textract/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
