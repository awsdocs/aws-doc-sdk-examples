# AWS Key Management Service examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to create and manage AWS Key Management
Service (AWS KMS) keys, grants, and policies.

*AWS KMS is an encryption and key management service scaled for the cloud.*

## Code examples

### Scenario examples

* [Encrypt and decrypt text](key_encryption.py)
* [Manage aliases](alias_management.py)
* [Manage grants](grant_management.py)
* [Manage key policies](key_policies.py)
* [Manage keys](key_management.py)

### API examples

* [Create a key](key_management.py)
(`CreateKey`)
* [Create a grant for a key](grant_management.py)
(`CreateGrant`)
* [Create an alias for a key](alias_management.py)
(`CreateAlias`)
* [Decrypt ciphertext](key_encryption.py)
(`Decrypt`)
* [Delete an alias](alias_management.py)
(`DeleteAlias`)
* [Describe a key](key_management.py)
(`DescribeKey`)
* [Disable a key](key_management.py)
(`DisableKey`)
* [Enable a key](key_management.py)
(`EnableKey`)
* [Encrypt text using a key](key_encryption.py)
(`Encrypt`)
* [Generate a plaintext data key for client-side encryption](key_management.py)
(`GenerateDataKey`)
* [Get a policy for a key](key_policies.py)
(`GetKeyPolicy`)
* [List aliases for a key](alias_management.py)
(`ListAliases`)
* [List grants for a key](grant_management.py)
(`ListGrants`)
* [List policies for a key](key_policies.py)
(`ListKeyPolicies`)
* [List keys](key_management.py)
(`ListKeys`)
* [Recencrypt ciphertext from one key to another](key_encryption.py)
(`ReEncrypt`)
* [Retire a grant for a key](grant_management.py)
(`RetireGrant`)
* [Revoke a grant for a key](grant_management.py)
(`RevokeGrant`)
* [Schedule deletion of a key](key_management.py)
(`ScheduleKeyDeletion`)
* [Set the policy for a key](key_policies.py)
(`PutKeyPolicy`)
* [Update the key referred to by an alias](alias_management.py)
(`UpdateAlias`)

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
- Boto3 1.14.47 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

Run the examples at a command prompt with commands like the following.

```
python key_management.py
``` 

The examples are interactive scripts that ask you to input data and answer questions. 
Some of them require an existing KMS key. You can create a key using the AWS Management
Console or by running the `key_management.py` script and choosing to keep the key that 
is created for the demo.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/kms 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS KMS service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kms.html)
- [AWS Key Management Service documentation](https://docs.aws.amazon.com/kms)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
