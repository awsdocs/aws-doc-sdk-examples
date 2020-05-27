# AWS Identity and Access Management (IAM) examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage IAM resources.
The examples are divided into the following files:

**access_key_wrapper.py**

Shows how to create and manage IAM user access keys, including how to determine
the time a key was last used and which service it was used for.

**account_wrapper.py**

Shows how to manage the alias of an account and how to acquire various reports about
account usage.

**policy_wrapper.py**

Shows how to create and manage IAM policies, including how to create policy versions,
set a default policy version, and roll back to a previous policy version.

**role_wrapper.py**

Shows how to create and manage IAM roles, including how to attach and detach policies.

**user_wrapper.py**

Shows how to create and manage IAM users, including how to attach a policy to a user.
Also includes a complete demonstration that creates users with different permissions
and shows how they can only perform the actions allowed by their attached policies. 

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

## Running the code

Each file can be run separately at a command prompt. For example, see the user
demonstration by running the following from a command prompt window.

```
python user_wrapper.py
```  

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/iam/iam_basics 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Identity and Access Management examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/iam-examples.html)
- [Boto3 AWS Identity and Access Management service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iam.html)
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  "AWS Regional Table" on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
