# IAM code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](hello/hello_iam.py#L4) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_create_user_assume_role.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](policy_wrapper.py#L221)
- [AttachUserPolicy](user_wrapper.py#L107)
- [CreateAccessKey](access_key_wrapper.py#L21)
- [CreateAccountAlias](account_wrapper.py#L23)
- [CreateInstanceProfile](../../cross_service/resilient_service/auto_scaler.py#L151)
- [CreatePolicy](policy_wrapper.py#L25)
- [CreatePolicyVersion](policy_wrapper.py#L79)
- [CreateRole](role_wrapper.py#L23)
- [CreateServiceLinkedRole](service_linked_roles.py#L23)
- [CreateUser](user_wrapper.py#L25)
- [DeleteAccessKey](access_key_wrapper.py#L47)
- [DeleteAccountAlias](account_wrapper.py#L44)
- [DeleteInstanceProfile](../../cross_service/resilient_service/auto_scaler.py#L304)
- [DeletePolicy](policy_wrapper.py#L61)
- [DeleteRole](role_wrapper.py#L102)
- [DeleteUser](user_wrapper.py#L46)
- [DetachRolePolicy](policy_wrapper.py#L240)
- [DetachUserPolicy](user_wrapper.py#L126)
- [GenerateCredentialReport](account_wrapper.py#L131)
- [GetAccessKeyLastUsed](access_key_wrapper.py#L68)
- [GetAccountAuthorizationDetails](account_wrapper.py#L86)
- [GetAccountPasswordPolicy](account_wrapper.py#L175)
- [GetAccountSummary](account_wrapper.py#L111)
- [GetCredentialReport](account_wrapper.py#L155)
- [GetPolicy](policy_wrapper.py#L139)
- [GetPolicyVersion](policy_wrapper.py#L140)
- [GetRole](role_wrapper.py#L59)
- [ListAccessKeys](access_key_wrapper.py#L97)
- [ListAccountAliases](account_wrapper.py#L62)
- [ListAttachedRolePolicies](role_wrapper.py#L158)
- [ListGroups](group_wrapper.py#L21)
- [ListPolicies](policy_wrapper.py#L117)
- [ListRolePolicies](role_wrapper.py#L139)
- [ListRoles](role_wrapper.py#L81)
- [ListSAMLProviders](account_wrapper.py#L213)
- [ListUsers](user_wrapper.py#L65)
- [UpdateAccessKey](access_key_wrapper.py#L118)
- [UpdateUser](user_wrapper.py#L85)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../../cross_service/resilient_service/runner.py)
- [Create read-only and read-write users](user_wrapper.py)
- [Manage access keys](access_key_wrapper.py)
- [Manage policies](policy_wrapper.py)
- [Manage roles](role_wrapper.py)
- [Manage your account](account_wrapper.py)
- [Roll back a policy version](policy_wrapper.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.

```
python hello/hello_iam.py
```

#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->

Start the example by running the following at a command prompt:

```
python scenario_create_user_assume_role.py
```


<!--custom.basics.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basics.iam_Scenario_CreateUserAssumeRole.end-->


#### Build and manage a resilient service

This example shows you how to create a load-balanced web service that returns book, movie, and song recommendations. The example shows how the service responds to failures, and how to restructure the service for more resilience when failures occur.

- Use an Amazon EC2 Auto Scaling group to create Amazon Elastic Compute Cloud (Amazon EC2) instances based on a launch template and to keep the number of instances in a specified range.
- Handle and distribute HTTP requests with Elastic Load Balancing.
- Monitor the health of instances in an Auto Scaling group and forward requests only to healthy instances.
- Run a Python web server on each EC2 instance to handle HTTP requests. The web server responds with recommendations and health checks.
- Simulate a recommendation service with an Amazon DynamoDB table.
- Control web server response to requests and health checks by updating AWS Systems Manager parameters.

<!--custom.scenario_prereqs.cross_ResilientService.start-->
<!--custom.scenario_prereqs.cross_ResilientService.end-->

Start the example by running the following at a command prompt:

```
python ../../cross_service/resilient_service/runner.py
```


<!--custom.scenarios.cross_ResilientService.start-->
Complete details and instructions on how to run this example can be found in the
[README](../../cross_service/resilient_service/README.md) for the example.
<!--custom.scenarios.cross_ResilientService.end-->

#### Create read-only and read-write users

This example shows you how to create users and attach policies to them. 

- Create two IAM users.
- Attach a policy for one user to get and put objects in an Amazon S3 bucket.
- Attach a policy for the second user to get objects from the bucket.
- Get different permissions to the bucket based on user credentials.

<!--custom.scenario_prereqs.iam_Scenario_UserPolicies.start-->
<!--custom.scenario_prereqs.iam_Scenario_UserPolicies.end-->

Start the example by running the following at a command prompt:

```
python user_wrapper.py
```


<!--custom.scenarios.iam_Scenario_UserPolicies.start-->
<!--custom.scenarios.iam_Scenario_UserPolicies.end-->

#### Manage access keys

This example shows you how to manage access keys. 

- Create and list access keys.
- Find out when and how an access key was last used.
- Update and delete access keys.

<!--custom.scenario_prereqs.iam_Scenario_ManageAccessKeys.start-->
<!--custom.scenario_prereqs.iam_Scenario_ManageAccessKeys.end-->

Start the example by running the following at a command prompt:

```
python access_key_wrapper.py
```


<!--custom.scenarios.iam_Scenario_ManageAccessKeys.start-->
<!--custom.scenarios.iam_Scenario_ManageAccessKeys.end-->

#### Manage policies

This example shows you how to do the following:

- Create and list policies.
- Create and get policy versions.
- Roll back a policy to a previous version.
- Delete policies.

<!--custom.scenario_prereqs.iam_Scenario_PolicyManagement.start-->
<!--custom.scenario_prereqs.iam_Scenario_PolicyManagement.end-->

Start the example by running the following at a command prompt:

```
python policy_wrapper.py
```


<!--custom.scenarios.iam_Scenario_PolicyManagement.start-->
<!--custom.scenarios.iam_Scenario_PolicyManagement.end-->

#### Manage roles

This example shows you how to do the following:

- Create an IAM role.
- Attach and detach policies for a role.
- Delete a role.

<!--custom.scenario_prereqs.iam_Scenario_RoleManagement.start-->
<!--custom.scenario_prereqs.iam_Scenario_RoleManagement.end-->

Start the example by running the following at a command prompt:

```
python role_wrapper.py
```


<!--custom.scenarios.iam_Scenario_RoleManagement.start-->
<!--custom.scenarios.iam_Scenario_RoleManagement.end-->

#### Manage your account

This example shows you how to do the following:

- Get and update the account alias.
- Generate a report of users and credentials.
- Get a summary of account usage.
- Get details for all users, groups, roles, and policies in your account, including their relationships to each other.

<!--custom.scenario_prereqs.iam_Scenario_AccountManagement.start-->
<!--custom.scenario_prereqs.iam_Scenario_AccountManagement.end-->

Start the example by running the following at a command prompt:

```
python account_wrapper.py
```


<!--custom.scenarios.iam_Scenario_AccountManagement.start-->
<!--custom.scenarios.iam_Scenario_AccountManagement.end-->

#### Roll back a policy version

This example shows you how to do the following:

- Get the list of policy versions in order by date.
- Find the default policy version.
- Make the previous policy version the default.
- Delete the old default version.

<!--custom.scenario_prereqs.iam_Scenario_RollbackPolicyVersion.start-->
<!--custom.scenario_prereqs.iam_Scenario_RollbackPolicyVersion.end-->

Start the example by running the following at a command prompt:

```
python policy_wrapper.py
```


<!--custom.scenarios.iam_Scenario_RollbackPolicyVersion.start-->
<!--custom.scenarios.iam_Scenario_RollbackPolicyVersion.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Python IAM reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iam.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0