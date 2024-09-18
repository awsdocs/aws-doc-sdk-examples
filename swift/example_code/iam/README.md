# IAM code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with AWS Identity and Access Management (IAM).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachRolePolicy](AttachRolePolicy/Sources/ServiceHandler/ServiceHandler.swift#L50)
- [CreateAccessKey](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L177)
- [CreatePolicy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L202)
- [CreateRole](CreateRole/Sources/ServiceHandler/ServiceHandler.swift#L50)
- [CreateServiceLinkedRole](CreateServiceLinkedRole/Sources/ServiceHandler/ServiceHandler.swift#L58)
- [CreateUser](CreateUser/Sources/ServiceHandler/ServiceHandler.swift#L49)
- [DeleteAccessKey](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L348)
- [DeletePolicy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L310)
- [DeleteRole](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L374)
- [DeleteUser](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L328)
- [DeleteUserPolicy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L251)
- [DetachRolePolicy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L291)
- [GetPolicy](GetPolicy/Sources/ServiceHandler/ServiceHandler.swift#L49)
- [GetRole](GetRole/Sources/ServiceHandler/ServiceHandler.swift#L50)
- [ListAttachedRolePolicies](basics/Sources/Basics/Basics.swift#L29)
- [ListGroups](basics/Sources/Basics/Basics.swift#L29)
- [ListPolicies](basics/Sources/Basics/Basics.swift#L29)
- [ListRolePolicies](basics/Sources/Basics/Basics.swift#L29)
- [ListRoles](basics/Sources/Basics/Basics.swift#L29)
- [ListUsers](basics/Sources/Basics/Basics.swift#L29)
- [PutUserPolicy](basics/Sources/ServiceHandler/ServiceHandlerIAM.swift#L229)


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