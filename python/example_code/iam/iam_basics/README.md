# AWS IAM getting started examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage AWS Identity and Access 
Management (IAM) resources. Learn to accomplish the following tasks:

* Create and manage IAM user access keys.
* Manage the alias of an account.
* Acquire reports about account usage.
* Create and manage IAM policies, including versioned policies.
* Create and manage IAM roles, including how to attach and detach policies.
* Create and manage IAM users, including how to attach a policy to a user.

*IAM is a web service for securely controlling access to AWS services. With IAM, you 
can centrally manage users, security credentials such as access keys, and permissions 
that control which AWS resources users and applications can access.* 

## Code examples

### Scenario examples

* [Create read-only and read-write users](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
* [Manage access keys](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
* [Manage policies](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
* [Manage roles](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/role_wrapper.py)
* [Manage your account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
* [Rollback a policy version](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)

### API examples

* [Attach a policy to a role](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/role_wrapper.py)
(`AttachRolePolicy`)
* [Attach a policy to a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`AttachUserPolicy`)
* [Create a policy](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
(`CreatePolicy`)
* [Create a policy version](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
(`CreatePolicyVersion`)
* [Create a role](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/role_wrapper.py)
(`CreateRole`)
* [Create a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`CreateUser`)
* [Create an access key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
(`CreateAccessKey`)
* [Create an alias for an account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`CreateAccountAlias`)
* [Delete a policy](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
(`DeletePolicy`)
* [Delete a role](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/role_wrapper.py)
(`DeleteRole`)
* [Delete a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`DeleteUser`)
* [Delete an account alias](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`DeleteAccountAlias`)
* [Detach a policy from a role](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/role_wrapper.py)
(`DetachRolePolicy`)
* [Detach a policy from a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`DetachUserPolicy`)
* [Delete an access key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
(`DeleteAccessKey`)
* [Generate a credential report](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`GenerateCredentialReport`)
* [Get a credential report](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`GetCredentialReport`)
* [Get a detailed authorization report for your account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`GetAccountAuthorizationDetails`)
* [Get a policy version](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
(`GetPolicyVersion`)
* [Get a summary of account usage](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`GetAccountSummary`)
* [Get data about the last use of an access key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
(`GetAccessKeyLastUsed`)
* [List a user's access keys](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
(`ListAccessKeys`)
* [List account aliases](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/account_wrapper.py)
(`ListAccountAliases`)
* [List policies](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/policy_wrapper.py)
(`ListPolicies`)
* [List users](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`ListUsers`)
* [Update a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/user_wrapper.py)
(`UpdateUser`)
* [Update an access key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/iam/access_key_wrapper.py)
(`UpdateAccessKey`)

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

Each file can be run separately at a command prompt. For example, see the user
demonstration by running the following at a command prompt.

```
python user_wrapper.py
```  

### Example structure

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
Also includes a complete demonstration that creates users with different permissions.
It also shows how they can only perform the actions allowed by their attached policies. 

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/iam/iam_basics 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Identity and Access Management service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iam.html)
- [AWS Identity and Access Management Documentation](https://docs.aws.amazon.com/iam)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
