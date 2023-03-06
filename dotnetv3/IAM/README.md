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

### Getting started
- [Hello IAM](Actions/HelloIAM.cs)

### Single actions
Code excerpts that show you how to call individual service functions.

- [Add a policy to a group](Actions/IAMWrapper.cs) (`PutGroupPolicyAsync`)
- [Add a user to a group](Actions/IAMWrapper.cs) (`AddUserToGroupAsync`)
- [Attach a policy to a role](Actions/IAMWrapper.cs) (`AttachRolePolicyAsync`)
- [Create a group](Actions/IAMWrapper.cs) (`CreateGroupAsync`)
- [Create a policy](Actions/IAMWrapper.cs) (`CreatePolicyAsync`)
- [Create a role](Actions/IAMWrapper.cs) (`CreateRoleAsync`)
- [Create a service-linked role](Actions/IAMWrapper.cs) (`CreateServiceLinkedRoleAsync`)
- [Create a user](Actions/IAMWrapper.cs) (`CreateUserAsync`)
- [Create an access key](Actions/IAMWrapper.cs) (`CreateAccessKeyAsync`)
- [Delete a group](Actions/IAMWrapper.cs) (`DeleteGroupAsync`)
- [Delete a group policy](Actions/IAMWrapper.cs) (`DeleteGroupPolicyAsync`)
- [Delete a policy](Actions/IAMWrapper.cs) (`DeletePolicyAsync`)
- [Delete a role](Actions/IAMWrapper.cs) (`DelteRoleAsync`)
- [Delete a role policy](Actions/IAMWrapper.cs) (`DeleteRolePolicyAsync`)
- [Delete a user](Actions/IAMWrapper.cs) (`DeleteUserAsync`)
- [Delete a user policy](Actions/IAMWrapper.cs) (`DeleteRolePolicyAsync`)
- [Delete an access key](Actions/IAMWrapper.cs) (`DeleteAccessKeyAsync`)
- [Detach a policy from a role](Actions/IAMWrapper.cs) (`DetatchRolePolicyAsync`)
- [Get a policy](Actions/IAMWrapper.cs) (`GetPolicyAsync`)
- [Get a role](Actions/IAMWrapper.cs) (`GetRoleAsync`)
- [Get a user](Actions/IAMWrapper.cs) (`GetUserAsync`)
- [Get the account password policy](Actions/IAMWrapper.cs) (`GetAccountPasswordPolicyAsync`)
- [List groups](Actions/IAMWrapper.cs) (`ListGroupsAsync`)
- [List inline policies for a role](Actions/IAMWrapper.cs) (`ListRolePoliciesAsync`)
- [List policies](Actions/IAMWrapper.cs) (`ListPoliciesAsync`)
- [List policies attached to a role](Actions/IAMWrapper.cs) (`ListAttachedRolePoliciesAsync`)
- [List roles](Actions/IAMWrapper.cs) (`ListRolesAsync`)
- [List SAML providers](Actions/IAMWrapper.cs) (`ListSAMLProvidersAsync`)
- [List users](Actions/IAMWrapper.cs) (`ListUsersAsync`)
- [Remove a user from a group](Actions/IAMWrapper.cs) (`RemoveUserFromGroupAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Create a user and assume a role](Scenarios/IAMBasics/IAMBasics.cs)

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

