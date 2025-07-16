# Amazon EC2 code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

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

### Get started

- [Hello Amazon EC2](scenario/Package.swift#L8) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](scenario/Sources/entry.swift#L1015)
- [AssociateAddress](scenario/Sources/entry.swift#L1039)
- [AuthorizeSecurityGroupIngress](scenario/Sources/entry.swift#L928)
- [CreateKeyPair](scenario/Sources/entry.swift#L410)
- [CreateSecurityGroup](scenario/Sources/entry.swift#L903)
- [DeleteKeyPair](scenario/Sources/entry.swift#L469)
- [DeleteSecurityGroup](scenario/Sources/entry.swift#L993)
- [DescribeImages](scenario/Sources/entry.swift#L809)
- [DescribeInstanceTypes](scenario/Sources/entry.swift#L537)
- [DescribeKeyPairs](scenario/Sources/entry.swift#L446)
- [DescribeSecurityGroups](hello/Sources/entry.swift#L44)
- [DisassociateAddress](scenario/Sources/entry.swift#L1065)
- [ReleaseAddress](scenario/Sources/entry.swift#L1082)
- [RunInstances](scenario/Sources/entry.swift#L845)
- [StartInstances](scenario/Sources/entry.swift#L711)
- [StopInstances](scenario/Sources/entry.swift#L661)
- [TerminateInstances](scenario/Sources/entry.swift#L760)


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

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Swift Amazon EC2 reference](https://sdk.amazonaws.com/swift/api/awsec2/latest/documentation/awsec2)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
