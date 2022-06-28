# Organizations code examples for the AWS SDK for .NET v3

## Overview

This folder contains examples for using the AWS Organizations using the AWS SDK for .NET v3.

AWS Organizations, programmatically manages AWS accounts, allocates resources, and applies policies for governance. Organizations simplifies billing by using a single payment method for all of your accounts.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to
  perform the task. For more information, see Grant least privilege.
- This code is not tested in every AWS Region. For more information, see AWS Regional Services.

## Code examples

### Single actions

- [Attaching a policy](AttachPolicyExample/)
- [Creating an account](CreateAccountExample/)
- [Creating an organizational unit](CreateOrganizationalUnitExample/)
- [Creating an organization](CreateOrganizationExample/)
- [Creating a policy](CreatePolicyExample/)
- [Deleting an organizational unit](DeleteOrganizationalUnitExample/)
- [Deleting an organization](DeleteOrganizationExample/)
- [Deleting a policy](DeletePolicyExample/)
- [Detaching a policy](DetachPolicyExample/)
- [Listing accounts](ListAccountsExample/)
- [Listing organizational units for a parent organization](ListOrgUnitsForParentExample/)
- [Listing policies](ListPoliciesExample/)

## Running the examples

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
