# Amazon S3 Glacier vaults and archives example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Storage Service
Glacier to create and manage vaults and archives.

*Amazon S3 Glacier is a storage service optimized for infrequently used data. The 
service provides durable and extremely low-cost storage with security features for 
data archiving and backup.*

## Code examples

### Usage examples

* [Archiving a file, getting notifications, and initiating a job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`upload_demo`)
* [Getting archive content from a job and deleting the archive](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`retrieve_demo`)

### API examples

* [Creating a vault](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py) 
(`create_vault`)
* [Deleting an archive](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`delete_archive`)
* [Deleting vault notifications](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`delete_vault_notifications`)
* [Describing a job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`describe_job`)
* [Getting job output](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`get_job_output`)
* [Getting vault notification configuration](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`get_vault_notification`)
* [Initiating an archive retrieval job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`initiate_job`)
* [Initiating an inventory retrieval job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`initiate_job`)
* [Listing jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`list_jobs`)
* [Listing vaults](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`list_vaults`)
* [Setting vault notifications](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`set_vault_notification`)
* [Uploading an archive](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/glacier/glacier_basics.py)
(`upload_archive`)

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
- Python 3.8 or later
- Boto3 1.17.96 or later
- Pytest 6.0.2 or later (to run unit tests)

### Command

Run code that creates an Amazon S3 Glacier vault, uploads an archive, optionally sets 
up notifications, and starts a job that retrieves an archive from the vault.

```
python glacier_basics.py --upload --notify <Amazon SNS topic ARN>
``` 

Run code that gets job output, deletes the archive, and deletes the vault.
Because Amazon S3 Glacier is designed for infrequent retrieval, a typical retrieval
job takes 3–5 hours to complete.

```
python glacier_basics.py --retrieve
```

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/glacier 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Glacier service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/glacier.html)
- [Amazon S3 Glacier documentation](https://docs.aws.amazon.com/glacier/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
