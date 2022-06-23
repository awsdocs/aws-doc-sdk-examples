# AWS Config rules example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Config to
create and manage config rules.

*AWS Config provides a detailed view of the resources associated with your AWS account.*

## Code examples

* [Creating an AWS Config rule](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/config/config_rules.py) 
(`put_config_rule`)
* [Deleting an AWS Config rule](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/config/config_rules.py) 
(`delete_config_rule`)
* [Describing an AWS Config rule](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/config/config_rules.py) 
(`describe_config_rules`)

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
- Boto3 1.14.47 or later

### Command

Run a demonstration that creates, describes, and deletes an AWS Config rule at
a command prompt with the following command.

```
python config_rules.py
``` 

### Example structure

The example contains the following file.

**config_rules.py**

Shows how to use AWS Config APIs. The `usage_demo` script creates a rule that prohibits
making Amazon Simple Storage Service (Amazon S3) buckets publicly readable, gets data 
about the rule, and deletes it.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/config 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Config service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/config.html)
- [AWS Config documentation](https://docs.aws.amazon.com/config)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
