#  Organizations code examples for the SDK for .NET

## Overview

The examples in this section show how to use the AWS SDK for .NET with AWS Organizations to create and manage organizations policies.

With Organizations, you can consolidate multiple AWS accounts into an organization that you create and centrally manage. You can create member accounts and invite existing accounts to join your organization. You can organize those accounts and manage them as a group.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Attach a policy to a target](AttachPolicyExample/AttachPolicyExample/AttachPolicy.cs) (`AttachPolicyAsync`)
- [Create an account](CreateAccountExample/CreateAccountExample/CreateAccount.cs) (`CreateAccountAsync`)
- [Create an organization](CreateOrganizationExample/CreateOrganizationExample/CreateOrganization.cs) (`CreateOrganizationAsync`)
- [Create an organizational unit](CreateOrganizationalUnitExample/CreateOrganizationalUnitExample/CreateOrganizationalUnit.cs) (`CreateOrganizationalUnitAsync`)
- [Create a policy](CreatePolicyExample/CreatePolicyExample/CreatePolicy.cs) (`CreatePolicyAsync`)
- [Delete an organization](DeleteOrganizationExample/DeleteOrganizationExample/DeleteOrganization.cs) (`DeleteOrganizationAsync`)
- [Delete an organizational unit](DeleteOrganizationalUnitExample/DeleteOrganizationalUnitExample/DeleteOrganizationalUnit.cs) (`DeleteOrganizationalUnitAsync`)
- [Delete a policy](DeletePolicyExample/DeletePolicyExample/DeletePolicy.cs) (`DeletePolicyAsync`)
- [Detach a policy from a target](DetachPolicyExample/DetachPolicyExample/DetachPolicy.cs) (`DetachPolicyAsync`)
- [List accounts](ListAccountsExample/ListAccountsExample/ListAccounts.cs) (`ListAccountsAsync`)
- [List organizational units for parent](ListOrganizationalUnitsForParentExample/ListOrganizationalUnitsForParentExample/ListOrganizationalUnitsForParent.cs) (`ListOrganizationalUnitsAsync`)
- [List policies](ListPoliciesExample/ListPoliciesExample/ListPolicies.cs) (`ListPoliciesAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [AWS Organizations User Guide](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_introduction.html)
* [AWS Organizations API Reference](https://docs.aws.amazon.com/organizations/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Organizations](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Organizations/NOrganizations.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
