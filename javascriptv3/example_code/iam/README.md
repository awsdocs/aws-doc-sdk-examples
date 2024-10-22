# IAM code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello IAM](hello.js#L6) (`ListPolicies`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/basic.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](actions/attach-role-policy.js#L6)
- [CreateAccessKey](actions/create-access-key.js#L6)
- [CreateAccountAlias](actions/create-account-alias.js#L6)
- [CreateGroup](actions/create-group.js#L6)
- [CreateInstanceProfile](../cross-services/wkflw-resilient-service/steps-demo.js#L450)
- [CreatePolicy](actions/create-policy.js#L6)
- [CreateRole](actions/create-role.js#L6)
- [CreateSAMLProvider](actions/create-saml-provider.js#L6)
- [CreateServiceLinkedRole](actions/create-service-linked-role.js#L6)
- [CreateUser](actions/create-user.js#L6)
- [DeleteAccessKey](actions/delete-access-key.js#L6)
- [DeleteAccountAlias](actions/delete-account-alias.js#L6)
- [DeleteGroup](actions/delete-group.js#L6)
- [DeleteInstanceProfile](../cross-services/wkflw-resilient-service/steps-destroy.js#L210)
- [DeletePolicy](actions/delete-policy.js#L6)
- [DeleteRole](actions/delete-role.js#L6)
- [DeleteRolePolicy](actions/delete-role-policy.js#L6)
- [DeleteSAMLProvider](actions/delete-saml-provider.js#L6)
- [DeleteServerCertificate](actions/delete-server-certificate.js#L6)
- [DeleteServiceLinkedRole](actions/delete-service-linked-role.js#L6)
- [DeleteUser](actions/delete-user.js#L6)
- [DetachRolePolicy](actions/detach-role-policy.js#L6)
- [GetAccessKeyLastUsed](actions/get-access-key-last-used.js#L6)
- [GetAccountPasswordPolicy](actions/get-account-password-policy.js#L6)
- [GetPolicy](actions/get-policy.js#L6)
- [GetRole](actions/get-role.js#L6)
- [GetServerCertificate](actions/get-server-certificate.js#L6)
- [GetServiceLinkedRoleDeletionStatus](actions/get-service-linked-role-deletion-status.js#L6)
- [ListAccessKeys](actions/list-access-keys.js#L6)
- [ListAccountAliases](actions/list-account-aliases.js#L6)
- [ListAttachedRolePolicies](actions/list-attached-role-policies.js#L6)
- [ListGroups](actions/list-groups.js#L6)
- [ListPolicies](actions/list-policies.js#L6)
- [ListRolePolicies](actions/list-role-policies.js#L6)
- [ListRoles](actions/list-roles.js#L6)
- [ListSAMLProviders](actions/list-saml-providers.js#L6)
- [ListServerCertificates](actions/list-server-certificates.js#L6)
- [ListUsers](actions/list-users.js#L6)
- [PutRolePolicy](actions/put-role-policy.js#L6)
- [UpdateAccessKey](actions/update-access-key.js#L6)
- [UpdateServerCertificate](actions/update-server-certificate.js#L6)
- [UpdateUser](actions/update-user.js#L6)
- [UploadServerCertificate](actions/upload-server-certificate.js#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../cross-services/wkflw-resilient-service/index.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello IAM

This example shows you how to get started using IAM.

```bash
node ./hello.js
```

#### Learn the basics

This example shows you how to create a user and assume a role. 

- Create a user with no permissions.
- Create a role that grants permission to list Amazon S3 buckets for the account.
- Add a policy to let the user assume the role.
- Assume the role and list S3 buckets using temporary credentials, then clean up resources.

<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.start-->
<!--custom.basic_prereqs.iam_Scenario_CreateUserAssumeRole.end-->


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


<!--custom.scenarios.cross_ResilientService.start-->
<!--custom.scenarios.cross_ResilientService.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for JavaScript (v3) IAM reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/iam)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0