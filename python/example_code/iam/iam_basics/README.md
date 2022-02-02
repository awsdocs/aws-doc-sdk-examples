# AWS IAM getting started examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage AWS Identity and Access 
Management (IAM) resources. Learn to accomplish the following tasks:

* Create and manage IAM user access keys.
* Manage the alias of an account.
* Acquire reports about account usage.
* Create a user and assume a role with new permissions.
* Create and manage IAM policies, including versioned policies.
* Create and manage IAM roles, including how to attach and detach policies.
* Create and manage IAM users, including how to attach a policy to a user.

*IAM is a web service for securely controlling access to AWS services. With IAM, you 
can centrally manage users, security credentials such as access keys, and permissions 
that control which AWS resources users and applications can access.* 

## Code examples

### Scenario examples

* [Create a user and assume a role](scenario_create_user_assume_role.py)
* [Create read-only and write-only users](user_wrapper.py)
* [Manage access keys](access_key_wrapper.py)
* [Manage policies](policy_wrapper.py)
* [Manage roles](role_wrapper.py)
* [Manage your account](account_wrapper.py)
* [Rollback a policy version](policy_wrapper.py)

### API examples

* [Attach a policy to a role](role_wrapper.py)
(`AttachRolePolicy`)
* [Attach a policy to a user](user_wrapper.py)
(`AttachUserPolicy`)
* [Create a policy](policy_wrapper.py)
(`CreatePolicy`)
* [Create a policy version](policy_wrapper.py)
(`CreatePolicyVersion`)
* [Create a role](role_wrapper.py)
(`CreateRole`)
* [Create a service-linked role](service_linked_roles.py)
(`CreateServiceLinkedRole`)
* [Create a user](user_wrapper.py)
(`CreateUser`)
* [Create an access key](access_key_wrapper.py)
(`CreateAccessKey`)
* [Create an alias for an account](account_wrapper.py)
(`CreateAccountAlias`)
* [Delete a policy](policy_wrapper.py)
(`DeletePolicy`)
* [Delete a role](role_wrapper.py)
(`DeleteRole`)
* [Delete a user](user_wrapper.py)
(`DeleteUser`)
* [Delete an account alias](account_wrapper.py)
(`DeleteAccountAlias`)
* [Detach a policy from a role](role_wrapper.py)
(`DetachRolePolicy`)
* [Detach a policy from a user](user_wrapper.py)
(`DetachUserPolicy`)
* [Delete an access key](access_key_wrapper.py)
(`DeleteAccessKey`)
* [Generate a credential report](account_wrapper.py)
(`GenerateCredentialReport`)
* [Get a credential report](account_wrapper.py)
(`GetCredentialReport`)
* [Get a detailed authorization report for your account](account_wrapper.py)
(`GetAccountAuthorizationDetails`)
* [Get a policy](policy_wrapper.py)
(`GetPolicy`)
* [Get a policy version](policy_wrapper.py)
(`GetPolicyVersion`)
* [Get a role](role_wrapper.py)
(`GetRole`)
* [Get a summary of account usage](account_wrapper.py)
(`GetAccountSummary`)
* [Get data about the last use of an access key](access_key_wrapper.py)
(`GetAccessKeyLastUsed`)
* [Get the account passwordy policy](account_wrapper.py)
(`GetAccountPasswordPolicy`)
* [List a user's access keys](access_key_wrapper.py)
(`ListAccessKeys`)
* [List account aliases](account_wrapper.py)
(`ListAccountAliases`)
* [List groups](group_wrapper.py)
(`ListGroups`)
* [List inline policies for a role](role_wrapper.py)
(`ListRolePolicies`)
* [List policies](policy_wrapper.py)
(`ListPolicies`)
* [List policies attached to a role](role_wrapper.py)
(`ListAttachedRolePolicies`)
* [List roles](role_wrapper.py)
(`ListRoles`)
* [List SAML providers](account_wrapper.py)
(`ListSAMLProviders`)
* [List users](user_wrapper.py)
(`ListUsers`)
* [Update a user](user_wrapper.py)
(`UpdateUser`)
* [Update an access key](access_key_wrapper.py)
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

**scenario_create_user_assume_role.py**

Shows how to create an IAM user, assume a role, and perform AWS actions.

1. Create a user who has no permissions.
2. Create a role that grants permission to list Amazon S3 buckets for the account.
3. Add a policy to let the user assume the role.
4. Assume the role and list Amazon S3 buckets using temporary credentials.
5. Delete the policy, role, and user.

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
