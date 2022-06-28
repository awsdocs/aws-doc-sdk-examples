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

### Single action

- [Attaching a role policy](AttachRolePolicyExample/AttachRolePolicyExample/)
- [Creating an access key](CreateAccessKeyExample/CreateAccessKeyExample/)
- [Creating a policy](CreatePolicyExample/)
- [Creating a role](CreateRoleExample/)
- [Creating a user](CreateUserExample/)
- [Deleting an access key](DeleteAccessKeyExample/)
- [Deleting a role policy](DeleteRolePolicyExample/)
- [Deleting a user](DeleteUserExample/)
- [Detaching a role policy](DetachRolePolicyExample/)
- [Getting an account password policy](GetAccountPasswordPolicyExample/)
- [Getting a policy](GetPolicyExample/)
- [Getting a role](GetRoleExample/)
- [Listing attached role policies](ListAttachedRolePoliciesExample/)
- [Listing groups](ListGroupsExample/)
- [Listing policies](ListPoliciesExample/)
- [Listing role policies](ListRolePoliciesExample/)
- [Listing roles](ListRolesExample/)
- [Listing SAML providers](ListSAMLProvidersExample/)
- [Listing users](ListUsersExample/)

### Scenarios

- [IAMUserExample](IAMUserExample/IAMUserExample/)
- [IAM Basics](IAM_Basics_Scenario/)

## Running the examples

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Resources and documentation

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

