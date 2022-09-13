# IAM code examples for the SDK for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with AWS Identity and Access Management (IAM) to manage IAM users, groups, roles, and policies.

IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage users, security credentials such as access keys, and permissions that control which AWS resources users and applications can access.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](AttachRolePolicyExample/AttachRolePolicyExample/AttachRolePolicy.cs) (`AttachRolePolicyAsync`)
- [Create an access key](CreateAccessKeyExample/CreateAccessKeyExample/CreateAccessKey.cs) (`CreateAccessKeyAsync`)
- [Create a policy](CreatePolicyExample/CreatePolicy.cs) (`CreatePolicyAsync`)
- [Create a role](CreateRoleExample/CreateRole.cs) (`CreateRoleAsync`)
- [Create a user](CreateUserExample/CreateUserExample/CreateUser.cs) (`CreateUserAsync`)
- [Delete an access key](DeleteAccessKeyExample/DeleteAccessKeyExample/DeleteAccessKey.cs) (`DeleteAccessKeyAsync`)
- [Delete a role policy](DeleteRolePolicyExample/DeleteRolePolicyExample/DeleteRolePolicy.cs) (`DeleteRolePolicyAsync`)
- [Delete a user](DeleteUserExample/DeleteUserExample/DeleteUser.cs) (`DeleteUserAsync`)
- [Detach a policy from a role](DetachRolePolicyExample/DetachRolePolicyExample/DetachRolePolicy.cs) (`DetatchRolePolicyAsync`)
- [Get the account password policy](GetAccountPasswordPolicyExample/GetAccountPasswordPolicy.cs) (`GetAccountPasswordPolicyAsync`)
- [Get a policy](GetPolicyExample/GetPolicy.cs) (`GetPolicyAsync`)
- [Get a role](GetRoleExample/GetRole.cs) (`GetRoleAsync`)
- [List policies attached to a role](ListAttachedRolePoliciesExample/ListAttachedRolePolicies.cs) (`ListAttachedRolePoliciesAsync`)
- [List groups](ListGroupsExample/ListGroups.cs) (`ListGroupsAsync`)
- [List policies](ListPoliciesExample/ListPolicies.cs) (`ListPoliciesAsync`)
- [List inline policies for a role](ListRolePoliciesExample/Program.cs) (`ListRolePoliciesAsync`)
- [List roles](ListRolesExample/ListRoles.cs) (`ListRolesAsync`)
- [List SAML providers](ListSAMLProvidersExample/ListSAMLProviders.cs) (`ListSAMLProvidersAsync`)
- [List users](ListUsersExample/ListUsers.cs) (`ListUsersAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Create a user and assume a role](IAM_Basics_Scenario/IAM_Basics_Scenario/IAM_Basics.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then
issue the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [AWS Identity and Access Management User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
* [AWS Identity and Access Management API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
* [AWS SDK for .NET IAM](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/IAM/NIAM.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

