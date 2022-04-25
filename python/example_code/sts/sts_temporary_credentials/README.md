# AWS STS temporary credential examples

## Purpose

Shows how to use the AWS Python SDK (Boto3) to access the AWS Security Token 
Service (AWS STS) to acquire temporary credentials that grant specific permissions. Also
demonstrates how to set up and use a multi-factor authentication (MFA) device. Learn
to accomplish the following tasks:

* Assume a role that grants specific permissions, and use those credentials to 
perform permitted actions.
* Add a new MFA device to a user. 
* Assume a role that requires MFA to be present.
* Construct a URL that gives federated users direct access to an account through the
AWS Management Console.
* Get a session token that can be used to call an API function that requires MFA.

*AWS STS is a web service that enables you to request temporary, limited-privilege 
credentials for AWS Identity and Access Management (IAM) users or for users you 
authenticate (federated users).*

## Code examples

### Scenario examples

* [Assume an IAM role that requires an MFA token](assume_role_mfa.py)
* [Construct a URL for federated users](federated_url.py)
* [Get a session token that requires an MFA token](session_token.py)

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
- Python 3.8.0 or later
- Boto3 1.13.2 or later
- PyTest 5.3.5 or later (to run unit tests)
- Requests 2.23.0 (to run federated_url.py)
- A multi-factor authentication device or application, such as LastPass Authenticator,
  Microsoft Authenticator, or Google Authenticator

### Command

This module contains several demonstrations of how to get temporary credentials:
* Assume a role with MFA.
* Get a session token to access APIs that require MFA.
* Construct a federated URL that lets you connect to another AWS account through 
the AWS Management Console with limited permissions.

You can run each script from a command window. For example, to see the *assume role MFA*
demo, run the following at a command prompt.

```
python assume_role_mfa.py
``` 

#### assume_role_mfa.py

Shows how to get temporary credentials by assuming a role that grants specific 
permissions, and how to use those credentials to perform permitted actions. In this
demo, the assumed role requires MFA to be present.

This example shows how to use AWS STS when you need more control than is available 
from the Boto3 credential provider. For many scenarios, you can add a profile to 
your credentials file so that Boto3 assumes a role on your behalf. 
For more information, see 
[Assume role provider](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/configuration.html#assume-role-provider) 
in the Boto3 *Credentials user guide*.

The demonstration has three parts: setup, usage, and teardown.

##### Setup

* Creates a virtual MFA device and registers it with an actual MFA device you own.
* Creates a user, a role, and a policy.
* The user has permission only to assume the role.
* For demo purposes, the user is created in the current account, but in practice
  the user could be from another account. 
* The role allows the user to assume it but requires MFA to be present.
* The policy is attached to the role and allows listing all buckets in the account.

##### Usage

* Tries to assume the role without supplying MFA credentials. This fails with an
  AccessDenied error.
* Assumes the role using MFA credentials, then uses the returned temporary credentials 
  to list the buckets for the account.

##### Teardown
* Removes all resources created during setup.
* After teardown, remember to remove the endpoint from your MFA device.

#### federated_url.py

Constructs a URL that gives federated users direct access to an account through the
AWS Management Console. This use of the console has limited permissions, defined by 
the associated role and policy. 

For more information, see 
[Enabling Custom Identity Broker Access to the AWS Console](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_providers_enable-console-custom-url.html)
in the *AWS Identity and Access Management User Guide*.

The demonstration has three parts: setup, usage, and teardown.

##### Setup

* Creates a role that can be assumed by the current user.
* Attaches a policy that allows only Amazon S3 read-only access.

##### Usage

* Acquires temporary credentials from AWS STS that can be used to assume a role 
  with limited permissions.
* Uses the temporary credentials to request a sign-in token from the
  AWS federation endpoint.
* Builds a URL that can be used in a browser to gain direct access to the AWS
  Management Console, with permissions limited to those defined in the assumed
  role.

##### Teardown
* Removes all resources created during setup.

#### session_token.py

Shows how to get a session token that can be used to call an API function that
requires MFA. This use case typically occurs when specific, sensitive APIs have 
additional MFA protection. For example, a user may be allowed to present ordinary
credentials to manipulate objects in an Amazon S3 bucket, but be required to 
present MFA credentials to delete the bucket.

The demonstration has three parts: setup, usage, and teardown.

##### Setup

* Creates a virtual MFA device and registers it with an actual MFA device you own.
* Creates a user with an inline policy that lets the user list buckets for the 
  account, but only when MFA is present.

##### Usage

* Gets a session token using MFA credentials, then uses the returned temporary 
  credentials to list the buckets for the account.

##### Teardown
* Removes all resources created during setup.
* After teardown, remember to remove the endpoint from your MFA device.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/sts/sts_temporary_credentials 
folder.

```commandline
python -m pytest
```

## Additional information

- [Boto3 AWS Security Token Service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sts.html)
- [Boto3 Assume role provider](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/configuration.html#assume-role-provider)
- [Boto3 Identity and Access Management (IAM) examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/iam-examples.html)
- [Boto3 Identity and Access Management (IAM) service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iam.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
