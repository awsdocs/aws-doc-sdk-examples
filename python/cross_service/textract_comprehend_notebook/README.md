# Detect entities in extracted text using a Jupyter notebook

## Purpose

Shows how to use the AWS SDK for Python (Boto3) in a Jupyter notebook to detect entities 
in text that is extracted from an image. This example uses Amazon Textract to
extract text from an image stored in Amazon Simple Storage Service (Amazon S3) and 
Amazon Comprehend to detect entities in the extracted text.

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
- Python 3.5 or later
- Boto3 1.17.96 or later
- Pillow 8.1.1 or later
- Pandas 1.3.2 or later

### Creating the resources

For this example, you need the following resources:

* An Amazon S3 bucket.
* An image in the Amazon S3 bucket containing text.

Create these resources by using the AWS Management Console or the AWS SDK for Python.

### Running the code

This example is a Jupyter notebook and must be run in an environment that can host
notebooks. For instructions on how to run the example using Amazon SageMaker, see
the directions in [TextractAndComprehendNotebook.ipynb](TextractAndComprehendNotebook.ipynb).

### Deleting the resources

After running the example, remember to delete any resources you are no longer using, 
including your Amazon S3 bucket.

## Running the tests

To run the unit tests:

1. Install the [Testbook](https://testbook.readthedocs.io/en/latest/) library. 
1. Replace the values of the indicated variables, specified in the 
`test-textract-comprehend.py` Python file, with the values that you want to use.
1. Run the test file at a command prompt: `python test-textract-comprehend.py`.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0