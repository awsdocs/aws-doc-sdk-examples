# AWS Organizations policy examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to create and manage AWS Organizations
policies.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto3 1.14.47 or later
- PyTest 5.3.5 or later (to run unit tests)

## Cautions

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Regional Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.


## Running the code

Run this example at a command prompt with the following command:

```
python organizations_policies.py [--target TARGET]
``` 

This example optionally attaches and detaches the demo policy to an AWS Organizations
resource, such as a root organization or account. Replace `TARGET` in the command with
the ID of the resource if you want to include this in the demo. 

### Example structure

The example contains one file.

**organizations_policies.py**

Shows how to create and manage AWS Organizations policies.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/organizations 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Organizations service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/organizations.html)
- [AWS Organizations documentation](https://docs.aws.amazon.com/organizations/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
