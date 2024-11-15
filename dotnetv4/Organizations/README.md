# Organizations code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Organizations.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Organizations consolidates multiple AWS accounts into an organization that you create and centrally manage._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachPolicy](AttachPolicyExample/AttachPolicyExample/AttachPolicy.cs#L6)
- [CreateAccount](CreateAccountExample/CreateAccountExample/CreateAccount.cs#L6)
- [CreateOrganization](CreateOrganizationExample/CreateOrganizationExample/CreateOrganization.cs#L6)
- [CreateOrganizationalUnit](CreateOrganizationalUnitExample/CreateOrganizationalUnitExample/CreateOrganizationalUnit.cs#L6)
- [CreatePolicy](CreatePolicyExample/CreatePolicyExample/CreatePolicy.cs#L6)
- [DeleteOrganization](DeleteOrganizationExample/DeleteOrganizationExample/DeleteOrganization.cs#L6)
- [DeleteOrganizationalUnit](DeleteOrganizationalUnitExample/DeleteOrganizationalUnitExample/DeleteOrganizationalUnit.cs#L6)
- [DeletePolicy](DeletePolicyExample/DeletePolicyExample/DeletePolicy.cs#L6)
- [DetachPolicy](DetachPolicyExample/DetachPolicyExample/DetachPolicy.cs#L6)
- [ListAccounts](ListAccountsExample/ListAccountsExample/ListAccounts.cs#L6)
- [ListOrganizationalUnitsForParent](ListOrganizationalUnitsForParentExample/ListOrganizationalUnitsForParentExample/ListOrganizationalUnitsForParent.cs#L6)
- [ListPolicies](ListPoliciesExample/ListPoliciesExample/ListPolicies.cs#L6)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Organizations User Guide](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_introduction.html)
- [Organizations API Reference](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_introduction.html)
- [SDK for .NET Organizations reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Organizations/NOrganizations.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0