# IAM code examples for AWS SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to manage Amazon IAM resources.

*IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage users,
security credentials such as access keys, and permissions that control which AWS resources users and applications can
access.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform
  the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  .
* This code is not tested in every AWS Region. For more information,
  see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Listing Roles](../IamService.php) (ListRoles)
* [Listing Users](../IamService.php) (ListUsers)
* [Listing Groups](../IamService.php) (ListGroups)
* [Attaching Role Policies](../IamService.php) (AttachRolePolicy)
* [Getting an Account Password Policy](../IamService.php) (GetAccountPasswordPolicy)
* [Listing SAML Providers](../IamService.php) (ListSAMLProviders)
* [Listing Role Policies](../IamService.php) (ListRolePolicies)
* [Getting a Role](../IamService.php) (GetRole)
* [Listing Policies](../IamService.php) (ListPolicies)
* [Creating a Service Linked Role](../IamService.php) (CreateServiceLinkedRole)
* [Creating a Role](../IamService.php) (CreateRole)
* [Listing Attached Role Policies](../IamService.php) (ListAttachedRolePolicies)
* [Getting a Policy](../IamService.php) (GetPolicy)

### Scenario

* [Getting started with IAM](GettingStartedWithIAM.php)

## Running the code

Run the scenario with the following command:

```
php GettingStartedWithIAM.php
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured as described in
  the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html)
  .
- PHP 7.1 or later
- Composer installed

## Tests

⚠️ Running the tests might result in charges to your AWS account.

Run the tests with the following command:

```
vendor/bin/phpunit tests/IAMBasicsTests.php
```

## Additional resources

* [AWS Identity and Access Management Documentation](https://docs.aws.amazon.com/iam)
* [AWS Identity and Access Management API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
* [AWS SDK for PHP API Reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
