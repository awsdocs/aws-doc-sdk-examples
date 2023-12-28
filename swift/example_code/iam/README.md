# IAM code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with AWS Identity and Access Management (IAM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage permissions in your AWS account._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a policy to a role](AttachRolePolicy/Sources/ServiceHandler/ServiceHandler.swift#L51) (`AttachRolePolicy`)
- [Create a policy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L204) (`CreatePolicy`)
- [Create a role](CreateRole/Sources/ServiceHandler/ServiceHandler.swift#L51) (`CreateRole`)
- [Create a service-linked role](CreateServiceLinkedRole/Sources/ServiceHandler/ServiceHandler.swift#L59) (`CreateServiceLinkedRole`)
- [Create a user](CreateUser/Sources/ServiceHandler/ServiceHandler.swift#L50) (`CreateUser`)
- [Create an access key](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L179) (`CreateAccessKey`)
- [Create an inline policy for a user](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L231) (`PutUserPolicy`)
- [Delete a policy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L312) (`DeletePolicy`)
- [Delete a role](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L376) (`DeleteRole`)
- [Delete a user](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L330) (`DeleteUser`)
- [Delete an access key](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L350) (`DeleteAccessKey`)
- [Delete an inline policy from a user](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L253) (`DeleteUserPolicy`)
- [Detach a policy from a role](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L293) (`DetachRolePolicy`)
- [Get a policy](GetPolicy/Sources/ServiceHandler/ServiceHandler.swift#L50) (`GetPolicy`)
- [Get a role](GetRole/Sources/ServiceHandler/ServiceHandler.swift#L51) (`GetRole`)
- [List groups](ListGroups/Sources/ServiceHandler/ServiceHandler.swift#L41) (`ListGroups`)
- [List inline policies for a role](ListRolePolicies/Sources/ServiceHandler/ServiceHandler.swift#L49) (`ListRolePolicies`)
- [List policies](ListPolicies/Sources/ServiceHandler/ServiceHandler.swift#L49) (`ListPolicies`)
- [List policies attached to a role](ListAttachedRolePolicies/Sources/ServiceHandler/ServiceHandler.swift#L45) (`ListAttachedRolePolicies`)
- [List roles](ListRoles/Sources/ServiceHandler/ServiceHandler.swift#L48) (`ListRoles`)
- [List users](ListUsers/Sources/ServiceHandler/ServiceHandler.swift#L41) (`ListUsers`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
- [IAM API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
- [SDK for Swift IAM reference](https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWSIam/Home)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0