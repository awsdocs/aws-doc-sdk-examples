# Amazon S3 managed file transfer example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) transfer manager to manage multipart
uploads to and downloads from an Amazon Simple Storage Service (Amazon S3) bucket.

When the file to transfer is larger than the specified threshold, the transfer
manager automatically uses multipart uploads or downloads. This example
shows how to use several of the available transfer manager settings, and reports
thread usage and time to transfer.

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any 
amount of data at any time, from anywhere on the web.*

## Code examples

### Scenario examples

* [Use a transfer manager to upload and download files](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/s3/file_transfer/demo_file_transfer.py)

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

Interactively demonstrates the code in file_transfer.py by running the following
in a command window in the file_transfer folder.

```
python -m demo_file_transfer
```

The demonstration script asks questions, takes actions to upload and download
files with various configurations, and manages artifact creation and cleanup.

Amazon S3 objects and downloaded files created during the demonstration are cleaned 
up at the end.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/s3/file_transfer 
folder.

```
python -m pytest
```

## Additional information

- [Boto3 Amazon S3 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)
- [Amazon S3 documentation](https://docs.aws.amazon.com/s3)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
