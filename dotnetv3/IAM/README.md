# AWS Identity and Access Management (IAM) code examples for .NET

## Overview

The examples in this section show how to use AWS Identity and Access Management (IAM) to manage IAM users, groups, roles, and policies.

## ⚠️ Important

- Running this code might result in charges to your AWS account. 
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
- This code is not tested in all AWS Regions. For more information, see 
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).


## Code examples

### Scenario

- [IAMUserExample](IAMUserExample/IAMUserExample/) - This application uses the
  basic features of the AWS Identity and Access Management (IAM) creating,
  managing, and controlling access to resources for users. The application was
  created using the AWS SDK for .NET version 3.7 and .NET Core 5. The application
  the following API commands:

  - CreateUsserAsync
  - CreateAccessKeyAsync
  - CreateRoleAsync
  - CreatePolicyAsync
  - AttachRolePolicyAsync
  - AssumeRoleAsync
  - DetatchRolePolicyAsync
  - DeleteRolePolicyAsync
  - DeletePolicyAsync
  - DeleteRoleAsync
  - DeleteAccessKeyAsync
  - DeleteUserAsync

- [IAM Basics](IAM_Basics_Scenario/) - This example uses AWS Identity and Access
  Management (IAM) to create a user, and then create a role that can use Amazon Simple
  Storage Service (Amazon S3) read-only permissions. This example was created using
  the AWS SDK for .NET version 3.7 and .NET Core 5. The application deletes all
  resources before exiting. This scenario uses the following API commands:

  - CreateUserAsync
  - CreateAccessKeyAsync
  - CreatePolicyAsync
  - CreateRoleAsync
  - ListBucketsAsync
  - AssumeRoleAsync
  - DeleteUserAsync
  - DeleteAccessKeyAsync
  - DeletePolicyAsync
  - DeleteRoleAsync
  - DeleteRolePolicyAsync

### Single action

- [AttachRolePolicyExample](AttachRolePolicyExample/AttachRolePolicyExample/) - Attaches a policy to an IAM role. (`AttachRolePolicyAsync`)
- [CreateAccessKeyExample](CreateAccessKeyExample/CreateAccessKeyExample/) - Creates a new IAM access key. (`CreateAccessKeyAsync`)
- [CreatePolicyExample](CreatePolicyExample/) - Creates a new IAM policy. (`CreatePolicyAsync`)
- [CreateRoleExample](CreateRoleExample/) - Creates a new IAM role. (`CreateRoleAsync`)
- [CreateUserExample](CreateUserExample/) - Creates a new IAM user. (`CreateUserAsync`)
- [DeleteAccessKeyExample](DeleteAccessKeyExample/) - Deletes an IAM access key. (`DeleteAccessKeyAsync`)
- [DeleteRolePolicyExample](DeleteRolePolicyExample/) - Deletes an IAM role policy. (`DeleteRolePolicyAsync`)
- [DeleteUserExample](DeleteUserExample/) - Deletes an IAM user. (`DeleteUserAsync`)
- [DetachRolePolicyExample](DetachRolePolicyExample/) - Detaches a policy from an IAM role. (`DetatchRolePolicyAsync`)
- [GetAccountPasswordPolicyExample](GetAccountPasswordPolicyExample/) - Gets the IAM account password policy.
- [GetPolicyExample](GetPolicyExample/) - Gets the details of an IAM policy. (`GetPolicyAsync`)
- [GetRoleExample](GetRoleExample/) - Gets the details of an IAM role. (`GetRoleAsync`)
- [ListAttachedRolePoliciesExample](ListAttachedRolePoliciesExample/) - Lists the IAM policies attached to a role. (`ListAttachedRolePoliciesAsync`)
- [ListGroupsExample](ListGroupsExample/) - Lists the IAM groups for an account. (`ListGroupsAsync`)
- [ListPoliciesExample](ListPoliciesExample/) - Lists the IAM policies for an account. (`ListPoliciesAsync`)
- [ListRolePoliciesExample](ListRolePoliciesExample/) - Lists the IAM role policies for an account. (`ListRolePoliciesAsync`)
- [ListRolesExample](ListRolesExample/) - Lists the IAM roles for an account. (`ListRolesAsync`)
- [ListSAMLProvidersExample](ListSAMLProvidersExample/) - Lists the SAML providers defined with an account. (`ListSAMLProvidersAsync`)
- [ListUsersExample](ListUsersExample/) - Lists the users for an account. (`ListUsersAsync`)


## Running the examples

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Resources and documentation

[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

